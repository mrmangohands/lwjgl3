/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package core.linux.liburing.templates

import org.lwjgl.generator.*
import core.linux.*
import core.linux.liburing.*

val LibIOURing = "LibIOURing".nativeClass(Module.CORE_LINUX_LIBURING, nativeSubPath = "linux", prefixConstant = "IORING_", prefixMethod = "io_uring_") {
    nativeImport(
        "liburing/io_uring.h",
        "syscall.h"
    )
    documentation =
        """
        Native bindings to ${url("https://github.com/axboe/liburing", "io_uring")}, a Linux-specific API for asynchronous I/O.

        It allows the user to submit one or more I/O requests, which are processed asynchronously without blocking the calling process. {@code io_uring} gets
        its name from ring buffers which are shared between user space and kernel space. This arrangement allows for efficient I/O, while avoiding the overhead
        of copying buffers between them, where possible. This interface makes {@code io_uring} different from other UNIX I/O APIs, wherein, rather than just
        communicate between kernel and user space with system calls, ring buffers are used as the main mode of communication. This arrangement has various
        performance benefits which are discussed in a separate section below. This man page uses the terms shared buffers, shared ring buffers and queues
        interchangeably.

        The general programming model you need to follow for io_uring is outlined below
        ${ul(
            """
            Set up shared buffers with #setup() and {@code mmap(2)}, mapping into user space shared buffers for the submission queue (SQ) and the completion
            queue (CQ). You place I/O requests you want to make on the SQ, while the kernel places the results of those operations on the CQ.
            """,
            """
            For every I/O request you need to make (like to read a file, write a file, accept a socket connection, etc), you create a submission queue entry,
            or SQE, describe the I/O operation you need to get done and add it to the tail of the submission queue (SQ). Each I/O operation is, in essence, the
            equivalent of a system call you would have made otherwise, if you were not using {@code io_uring}. You can add more than one SQE to the queue
            depending on the number of operations you want to request.
            """,
            "After you add one or more SQEs, you need to call #enter() to tell the kernel to dequeue your I/O requests off the SQ and begin processing them.",
            """
            For each SQE you submit, once it is done processing the request, the kernel places a completion queue event or CQE at the tail of the completion
            queue or CQ. The kernel places exactly one matching CQE in the CQ for every SQE you submit on the SQ. After you retrieve a CQE, minimally, you
            might be interested in checking the res field of the CQE structure, which corresponds to the return value of the system call's equivalent, had you
            used it directly without using {@code io_uring}. For instance, a read operation under {@code io_uring}, started with the #OP_READ operation, which
            issues the equivalent of the {@code read(2)} system call, would return as part of {@code res} what {@code read(2)} would have returned if called
            directly, without using {@code io_uring}.
            """,
            """
            Optionally, #enter() can also wait for a specified number of requests to be processed by the kernel before it returns. If you specified a certain
            number of completions to wait for, the kernel would have placed at least those many number of CQEs on the CQ, which you can then readily read,
            right after the return from {@code io_uring_enter(2)}.
            """,
            """
            It is important to remember that I/O requests submitted to the kernel can complete in any order. It is not necessary for the kernel to process one
            request after another, in the order you placed them. Given that the interface is a ring, the requests are attempted in order, however that doesn't
            imply any sort of ordering on their completion. When more than one request is in flight, it is not possible to determine which one will complete
            first. When you dequeue CQEs off the CQ, you should always check which submitted request it corresponds to. The most common method for doing so is
            utilizing the {@code user_data} field in the request, which is passed back on the completion side.
            """
        )}

        Adding to and reading from the queues:
        ${ul(
            "You add SQEs to the tail of the SQ. The kernel reads SQEs off the head of the queue.",
            "The kernel adds CQEs to the tail of the CQ. You read CQEs off the head of the queue."
        )}

        <h3>Submission queue polling</h3>

        One of the goals of {@code io_uring} is to provide a means for efficient I/O. To this end, {@code io_uring} supports a polling mode that lets you avoid
        the call to #enter(), which you use to inform the kernel that you have queued SQEs on to the SQ. With SQ Polling, {@code io_uring} starts a kernel
        thread that polls the submission queue for any I/O requests you submit by adding SQEs. With SQ Polling enabled, there is no need for you to call
        {@code io_uring_enter(2)}, letting you avoid the overhead of system calls. A designated kernel thread dequeues SQEs off the SQ as you add them and
        dispatches them for asynchronous processing.

        <h3>Setting up io_uring</h3>

        The main steps in setting up {@code io_uring} consist of mapping in the shared buffers with {@code mmap(2)} calls.

        <h3>Submitting I/O requests</h3>

        The process of submitting a request consists of describing the I/O operation you need to get done using an {@code io_uring_sqe} structure instance.
        These details describe the equivalent system call and its parameters. Because the range of I/O operations Linux supports are very varied and the
        {@code io_uring_sqe} structure needs to be able to describe them, it has several fields, some packed into unions for space efficiency.

        To submit an I/O request to {@code io_uring}, you need to acquire a submission queue entry (SQE) from the submission queue (SQ), fill it up with
        details of the operation you want to submit and call #enter(). If you want to avoid calling {@code io_uring_enter(2)}, you have the option of setting
        up Submission Queue Polling.

        SQEs are added to the tail of the submission queue. The kernel picks up SQEs off the head of the SQ. The general algorithm to get the next available
        SQE and update the tail is as follows.
        ${codeBlock("""
struct io_uring_sqe *sqe;
unsigned tail, index;
tail = *sqring->tail;
index = tail & (*sqring->ring_mask);
sqe = &sqring->sqes[index];
// fill up details about this I/O request
describe_io(sqe);
// fill the sqe index into the SQ ring array
sqring->array[index] = index;
tail++;
atomic_store_release(sqring->tail, tail);""")}

        To get the index of an entry, the application must mask the current tail index with the size mask of the ring. This holds true for both SQs and CQs.
        Once the SQE is acquired, the necessary fields are filled in, describing the request. While the CQ ring directly indexes the shared array of CQEs, the
        submission side has an indirection array between them. The submission side ring buffer is an index into this array, which in turn contains the index
        into the SQEs.

        The following code snippet demonstrates how a read operation, an equivalent of a preadv2(2) system call is described by filling up an SQE with the
        necessary parameters.
        ${codeBlock("""
struct iovec iovecs[16];
 ...
sqe->opcode = IORING_OP_READV;
sqe->fd = fd;
sqe->addr = (unsigned long) iovecs;
sqe->len = 16;
sqe->off = offset;
sqe->flags = 0;""")}

        <h4>Memory ordering</h4>

        Modern compilers and CPUs freely reorder reads and writes without affecting the program's outcome to optimize performance. Some aspects of this need to
        be kept in mind on SMP systems since {@code io_uring} involves buffers shared between kernel and user space. These buffers are both visible and
        modifiable from kernel and user space. As heads and tails belonging to these shared buffers are updated by kernel and user space, changes need to be
        coherently visible on either side, irrespective of whether a CPU switch took place after the kernel-user mode switch happened. We use memory barriers
        to enforce this coherency. Being significantly large subjects on their own, memory barriers are out of scope for further discussion on this man page.

        <h4>Letting the kernel know about I/O submissions</h4>

        Once you place one or more SQEs on to the SQ, you need to let the kernel know that you've done so. You can do this by calling the #enter() system call.
        This system call is also capable of waiting for a specified count of events to complete. This way, you can be sure to find completion events in the
        completion queue without having to poll it for events later.

        <h3>Reading completion events</h3>

        Similar to the submission queue (SQ), the completion queue (CQ) is a shared buffer between the kernel and user space. Whereas you placed submission
        queue entries on the tail of the SQ and the kernel read off the head, when it comes to the CQ, the kernel places completion queue events or CQEs on the
        tail of the CQ and you read off its head.

        Submission is flexible (and thus a bit more complicated) since it needs to be able to encode different types of system calls that take various
        parameters. Completion, on the other hand is simpler since we're looking only for a return value back from the kernel. This is easily understood by
        looking at the completion queue event structure, ##IOURingCQE.

        Here, {@code user_data} is custom data that is passed unchanged from submission to completion. That is, from SQEs to CQEs. This field can be used to
        set context, uniquely identifying submissions that got completed. Given that I/O requests can complete in any order, this field can be used to
        correlate a submission with a completion. {@code res} is the result from the system call that was performed as part of the submission; its return
        value. The {@code flags} field could carry request-specific metadata in the future, but is currently unused.

        The general sequence to read completion events off the completion queue is as follows:
        ${codeBlock("""
unsigned head;
head = *cqring->head;
if (head != atomic_load_acquire(cqring->tail)) {
    struct io_uring_cqe *cqe;

    unsigned index;

    index = head & (cqring->mask);

    cqe = &cqring->cqes[index];

    // process completed CQE

    process_cqe(cqe);

    // CQE consumption complete

    head++;
}
atomic_store_release(cqring->head, head);""")}

        It helps to be reminded that the kernel adds CQEs to the tail of the CQ, while you need to dequeue them off the head. To get the index of an entry at
        the head, the application must mask the current head index with the size mask of the ring. Once the CQE has been consumed or processed, the head needs
        to be updated to reflect the consumption of the CQE. Attention should be paid to the read and write barriers to ensure successful read and update of
        the head.

        <h3>io_uring performance</h3>

        Because of the shared ring buffers between kernel and user space, {@code io_uring} can be a zero-copy system. Copying buffers to and from becomes
        necessary when system calls that transfer data between kernel and user space are involved. But since the bulk of the communication in {@code io_uring}
        is via buffers shared between the kernel and user space, this huge performance overhead is completely avoided.

        While system calls may not seem like a significant overhead, in high performance applications, making a lot of them will begin to matter. While
        workarounds the operating system has in place to deal with Spectre and Meltdown are ideally best done away with, unfortunately, some of these
        workarounds are around the system call interface, making system calls not as cheap as before on affected hardware. While newer hardware should not need
        these workarounds, hardware with these vulnerabilities can be expected to be in the wild for a long time. While using synchronous programming
        interfaces or even when using asynchronous programming interfaces under Linux, there is at least one system call involved in the submission of each
        request. In {@code io_uring}, on the other hand, you can batch several requests in one go, simply by queueing up multiple SQEs, each describing an I/O
        operation you want and make a single call to #enter(). This is possible due to {@code io_uring}'s shared buffers based design.

        While this batching in itself can avoid the overhead associated with potentially multiple and frequent system calls, you can reduce even this overhead
        further with Submission Queue Polling, by having the kernel poll and pick up your SQEs for processing as you add them to the submission queue. This
        avoids the {@code io_uring_enter(2)} call you need to make to tell the kernel to pick SQEs up. For high-performance applications, this means even
        lesser system call overheads.
        """

    IntConstant("", "MAX_ENTRIES".."4096")

    EnumConstant(
        "{@code io_uring_sqe->flags} bits",

        "IOSQE_FIXED_FILE_BIT".enum("", "0"),
        "IOSQE_IO_DRAIN_BIT".enum,
        "IOSQE_IO_LINK_BIT".enum,
        "IOSQE_IO_HARDLINK_BIT".enum,
        "IOSQE_ASYNC_BIT".enum,
        "IOSQE_BUFFER_SELECT_BIT".enum,
        "IOSQE_CQE_SKIP_SUCCESS_BIT".enum
    ).noPrefix()

    EnumConstant(
        "{@code io_uring_sqe->flags} bitfield values",

        "IOSQE_FIXED_FILE".enum(
            """
            When this flag is specified, {@code fd} is an index into the files array registered with the {@code io_uring} instance (see the #REGISTER_FILES
            section of the #register() man page).

            Note that this isn't always available for all commands. If used on a command that doesn't support fixed files, the SQE will error with
            {@code -EBADF}.

            Available since 5.1.
            """,
            "1 << IOSQE_FIXED_FILE_BIT"),
        "IOSQE_IO_DRAIN".enum(
            """
            When this flag is specified, the SQE will not be started before previously submitted SQEs have completed, and new SQEs will not be started before
            this one completes.

            Available since 5.2.
            """,
            "1 << IOSQE_IO_DRAIN_BIT"
        ),
        "IOSQE_IO_LINK".enum(
            """
            When this flag is specified, it forms a link with the next SQE in the submission ring.

            That next SQE will not be started before this one completes. This, in effect, forms a chain of SQEs, which can be arbitrarily long. The tail of the
            chain is denoted by the first SQE that does not have this flag set. This flag has no effect on previous SQE submissions, nor does it impact SQEs
            that are outside of the chain tail. This means that multiple chains can be executing in parallel, or chains and individual SQEs. Only members
            inside the chain are serialized. A chain of SQEs will be broken, if any request in that chain ends in error. {@code io_uring} considers any
            unexpected result an error. This means that, eg, a short read will also terminate the remainder of the chain. If a chain of SQE links is broken,
            the remaining unstarted part of the chain will be terminated and completed with {@code -ECANCELED} as the error code.

            Available since 5.3.
            """,
            "1 << IOSQE_IO_LINK_BIT"
        ),
        "IOSQE_IO_HARDLINK".enum(
            """
            Like #IOSQE_IO_LINK, but it doesn't sever regardless of the completion result.

            Note that the link will still sever if we fail submitting the parent request, hard links are only resilient in the presence of completion results
            for requests that did submit correctly. {@code IOSQE_IO_HARDLINK} implies {@code IOSQE_IO_LINK}.

            Available since 5.5.
            """,
            "1 << IOSQE_IO_HARDLINK_BIT"
        ),
        "IOSQE_ASYNC".enum(
            """
            Normal operation for {@code io_uring} is to try and issue an sqe as non-blocking first, and if that fails, execute it in an async manner.

            To support more efficient overlapped operation of requests that the application knows/assumes will always (or most of the time) block, the
            application can ask for an sqe to be issued async from the start.

            Available since 5.6.
            """,
            "1 << IOSQE_ASYNC_BIT"
        ),
        "IOSQE_BUFFER_SELECT".enum(
            """
            Used in conjunction with the #OP_PROVIDE_BUFFERS command, which registers a pool of buffers to be used by commands that read or receive data.

            When buffers are registered for this use case, and this flag is set in the command, {@code io_uring} will grab a buffer from this pool when the
            request is ready to receive or read data. If successful, the resulting CQE will have #CQE_F_BUFFER set in the flags part of the struct, and the
            upper #CQE_BUFFER_SHIFT bits will contain the ID of the selected buffers. This allows the application to know exactly which buffer was selected for
            the operation. If no buffers are available and this flag is set, then the request will fail with {@code -ENOBUFS} as the error code. Once a buffer
            has been used, it is no longer available in the kernel pool. The application must re-register the given buffer again when it is ready to recycle it
            (eg has completed using it).

            Available since 5.7.
            """,
            "1 << IOSQE_BUFFER_SELECT_BIT"
        ),
        "IOSQE_CQE_SKIP_SUCCESS".enum(
            """
            Don't generate a CQE if the request completes successfully.
            
            If the request fails, an appropriate CQE will be posted as usual and if there is no #IOSQE_IO_HARDLINK, CQEs for all linked requests will be
            omitted. The notion of failure/success is {@code opcode} specific and is the same as with breaking chains of #IOSQE_IO_LINK. One special case is
            when the request has a linked timeout, then the CQE generation for the linked timeout is decided solely by whether it has
            {@code IOSQE_CQE_SKIP_SUCCESS} set, regardless whether it timed out or was cancelled. In other words, if a linked timeout has the flag set, it's
            guaranteed to not post a CQE.

            The semantics are chosen to accommodate several use cases. First, when all but the last request of a normal link without linked timeouts are marked
            with the flag, only one CQE per link is posted. Additionally, it enables supression of CQEs in cases where the side effects of a successfully
            executed operation is enough for userspace to know the state of the system. One such example would be writing to a synchronisation file.

            This flag is incompatible with #IOSQE_IO_DRAIN. Using both of them in a single ring is undefined behavior, even when they are not used together in
            a single request. Currently, after the first request with {@code IOSQE_CQE_SKIP_SUCCESS}, all subsequent requests marked with drain will be failed
            at submission time. Note that the error reporting is best effort only, and restrictions may change in the future.

            Available since 5.17.
            """,
            "1 << IOSQE_CQE_SKIP_SUCCESS_BIT"
        )
    ).noPrefix()

    EnumConstant(
        "{@code io_uring_setup()} flags",

        "SETUP_IOPOLL".enum(
            """
            Perform busy-waiting for an I/O completion, as opposed to getting notifications via an asynchronous IRQ (Interrupt Request).

            The file system (if any) and block device must support polling in order for this to work. Busy-waiting provides lower latency, but may consume more
            CPU resources than interrupt driven I/O. Currently, this feature is usable only on a file descriptor opened using the {@code O_DIRECT} flag. When a
            read or write is submitted to a polled context, the application must poll for completions on the CQ ring by calling #enter(). It is illegal to mix
            and match polled and non-polled I/O on an io_uring instance.
            """,
            "1 << 0"
        ),
        "SETUP_SQPOLL".enum(
            """
            When this flag is specified, a kernel thread is created to perform submission queue polling.

            An {@code io_uring} instance configured in this way enables an application to issue I/O without ever context switching into the kernel. By using
            the submission queue to fill in new submission queue entries and watching for completions on the completion queue, the application can submit and
            reap I/Os without doing a single system call.

            If the kernel thread is idle for more than {@code sq_thread_idle} milliseconds, it will set the #SQ_NEED_WAKEUP bit in the flags field of the
            struct {@code io_sq_ring}. When this happens, the application must call #enter() to wake the kernel thread. If I/O is kept busy, the kernel thread
            will never sleep. An application making use of this feature will need to guard the {@code io_uring_enter()} call with the following code sequence:
            ${codeBlock("""
// Ensure that the wakeup flag is read after the tail pointer
// has been written. It's important to use memory load acquire
// semantics for the flags read, as otherwise the application
// and the kernel might not agree on the consistency of the
// wakeup flag.
unsigned flags = atomic_load_relaxed(sq_ring->flags);
if (flags & IORING_SQ_NEED_WAKEUP)
    io_uring_enter(fd, 0, 0, IORING_ENTER_SQ_WAKEUP);""")}

            where {@code sq_ring} is a submission queue ring setup using the struct {@code io_sqring_offsets} described below.

            Before version 5.11 of the Linux kernel, to successfully use this feature, the application must register a set of files to be used for IO through
            #register() using the #REGISTER_FILES opcode. Failure to do so will result in submitted IO being errored with {@code EBADF}. The presence of this
            feature can be detected by the #FEAT_SQPOLL_NONFIXED feature flag. In version 5.11 and later, it is no longer necessary to register files to use
            this feature. 5.11 also allows using this as non-root, if the user has the {@code CAP_SYS_NICE} capability.
            """,
            "1 << 1"
        ),
        "SETUP_SQ_AFF".enum(
            """
            If this flag is specified, then the poll thread will be bound to the cpu set in the {@code sq_thread_cpu} field of the struct
            {@code io_uring_params}. This flag is only meaningful when #SETUP_SQPOLL is specified. When {@code cgroup} setting {@code cpuset.cpus} changes
            (typically in container environment), the bounded cpu set may be changed as well.
            """,
            "1 << 2"
        ),
        "SETUP_CQSIZE".enum(
            """
            Create the completion queue with struct {@code io_uring_params.cq_entries} entries.

            The value must be greater than entries, and may be rounded up to the next power-of-two.
            """,
            "1 << 3"
        ),
        "SETUP_CLAMP".enum(
            """
            If this flag is specified, and if entries exceeds #MAX_ENTRIES, then entries will be clamped at {@code IORING_MAX_ENTRIES}.

            If the flag #SETUP_SQPOLL is set, and if the value of struct {@code io_uring_params.cq_entries} exceeds {@code IORING_MAX_CQ_ENTRIES}, then it will
            be clamped at {@code IORING_MAX_CQ_ENTRIES}.
            """,
            "1 << 4"
        ),
        "SETUP_ATTACH_WQ".enum(
            """
            This flag should be set in conjunction with struct {@code io_uring_params.wq_fd} being set to an existing {@code io_uring} ring file descriptor.

            When set, the {@code io_uring} instance being created will share the asynchronous worker thread backend of the specified {@code io_uring} ring,
            rather than create a new separate thread pool.
            """,
            "1 << 5"
        ),
        "SETUP_R_DISABLED".enum(
            """
            If this flag is specified, the io_uring ring starts in a disabled state.

            In this state, restrictions can be registered, but submissions are not allowed. See #register() for details on how to enable the ring. Available
            since 5.10.
            """,
            "1 << 6"
        )
    )

    EnumConstantByte(
        "",

        "OP_NOP".enumByte("Do not perform any I/O. This is useful for testing the performance of the {@code io_uring} implementation itself.", "0"),
        "OP_READV".enumByte("Vectored read operation, similar to {@code preadv2(2)}. If the file is not seekable, {@code off} must be set to zero."),
        "OP_WRITEV".enumByte("Vectored write operation, similar to {@code pwritev2(2)}. If the file is not seekable, {@code off} must be set to zero."),
        "OP_FSYNC".enumByte(
            """
            File sync. See also {@code fsync(2)}.

            Note that, while I/O is initiated in the order in which it appears inthe submission queue, completions are unordered. For example, an application
            which places a write I/O followed by an fsync in the submission queue cannot expect the fsync to apply to the write. The two operations execute in
            parallel, so the fsync may complete before the write is issued to the storage. The same is also true for previously issued writes that have not
            completed prior to the fsync.
            """
        ),
        "OP_READ_FIXED".enumByte("Read from pre-mapped buffers. See #register() for details on how to setup a context for fixed reads."),
        "OP_WRITE_FIXED".enumByte("Write to pre-mapped buffers. See #register() for details on how to setup a context for fixed writes."),
        "OP_POLL_ADD".enumByte(
            """
            Poll the {@code fd} specified in the submission queue entry for the events specified in the {@code poll_events} field.

            Unlike poll or epoll without {@code EPOLLONESHOT}, by default this interface always works in one shot mode. That is, once the poll operation is
            completed, it will have to be resubmitted.

            If #POLL_ADD_MULTI is set in the SQE {@code len} field, then the poll will work in multi shot mode instead. That means it'll repatedly trigger when
            the requested event becomes true, and hence multiple CQEs can be generated from this single SQE. The CQE {@code flags} field will have #CQE_F_MORE
            set on completion if the application should expect further CQE entries from the original request. If this flag isn't set on completion, then the
            poll request has been terminated and no further events will be generated. This mode is available since 5.13.

            If #POLL_UPDATE_EVENTS is set in the SQE {@code len} field, then the request will update an existing poll request with the mask of events passed in
            with this request. The lookup is based on the {@code user_data} field of the original SQE submitted, and this values is passed in the {@code addr}
            field of the SQE. This mode is available since 5.13.

            If #POLL_UPDATE_USER_DATA is set in the SQE {@code len} field, then the request will update the {@code user_data} of an existing poll request based
            on the value passed in the {@code off} field. This mode is available since 5.13.

            This command works like an {@code asyncpoll(2)} and the completion event result is the returned mask of events. For the variants that update
            {@code user_data} or {@code events}, the completion result will be similar to #OP_POLL_REMOVE.
            """
        ),
        "OP_POLL_REMOVE".enumByte(
            """
            Remove an existing poll request.

            If found, the {@code res} field of the struct {@code io_uring_cqe} will contain 0. If not found, {@code res} will contain {@code -ENOENT}, or
            {@code -EALREADY} if the poll request was in the process of completing already.
            """
        ),
        "OP_SYNC_FILE_RANGE".enumByte(
            """
            Issue the equivalent of a {@code sync_file_range(2)} on the file descriptor.

            The {@code fd} field is the file descriptor to sync, the {@code off} field holds the offset in bytes, the {@code len} field holds the length in
            bytes, and the {@code sync_range_flags} field holds the flags for the command. See also {@code sync_file_range(2)} for the general description of
            the related system call.

            Available since 5.2.
            """
        ),
        "OP_SENDMSG".enumByte(
            """
            Issue the equivalent of a {@code sendmsg(2)} system call.

            {@code fd} must be set to the socket file descriptor, {@code addr} must contain a pointer to the {@code msghdr} structure, and {@code msg_flags}
            holds the flags associated with the system call. See also {@code sendmsg(2)} for the general description of the related system call.

            Available since 5.3.
            """
        ),
        "OP_RECVMSG".enumByte(
            """
            Works just like #OP_SENDMSG, except for {@code recvmsg(2)} instead. See the description of {@code IORING_OP_SENDMSG}.

            Available since 5.3.
            """
        ),
        "OP_TIMEOUT".enumByte(
            """
            This command will register a timeout operation.

            The {@code addr} field must contain a pointer to a {@code struct timespec64} structure, {@code len} must contain 1 to signify one
            {@code timespec64} structure, {@code timeout_flags} may contain #TIMEOUT_ABS for an absolute timeout value, or 0 for a relative timeout.
            {@code off} may contain a completion event count. A timeout will trigger a wakeup event on the completion ring for anyone waiting for events. A
            timeout condition is met when either the specified timeout expires, or the specified number of events have completed. Either condition will trigger
            the event. If set to 0, completed events are not counted, which effectively acts like a timer. {@code io_uring} timeouts use the
            {@code CLOCK_MONOTONIC} clock source. The request will complete with {@code -ETIME} if the timeout got completed through expiration of the timer,
            or 0 if the timeout got completed through requests completing on their own. If the timeout was cancelled before it expired, the request will
            complete with {@code -ECANCELED}.

            Available since 5.4.

            Since 5.15, this command also supports the following modifiers in {@code timeout_flags}:
            ${ul(
                """
                #TIMEOUT_BOOTTIME: If set, then the clock source used is {@code CLOCK_BOOTTIME} instead of {@code CLOCK_MONOTONIC}. This clock source differs
                in that it includes time elapsed if the system was suspend while having a timeout request in-flight.
                """,
                "#TIMEOUT_REALTIME: If set, then the clock source used is {@code CLOCK_BOOTTIME} instead of {@code CLOCK_MONOTONIC}."
            )}
            """
        ),
        "OP_TIMEOUT_REMOVE".enumByte(
            """
            If {@code timeout_flags} are zero, then it attempts to remove an existing timeout operation. {@code addr} must contain the {@code user_data} field
            of the previously issued timeout operation. If the specified timeout request is found and cancelled successfully, this request will terminate with
            a result value of 0. If the timeout request was found but expiration was already in progress, this request will terminate with a result value of
            {@code -EBUSY}. If the timeout request wasn't found, the request will terminate with a result value of {@code -ENOENT}.

            Available since 5.5.

            If {@code timeout_flags} contain #TIMEOUT_UPDATE, instead of removing an existing operation, it updates it. {@code addr} and return values are same
            as before. {@code addr2} field must contain a pointer to a {@code struct timespec64} structure. {@code timeout_flags} may also contain
            #TIMEOUT_ABS, in which case the value given is an absolute one, not a relative one.

            Available since 5.11.
            """
        ),
        "OP_ACCEPT".enumByte(
            """
            Issue the equivalent of an {@code accept4(2)} system call.

            {@code fd} must be set to the socket file descriptor, {@code addr} must contain the pointer to the {@code sockaddr} structure, and {@code addr2}
            must contain a pointer to the {@code socklen_t} {@code addrlen} field. Flags can be passed using the {@code accept_flags} field. See also
            {@code accept4(2)} for the general description of the related system call.

            Available since 5.5.

            If the {@code file_index} field is set to a positive number, the file won't be installed into the normal file table as usual but will be placed
            into the fixed file table at index {@code file_index - 1}. In this case, instead of returning a file descriptor, the result will contain either 0
            on success or an error. If the index points to a valid empty slot, the installation is guaranteed to not fail. If there is already a file in the
            slot, it will be replaced, similar to #OP_FILES_UPDATE. Please note that only {@code io_uring} has access to such files and no other syscall can
            use them. See #IOSQE_FIXED_FILE and #REGISTER_FILES.

            Available since 5.15.
            """
        ),
        "OP_ASYNC_CANCEL".enumByte(
            """
            Attempt to cancel an already issued request.

            {@code addr} must contain the {@code user_data} field of the request that should be cancelled. The cancellation request will complete with one of
            the following results codes. If found, the {@code res} field of the cqe will contain 0. If not found, {@code res} will contain {@code -ENOENT}. If
            found and attempted cancelled, the {@code res} field will contain {@code -EALREADY}. In this case, the request may or may not terminate. In
            general, requests that are interruptible (like socket IO) will get cancelled, while disk IO requests cannot be cancelled if already started.

            Available since 5.5.
            """
        ),
        "OP_LINK_TIMEOUT".enumByte(
            """
            This request must be linked with another request through #IOSQE_IO_LINK which is described below.

            Unlike #OP_TIMEOUT, {@code IORING_OP_LINK_TIMEOUT} acts on the linked request, not the completion queue. The format of the command is otherwise
            like {@code IORING_OP_TIMEOUT}, except there's no completion event count as it's tied to a specific request. If used, the timeout specified in the
            command will cancel the linked command, unless the linked command completes before the timeout. The timeout will complete with {@code -ETIME} if
            the timer expired and the linked request was attempted cancelled, or {@code -ECANCELED} if the timer got cancelled because of completion of the
            linked request. Like {@code IORING_OP_TIMEOUT} the clock source used is {@code CLOCK_MONOTONIC}.

            Available since 5.5.
            """
        ),
        "OP_CONNECT".enumByte(
            """
            Issue the equivalent of a {@code connect(2)} system call.

            {@code fd} must be set to the socket file descriptor, {@code addr} must contain the const pointer to the {@code sockaddr} structure, and
            {@code off} must contain the {@code socklen_t} {@code addrlen} field. See also {@code connect(2)} for the general description of the related system
            call.

            Available since 5.5.
            """
        ),
        "OP_FALLOCATE".enumByte(
            """
            Issue the equivalent of a {@code fallocate(2)} system call.

            {@code fd} must be set to the file descriptor, {@code len} must contain the mode associated with the operation, {@code off} must contain the offset
            on which to operate, and {@code addr} must contain the length. See also {@code fallocate(2)} for the general description of the related system
            call.

            Available since 5.6.
            """
        ),
        "OP_OPENAT".enumByte(
            """
            Issue the equivalent of a {@code openat(2)} system call.

            {@code fd} is the {@code dirfd} argument, {@code addr} must contain a pointer to the {@code *pathname} argument, {@code open_flags} should contain
            any flags passed in, and {@code len} is access mode of the file. See also {@code openat(2)} for the general description of the related system call.

            Available since 5.6.

            If the {@code file_index} field is set to a positive number, the file won't be installed into the normal file table as usual but will be placed
            into the fixed file table at index {@code file_index - 1}. In this case, instead of returning a file descriptor, the result will contain either 0
            on success or an error. If there is already a file registered at this index, the request will fail with {@code -EBADF}. Only {@code io_uring} has
            access to such files and no other syscall can use them. See #IOSQE_FIXED_FILE and #REGISTER_FILES.

            Available since 5.15.
            """
        ),
        "OP_CLOSE".enumByte(
            """
            Issue the equivalent of a {@code close(2)} system call.

            {@code fd} is the file descriptor to be closed. See also {@code close(2)} for the general description of the related system call.

            Available since 5.6.

            If the {@code file_index} field is set to a positive number, this command can be used to close files that were direct opened through #OP_OPENAT,
            #OP_OPENAT2, or #OP_ACCEPT using the {@code io_uring} specific direct descriptors. Note that only one of the descriptor fields may be set. The
            direct close feature is available since the 5.15 kernel, where direct descriptors were introduced.
            """
        ),
        "OP_FILES_UPDATE".enumByte(
            """
            This command is an alternative to using #REGISTER_FILES_UPDATE which then works in an async fashion, like the rest of the {@code io_uring}
            commands.

            The arguments passed in are the same. {@code addr} must contain a pointer to the array of file descriptors, {@code len} must contain the length of
            the array, and {@code off} must contain the offset at which to operate. Note that the array of file descriptors pointed to in {@code addr} must
            remain valid until this operation has completed.

            Available since 5.6.
            """
        ),
        "OP_STATX".enumByte(
            """
            Issue the equivalent of a {@code statx(2)} system call.

            {@code fd} is the {@code dirfd} argument, {@code addr} must contain a pointer to the {@code *pathname} string, {@code statx_flags} is the
            {@code flags} argument, {@code len} should be the {@code mask} argument, and {@code off} must contain a pointer to the {@code statxbuf} to be
            filled in. See also {@code statx(2)} for the general description of the related system call.

            Available since 5.6.
            """
        ),
        "OP_READ".enumByte(
            """
            Issue the equivalent of a {@code pread(2)} or {@code pwrite(2)} system call.

            {@code fd} is the file descriptor to be operated on, {@code addr} contains the buffer in question, {@code len} contains the length of the IO
            operation, and {@code offs} contains the read or write offset. If {@code fd} does not refer to a seekable file, {@code off} must be set to zero.
            If {@code offs} is set to -1, the offset will use (and advance) the file position, like the {@code read(2)} and {@code write(2)} system calls.
            These are non-vectored versions of the #OP_READV and #OP_WRITEV opcodes. See also {@code read(2)} and {@code write(2)} for the general description
            of the related system call.

            Available since 5.6.
            """
        ),
        "OP_WRITE".enumByte("See #OP_READ."),
        "OP_FADVISE".enumByte(
            """
            Issue the equivalent of a {@code posix_fadvise(2)} system call.

            {@code fd} must be set to the file descriptor, {@code off} must contain the offset on which to operate, {@code len} must contain the length, and
            {@code fadvise_advice} must contain the advice associated with the operation. See also {@code posix_fadvise(2)} for the general description of the
            related system call.

            Available since 5.6.
            """
        ),
        "OP_MADVISE".enumByte(
            """
            Issue the equivalent of a {@code madvise(2)} system call.

            {@code addr} must contain the address to operate on, {@code len} must contain the length on which to operate, and {@code fadvise_advice} must
            contain the advice associated with the operation. See also {@code madvise(2)} for the general description of the related system call.

            Available since 5.6.
            """
        ),
        "OP_SEND".enumByte(
            """
            Issue the equivalent of a {@code send(2)} system call.

            {@code fd} must be set to the socket file descriptor, {@code addr} must contain a pointer to the buffer, {@code len} denotes the length of the
            buffer to send, and {@code msg_flags} holds the flags associated with the system call. See also {@code send(2)} for the general description of the
            related system call.

            Available since 5.6.
            """
        ),
        "OP_RECV".enumByte(
            """
            Works just like #OP_SEND, except for {@code recv(2)} instead. See the description of {@code IORING_OP_SEND}.

            Available since 5.6.
            """
        ),
        "OP_OPENAT2".enumByte(
            """
            Issue the equivalent of a {@code openat2(2)} system call.

            {@code fd} is the {@code dirfd} argument, {@code addr} must contain a pointer to the {@code *pathname} argument, {@code len} should contain the
            size of the {@code open_how} structure, and {@code off} should be set to the address of the {@code open_how} structure. See also {@code openat2(2)}
            for the general description of the related system call.

            Available since 5.6.

            If the {@code file_index} field is set to a positive number, the file won't be installed into the normal file table as usual but will be placed
            into the fixed file table at index {@code file_index - 1}. In this case, instead of returning a file descriptor, the result will contain either 0
            on success or an error. If there is already a file registered at this index, the request will fail with {@code -EBADF}. Only {@code io_uring} has
            access to such files and no other syscall can use them. See #IOSQE_FIXED_FILE and #REGISTER_FILES.

            Available since 5.15.
            """
        ),
        "OP_EPOLL_CTL".enumByte(
            """
            Add, remove or modify entries in the interest list of {@code epoll(7)}. See {@code epoll_ctl(2)} for details of the system call.

            {@code fd} holds the file descriptor that represents the epoll instance, {@code addr} holds the file descriptor to add, remove or modify,
            {@code len} holds the operation ({@code EPOLL_CTL_ADD}, {@code EPOLL_CTL_DEL}, {@code EPOLL_CTL_MOD}) to perform and, {@code off} holds a pointer
            to the {@code epoll_events} structure.

            Available since 5.6.
            """
        ),
        "OP_SPLICE".enumByte(
            """
            Issue the equivalent of a {@code splice(2)} system call.

            {@code splice_fd_in} is the file descriptor to read from, {@code splice_off_in} is an offset to read from, {@code fd} is the file descriptor to
            write to, {@code off} is an offset from which to start writing to. A sentinel value of -1 is used to pass the equivalent of a #NULL for the offsets
            to {@code splice(2)}. {@code len} contains the number of bytes to copy. {@code splice_flags} contains a bit mask for the flag field associated with
            the system call. Please note that one of the file descriptors must refer to a pipe. See also {@code splice(2)} for the general description of the
            related system call.

            Available since 5.7.
            """
        ),
        "OP_PROVIDE_BUFFERS".enumByte(
            """
            This command allows an application to register a group of buffers to be used by commands that read/receive data.

            Using buffers in this manner can eliminate the need to separate the poll + read, which provides a convenient point in time to allocate a buffer for
            a given request. It's often infeasible to have as many buffers available as pending reads or receive. With this feature, the application can have
            its pool of buffers ready in the kernel, and when the file or socket is ready to read/receive data, a buffer can be selected for the operation.

            {@code fd} must contain the number of buffers to provide, {@code addr} must contain the starting address to add buffers from, {@code len} must
            contain the length of each buffer to add from the range, {@code buf_group} must contain the group ID of this range of buffers, and {@code off} must
            contain the starting buffer ID of this range of buffers. With that set, the kernel adds buffers starting with the memory address in {@code addr},
            each with a length of {@code len}. Hence the application should provide {@code len * fd} worth of memory in {@code addr}. Buffers are grouped by
            the group ID, and each buffer within this group will be identical in size according to the above arguments. This allows the application to provide
            different groups of buffers, and this is often used to have differently sized buffers available depending on what the expectations are of the
            individual request. When submitting a request that should use a provided buffer, the #IOSQE_BUFFER_SELECT flag must be set, and {@code buf_group}
            must be set to the desired buffer group ID where the buffer should be selected from.

            Available since 5.7.
            """
        ),
        "OP_REMOVE_BUFFERS".enumByte(
            """
            Remove buffers previously registered with #OP_PROVIDE_BUFFERS.

            {@code fd} must contain the number of buffers to remove, and {@code buf_group} must contain the buffer group ID from which to remove the buffers.

            Available since 5.7.
            """
        ),
        "OP_TEE".enumByte(
            """
            Issue the equivalent of a {@code tee(2)} system call.

            {@code splice_fd_in} is the file descriptor to read from, {@code fd} is the file descriptor to write to, {@code len} contains the number of bytes
            to copy, and {@code splice_flags} contains a bit mask for the flag field associated with the system call. Please note that both of the file
            descriptors must refer to a pipe. See also {@code tee(2)} for the general description of the related system call.

            Available since 5.8.
            """
        ),
        "OP_SHUTDOWN".enumByte(
            """
            Issue the equivalent of a {@code shutdown(2)} system call.

            {@code fd} is the file descriptor to the socket being shutdown and {@code len} must be set to the {@code how} argument. No other fields should be
            set.

            Available since 5.11.
            """
        ),
        "OP_RENAMEAT".enumByte(
            """
            Issue the equivalent of a {@code renameat2(2)} system call.

            {@code fd} should be set to the {@code olddirfd}, {@code addr} should be set to the {@code oldpath}, {@code len} should be set to the
            {@code newdirfd}, {@code addr} should be set to the {@code oldpath}, {@code addr2} should be set to the {@code newpath}, and finally
            {@code rename_flags} should be set to the {@code flags} passed in to {@code renameat2(2)}.

            Available since 5.11.
            """
        ),
        "OP_UNLINKAT".enumByte(
            """
            Issue the equivalent of a {@code unlinkat2(2)} system call.

            {@code fd} should be set to the {@code dirfd}, {@code addr} should be set to the {@code pathname}, and {@code unlink_flags} should be set to the
            {@code flags} being passed in to {@code unlinkat(2)}.

            Available since 5.11.
            """
        ),
        "OP_MKDIRAT".enumByte(
            """
            Issue the equivalent of a {@code mkdirat2(2)} system call.

            {@code fd} should be set to the {@code dirfd}, {@code addr} should be set to the {@code pathname}, and {@code len} should be set to the
            {@code mode} being passed in to {@code mkdirat(2)}.

            Available since 5.15.
            """
        ),
        "OP_SYMLINKAT".enumByte(
            """
            Issue the equivalent of a {@code symlinkat2(2)} system call.

            {@code fd} should be set to the {@code newdirfd}, {@code addr} should be set to the {@code target} and {@code addr2} should be set to the
            {@code linkpath} being passed in to {@code symlinkat(2)}.

            Available since 5.15.
            """
        ),
        "OP_LINKAT".enumByte(
            """
            Issue the equivalent of a {@code linkat2(2)} system call.

            {@code fd} should be set to the {@code olddirfd}, {@code addr} should be set to the {@code oldpath}, {@code len} should be set to the
            {@code newdirfd}, {@code addr2} should be set to the {@code newpath}, and {@code hardlink_flags} should be set to the {@code flags} being passed in
            {@code tolinkat(2)}.

            Available since 5.15.
            """
        ),
        "OP_GETDENTS".enumByte(
            """
            Issue the equivalent of a {@code getdents64(2)} system call.

            Available since 5.17.
            """
        ),
        "OP_LAST".enumByte
    )

    EnumConstant(
        "{@code sqe->fsync_flags}",

        "FSYNC_DATASYNC".enum("", "1 << 0")
    )

    EnumConstant(
        "{@code sqe->timeout_flags}",

        "TIMEOUT_ABS".enum("", "1 << 0"),
        "TIMEOUT_UPDATE".enum("", "1 << 1"),
        "TIMEOUT_BOOTTIME".enum("", "1 << 2"),
        "TIMEOUT_REALTIME".enum("", "1 << 3"),
        "LINK_TIMEOUT_UPDATE".enum("", "1 << 4"),
        "TIMEOUT_ETIME_SUCCESS".enum("", "1 << 5"),
        "TIMEOUT_CLOCK_MASK".enum("", "IORING_TIMEOUT_BOOTTIME | IORING_TIMEOUT_REALTIME"),
        "TIMEOUT_UPDATE_MASK".enum("", "IORING_TIMEOUT_UPDATE | IORING_LINK_TIMEOUT_UPDATE")
    )

    EnumConstant(
        "{@code sqe->splice_flags}, extends {@code splice(2)} flags",

        "SPLICE_F_FD_IN_FIXED".enum("", "1 << 31")
    )

    EnumConstant(
        """
        {@code POLL_ADD} flags. Note that since {@code sqe->poll_events} is the flag space, the command flags for {@code POLL_ADD} are stored in
        {@code sqe->len}.

        {@code IORING_POLL_UPDATE}: Update existing poll request, matching {@code sqe->addr} as the old {@code user_data} field.
        """,

        "POLL_ADD_MULTI".enum(
            "Multishot poll. Sets {@code IORING_CQE_F_MORE} if the poll handler will continue to report CQEs on behalf of the same SQE.",
            "1 << 0"
        ),
        "POLL_UPDATE_EVENTS".enum("", "1 << 1"),
        "POLL_UPDATE_USER_DATA".enum("", "1 << 2")
    )

    EnumConstant(
        "{@code cqe->flags}",

        "CQE_F_BUFFER".enum("If set, the upper 16 bits are the buffer ID", "1 << 0"),
        "CQE_F_MORE".enum("If set, parent SQE will generate more CQE entries", "1 << 0")
    )

    EnumConstant(
        "",

        "CQE_BUFFER_SHIFT".enum("", "16")
    )

    LongConstant(
        "Magic offsets for the application to {@code mmap} the data it needs",

        "OFF_SQ_RING".."0L",
        "OFF_CQ_RING".."0x8000000L",
        "OFF_SQES".."0x10000000L",
    )

    EnumConstant(
        "{@code sq_ring->flags}",

        "SQ_NEED_WAKEUP".enum("needs {@code io_uring_enter} wakeup", "1 << 0"),
        "SQ_CQ_OVERFLOW".enum("CQ ring is overflown", "1 << 0")
    )

    EnumConstant(
        "{@code cq_ring->flags}",

        "CQ_EVENTFD_DISABLED".enum("disable {@code eventfd} notifications", "1 << 0")
    )

    EnumConstant(
        "{@code io_uring_enter(2)} flags",

        "ENTER_GETEVENTS".enum(
            """
            If this flag is set, then the system call will wait for the specificied number of events in {@code min_complete} before returning.

            This flag can be set along with {@code to_submit} to both submit and complete events in a single system call.
            """,
            "1 << 0"
        ),
        "ENTER_SQ_WAKEUP".enum(
            "If the ring has been created with #SETUP_SQPOLL, then this flag asks the kernel to wakeup the SQ kernel thread to submit IO.",
            "1 << 1"
        ),
        "ENTER_SQ_WAIT".enum(
            """
            If the ring has been created with #SETUP_SQPOLL, then the application has no real insight into when the SQ kernel thread has consumed entries from
            the SQ ring. This can lead to a situation where the application can no longer get a free SQE entry to submit, without knowing when it one becomes
            available as the SQ kernel thread consumes them. If the system call is used with this flag set, then it will wait until at least one entry is free
            in the SQ ring.
            """,
            "1 << 2"
        ),
        "ENTER_EXT_ARG".enum(
            """
            Since kernel 5.11, the system calls arguments have been modified to look like the following:
            ${codeBlock("""
int io_uring_enter(unsigned int fd, unsigned int to_submit,
                   unsigned int min_complete, unsigned int flags,
                   const void *arg, size_t argsz);
            """)}

            which is behaves just like the original definition by default. However, if {@code IORING_ENTER_EXT_ARG} is set, then instead of a {@code sigset_t}
            being passed in, a pointer to a struct {@code io_uring_getevents_arg} is used instead and {@code argsz} must be set to the size of this structure.

            The definition is ##IOURingGeteventsArg which allows passing in both a signal mask as well as pointer to a struct {@code __kernel_timespec} timeout
            value. If {@code ts} is set to a valid pointer, then this time value indicates the timeout for waiting on events. If an application is waiting on
            events and wishes to stop waiting after a specified amount of time, then this can be accomplished directly in version 5.11 and newer by using this
            feature.
            """,
            "1 << 3"
        )
    )

    EnumConstant(
        "{@code io_uring_params->features} flags",

        "FEAT_SINGLE_MMAP".enum(
            """
            If this flag is set, the two SQ and CQ rings can be mapped with a single {@code mmap(2)} call.

            The SQEs must still be allocated separately. This brings the necessary {@code mmap(2)} calls down from three to two. Available since kernel 5.4.
            """,
            "1 << 0"
        ),
        "FEAT_NODROP".enum(
            """
            If this flag is set, {@code io_uring} supports never dropping completion events.

            If a completion event occurs and the CQ ring is full, the kernel stores the event internally until such a time that the CQ ring has room for more
            entries. If this overflow condition is entered, attempting to submit more IO will fail with the {@code -EBUSY} error value, if it can't flush the
            overflown events to the CQ ring. If this happens, the application must reap events from the CQ ring and attempt the submit again. Available since
            kernel 5.5.
            """,
            "1 << 1"
        ),
        "FEAT_SUBMIT_STABLE".enum(
            """
            If this flag is set, applications can be certain that any data for async offload has been consumed when the kernel has consumed the SQE.

            Available since kernel 5.5.
            """,
            "1 << 2"
        ),
        "FEAT_RW_CUR_POS".enum(
            """
            If this flag is set, applications can specify {@code offset == -1} with {@code IORING_OP_{READV,WRITEV}}, {@code IORING_OP_{READ,WRITE}_FIXED}, and
            {@code IORING_OP_{READ,WRITE}} to mean current file position, which behaves like {@code preadv2(2)} and {@code pwritev2(2)} with
            {@code offset == -1}.

            It'll use (and update) the current file position. This obviously comes with the caveat that if the application has multiple reads or writes in
            flight, then the end result will not be as expected. This is similar to threads sharing a file descriptor and doing IO using the current file
            position.

            Available since kernel 5.6.
            """,
            "1 << 3"
        ),
        "FEAT_CUR_PERSONALITY".enum(
            """
            If this flag is set, then {@code io_uring} guarantees that both sync and async execution of a request assumes the credentials of the task that
            called #enter() to queue the requests.

            If this flag isn't set, then requests are issued with the credentials of the task that originally registered the {@code io_uring}. If only one task
            is using a ring, then this flag doesn't matter as the credentials will always be the same. Note that this is the default behavior, tasks can still
            register different personalities through #register() with #REGISTER_PERSONALITY and specify the personality to use in the sqe.

            Available since kernel 5.6.
            """,
            "1 << 4"
        ),
        "FEAT_FAST_POLL".enum(
            """
            If this flag is set, then {@code io_uring} supports using an internal poll mechanism to drive data/space readiness.

            This means that requests that cannot read or write data to a file no longer need to be punted to an async thread for handling, instead they will
            begin operation when the file is ready. This is similar to doing poll + read/write in userspace, but eliminates the need to do so. If this flag is
            set, requests waiting on space/data consume a lot less resources doing so as they are not blocking a thread.

            Available since kernel 5.7.
            """,
            "1 << 5"
        ),
        "FEAT_POLL_32BITS".enum(
            """
            If this flag is set, the #OP_POLL_ADD command accepts the full 32-bit range of epoll based flags.

            Most notably {@code EPOLLEXCLUSIVE} which allows exclusive (waking single waiters) behavior.

            Available since kernel 5.9.
            """,
            "1 << 6"
        ),
        "FEAT_SQPOLL_NONFIXED".enum(
            """
            If this flag is set, the #SETUP_SQPOLL feature no longer requires the use of fixed files.

            Any normal file descriptor can be used for IO commands without needing registration.

            Available since kernel 5.11.
            """,
            "1 << 7"
        ),
        "FEAT_EXT_ARG".enum(
            """
            If this flag is set, then the #enter() system call supports passing in an extended argument instead of just the {@code sigset_t} of earlier
            kernels.

            This extended argument is of type {@code struct io_uring_getevents_arg} and allows the caller to pass in both a {@code sigset_t} and a timeout
            argument for waiting on events. A pointer to this struct must be passed in if #ENTER_EXT_ARG is set in the flags for the enter system call.

            Available since kernel 5.11.
            """,
            "1 << 8"
        ),
        "FEAT_NATIVE_WORKERS".enum(
            """
            If this flag is set, {@code io_uring} is using native workers for its async helpers.

            Previous kernels used kernel threads that assumed the identity of the original {@code io_uring} owning task, but later kernels will actively create
            what looks more like regular process threads instead.

            Available since kernel 5.12.
            """,
            "1 << 9"
        ),
        "FEAT_RSRC_TAGS".enum(
            """
            If this flag is set, then {@code io_uring} supports a variety of features related to fixed files and buffers.

            In particular, it indicates that registered buffers can be updated in-place, whereas before the full set would have to be unregistered first.

            Available since kernel 5.13.
            """,
            "1 << 10"
        ),
        "FEAT_CQE_SKIP".enum("", "1 << 11")
    )

    EnumConstant(
        "#register() {@code opcodes} and arguments",

        "REGISTER_BUFFERS".enum(
            """
            {@code arg} points to a struct {@code iovec} array of {@code nr_args} entries.

            The buffers associated with the {@code iovecs} will be locked in memory and charged against the user's {@code RLIMIT_MEMLOCK} resource limit.
            See {@code getrlimit(2)} for more information. Additionally, there is a size limit of 1GiB per buffer. Currently, the buffers must be anonymous,
            non-file-backed memory, such as that returned by {@code malloc(3)} or {@code mmap(2)} with the {@code MAP_ANONYMOUS} flag set. It is expected that
            this limitation will be lifted in the future. Huge pages are supported as well. Note that the entire huge page will be pinned in the kernel, even
            if only a portion of it is used.

            After a successful call, the supplied buffers are mapped into the kernel and eligible for I/O. To make use of them, the application must specify
            the #OP_READ_FIXED or #OP_WRITE_FIXED {@code opcodes} in the submission queue entry (see the struct {@code io_uring_sqe} definition in #enter()),
            and set the {@code buf_index} field to the desired buffer index. The memory range described by the submission queue entry's {@code addr} and
            {@code len} fields must fall within the indexed buffer.

            It is perfectly valid to setup a large buffer and then only use part of it for an I/O, as long as the range is within the originally mapped region.

            An application can increase or decrease the size or number of registered buffers by first unregistering the existing buffers, and then issuing a
            new call to {@code io_uring_register()} with the new buffers.

            Note that before 5.13 registering buffers would wait for the ring to idle. If the application currently has requests in-flight, the registration
            will wait for those to finish before proceeding.

            An application need not unregister buffers explicitly before shutting down the io_uring instance.

            Available since 5.1.
            """,
            "0"
        ),
        "UNREGISTER_BUFFERS".enum(
            """
            This operation takes no argument, and {@code arg} must be passed as #NULL.

            All previously registered buffers associated with the {@code io_uring} instance will be released.

            Available since 5.1.
            """
        ),
        "REGISTER_FILES".enum(
            """
            Register files for I/O.

            {@code arg} contains a pointer to an array of {@code nr_args} file descriptors (signed 32 bit integers). To make use of the registered files, the
            #IOSQE_FIXED_FILE flag must be set in the {@code flags} member of the struct {@code io_uring_sqe}, and the {@code fd} member is set to the index of
            the file in the file descriptor array.

            The file set may be sparse, meaning that the {@code fd} field in the array may be set to -1. See #REGISTER_FILES_UPDATE for how to update files in
            place.

            Note that before 5.13 registering files would wait for the ring to idle. If the application currently has requests in-flight, the registration will
            wait for those to finish before proceeding. See #REGISTER_FILES_UPDATE for how to update an existing set without that limitation.

            Files are automatically unregistered when the io_uring instance is torn down. An application needs only unregister if it wishes to register a new
            set of fds.

            Available since 5.1.
            """
        ),
        "UNREGISTER_FILES".enum(
            """
            This operation requires no argument, and {@code arg} must be passed as {@code NULL}.

            All previously registered files associated with the {@code io_uring} instance will be unregistered.

            Available since 5.1.
            """
        ),
        "REGISTER_EVENTFD".enum(
            """
            It's possible to use {@code eventfd(2)} to get notified of completion events on an {@code io_uring} instance. If this is desired, an eventfd file
            descriptor can be registered through this operation.

            {@code arg} must contain a pointer to the eventfd file descriptor, and {@code nr_args} must be 1.

            Available since 5.2.

            An application can temporarily disable notifications, coming through the registered eventfd, by setting the #CQ_EVENTFD_DISABLED bit in the
            {@code flags} field of the CQ ring.

            Available since 5.8.
            """
        ),
        "UNREGISTER_EVENTFD".enum(
            """
            Unregister an eventfd file descriptor to stop notifications.

            Since only one eventfd descriptor is currently supported, this operation takes no argument, and {@code arg} must be passed as #NULL and
            {@code nr_args} must be zero.

            Available since 5.2.
            """
        ),
        "REGISTER_FILES_UPDATE".enum(
            """
            This operation replaces existing files in the registered file set with new ones, either turning a sparse entry (one where {@code fd} is equal to
            -1) into a real one, removing an existing entry (new one is set to -1), or replacing an existing entry with a new existing entry.

            {@code arg} must contain a pointer to a struct {@code io_uring_files_update}, which contains an offset on which to start the update, and an array
            of file descriptors to use for the update. {@code nr_args} must contain the number of descriptors in the passed in array.

            Available since 5.5.

            File descriptors can be skipped if they are set to #REGISTER_FILES_SKIP. Skipping an fd will not touch the file associated with the previous fd at
            that index.

            Available since 5.12.
            """
        ),
        "REGISTER_EVENTFD_ASYNC".enum(
            """
            This works just like #REGISTER_EVENTFD, except notifications are only posted for events that complete in an async manner.

            This means that events that complete inline while being submitted do not trigger a notification event. The arguments supplied are the same as for
            {@code IORING_REGISTER_EVENTFD}.

            Available since 5.6.
            """
        ),
        "REGISTER_PROBE".enum(
            """
            This operation returns a structure, {@code io_uring_probe}, which contains information about the {@code opcodes} supported by {@code io_uring} on
            the running kernel.

            {@code arg} must contain a pointer to a struct {@code io_uring_probe}, and {@code nr_args} must contain the size of the ops array in that probe
            struct. The {@code ops} array is of the type {@code io_uring_probe_op}, which holds the value of the {@code opcode} and a {@code flags} field. If
            the flags field has #IO_URING_OP_SUPPORTED set, then this opcode is supported on the running kernel.

            Available since 5.6.
            """
        ),
        "REGISTER_PERSONALITY".enum(
            """
            This operation registers credentials of the running application with {@code io_uring}, and returns an id associated with these credentials.

            Applications wishing to share a ring between separate users/processes can pass in this credential id in the sqe personality field. If set, that
            particular sqe will be issued with these credentials. Must be invoked with {@code arg} set to #NULL and {@code nr_args} set to zero.

            Available since 5.6.
            """
        ),
        "UNREGISTER_PERSONALITY".enum(
            """
            This operation unregisters a previously registered personality with {@code io_uring}.

            {@code nr_args} must be set to the id in question, and {@code arg} must be set to #NULL.

            Available since 5.6.
            """
        ),

        "REGISTER_RESTRICTIONS".enum(
            """
            {@code arg} points to a struct {@code io_uring_restriction} array of {@code nr_args} entries.

            With an entry it is possible to allow an #register() {@code opcode}, or specify which {@code opcode} and flags of the submission queue entry are
            allowed, or require certain flags to be specified (these flags must be set on each submission queue entry).

            All the restrictions must be submitted with a single {@code io_uring_register()} call and they are handled as an allowlist ({@code opcodes} and
            flags not registered, are not allowed).

            Restrictions can be registered only if the {@code io_uring} ring started in a disabled state (#SETUP_R_DISABLED must be specified in the call to
            #setup()).

            Available since 5.10.
            """
        ),
        "REGISTER_ENABLE_RINGS".enum(
            """
            This operation enables an {@code io_uring} ring started in a disabled state (#SETUP_R_DISABLED was specified in the call to #setup()).

            While the {@code io_uring} ring is disabled, submissions are not allowed and registrations are not restricted. After the execution of this
            operation, the {@code io_uring} ring is enabled: submissions and registration are allowed, but they will be validated following the registered
            restrictions (if any). This operation takes no argument, must be invoked with {@code arg} set to #NULL and {@code nr_args} set to zero.

            Available since 5.10.
            """
        ),
        "REGISTER_FILES2".enum(
            """
            Register files for I/O. Similar to #REGISTER_FILES.

            {@code arg} points to a struct {@code io_uring_rsrc_register}, and {@code nr_args} should be set to the number of bytes in the structure.

            The {@code data} field contains a pointer to an array of {@code nr} file descriptors (signed 32 bit integers). {@code tags} field should either be
            0 or or point to an array of {@code nr} "tags" (unsigned 64 bit integers). See #REGISTER_BUFFERS2 for more info on resource tagging.

            Note that resource updates, e.g. #REGISTER_FILES_UPDATE, don't necessarily deallocate resources, they might be held until all requests using that
            resource complete.

            Available since 5.13.
            """
        ),
        "REGISTER_FILES_UPDATE2".enum(
            """
            Similar to #REGISTER_FILES_UPDATE, replaces existing files in the registered file set with new ones, either turning a sparse entry (one where fd is
            equal to -1) into a real one, removing an existing entry (new one is set to -1), or replacing an existing entry with a new existing entry.

            {@code arg} must contain a pointer to a struct {@code io_uring_rsrc_update2}, which contains an offset on which to start the update, and an array
            of file descriptors to use for the update stored in data. {@code tags} points to an array of tags. {@code nr} must contain the number of
            descriptors in the passed in arrays. See #REGISTER_BUFFERS2 for the resource tagging description.

            Available since 5.13.
            """
        ),
        "REGISTER_BUFFERS2".enum(
            """
            Register buffers for I/O.

            Similar to #REGISTER_BUFFERS but aims to have a more extensible ABI. {@code arg} points to a struct {@code io_uring_rsrc_register}, and
            {@code nr_args} should be set to the number of bytes in the structure.

            The data field contains a pointer to a struct {@code iovec} array of {@code nr} entries. The {@code tags} field should either be 0, then tagging is
            disabled, or point to an array of {@code nr} "tags" (unsigned 64 bit integers). If a tag is zero, then tagging for this particular resource (a
            buffer in this case) is disabled. Otherwise, after the resource had been unregistered and it's not used anymore, a CQE will be posted with
            {@code user_data} set to the specified tag and all other fields zeroed.

            Note that resource updates, e.g. #REGISTER_BUFFERS_UPDATE, don't necessarily deallocate resources by the time it returns, but they might be held
            alive until all requests using it complete.

            Available since 5.13.
            """
        ),
        "REGISTER_BUFFERS_UPDATE".enum(
            """
            Updates registered buffers with new ones, either turning a sparse entry into a real one, or replacing an existing entry.

            {@code arg} must contain a pointer to a struct {@code io_uring_rsrc_update2}, which contains an offset on which to start the update, and an array
            of struct {@code iovec}. {@code tags} points to an array of tags. {@code nr} must contain the number of descriptors in the passed in arrays. See
            #REGISTER_BUFFERS2 for the resource tagging description.

            Available since 5.13.
            """
        ),

        "REGISTER_IOWQ_AFF".enum(
            """
            By default, async workers created by {@code io_uring} will inherit the CPU mask of its parent.

            This is usually all the CPUs in the system, unless the parent is being run with a limited set. If this isn't the desired outcome, the application
            may explicitly tell {@code io_uring} what CPUs the async workers may run on.

            {@code arg} must point to a {@code cpu_set_t} mask, and {@code nr_args} the byte size of that mask.

            Available since 5.14.
            """
        ),
        "UNREGISTER_IOWQ_AFF".enum(
            """
            Undoes a CPU mask previously set with #REGISTER_IOWQ_AFF.

            Must not have {@code arg} or {@code nr_args} set.

            Available since 5.14.
            """
        ),

        "REGISTER_IOWQ_MAX_WORKERS".enum(
            """
            By default, {@code io_uring} limits the unbounded workers created to the maximum processor count set by {@code RLIMIT_NPROC} and the bounded
            workers is a function of the SQ ring size and the number of CPUs in the system. Sometimes this can be excessive (or too little, for bounded), and
            this command provides a way to change the count per ring (per NUMA node) instead.

            {@code arg} must be set to an unsigned int pointer to an array of two values, with the values in the array being set to the maximum count of
            workers per NUMA node. Index 0 holds the bounded worker count, and index 1 holds the unbounded worker count. On successful return, the passed in
            array will contain the previous maximum valyes for each type. If the count being passed in is 0, then this command returns the current maximum
            values and doesn't modify the current setting. {@code nr_args} must be set to 2, as the command takes two values.

            Available since 5.15.
            """
        ),

        "REGISTER_LAST".enum
    )

    EnumConstant(
        "{@code io-wq} worker categories",

        "IO_WQ_BOUND".enum("", "0"),
        "IO_WQ_UNBOUND".enum
    ).noPrefix()

    IntConstant(
        "Skip updating fd indexes set to this value in the fd table.",

        "REGISTER_FILES_SKIP".."-2"
    )

    IntConstant(
        "",

        "IO_URING_OP_SUPPORTED".."1 << 0"
    ).noPrefix()

    EnumConstant(
        "{@code io_uring_restriction->opcode} values",

        "RESTRICTION_REGISTER_OP".enum("Allow an {@code io_uring_register(2)} opcode", "0"),
        "RESTRICTION_SQE_OP".enum("Allow an sqe opcode"),
        "RESTRICTION_SQE_FLAGS_ALLOWED".enum("Allow sqe flags"),
        "RESTRICTION_SQE_FLAGS_REQUIRED".enum("Require sqe flags (these flags must be set on each submission)"),
        "RESTRICTION_LAST".enum("Require sqe flags (these flags must be set on each submission)")
    )

    SaveErrno..NativeName("__sys_io_uring_setup")..int(
        "setup",
        """
        The {@code io_uring_setup()} system call sets up a submission queue (SQ) and completion queue (CQ) with at least {@code entries} entries, and returns a
        file descriptor which can be used to perform subsequent operations on the {@code io_uring} instance.

        The submission and completion queues are shared between userspace and the kernel, which eliminates the need to copy data when initiating and completing
        I/O.

        Closing the file descriptor returned by {@code io_uring_setup(2)} will free all resources associated with the {@code io_uring} context.
        """,

        unsigned("entries", ""),
        io_uring_params.p("p", "used by the application to pass options to the kernel, and by the kernel to convey information about the ring buffers"),

        returnDoc =
        """
        a new file descriptor on success.

        The application may then provide the file descriptor in a subsequent {@code mmap(2)} call to map the submission and completion queues, or to the
        #register() or #enter() system calls.

        On error, {@code -1} is returned and {@code errno} is set appropriately.
        """
    )

    SaveErrno..NativeName("__sys_io_uring_register")..int(
        "register",
        """
        The {@code io_uring_register()} system call registers resources (e.g. user buffers, files, eventfd, personality, restrictions) for use in an
        {@code io_uring} instance referenced by {@code fd}.

        Registering files or user buffers allows the kernel to take long term references to internal data structures or create long term mappings of
        application memory, greatly reducing per-I/O overhead.
        """,

        int("fd", "the file descriptor returned by a call to #setup()"),
        unsigned("opcode", "", "REGISTER_\\w+"),
        nullable..opaque_p("arg", ""),
        unsigned("nr_args", ""),

        returnDoc = "on success, returns 0. On error, -1 is returned, and {@code errno} is set accordingly."
    )

    SaveErrno..NativeName("__sys_io_uring_enter2")..int(
        "enter2",
        "",

        int("fd", ""),
        unsigned("to_submit", ""),
        unsigned("min_complete", ""),
        unsigned("flags", ""),
        nullable..sigset_t.p("sig", ""),
        int("sz", "")
    )

    SaveErrno..NativeName("__sys_io_uring_enter")..int(
        "enter",
        """
        {@code io_uring_enter()} is used to initiate and complete I/O using the shared submission and completion queues setup by a call to #setup().

        A single call can both submit new I/O and wait for completions of I/O initiated by this call or previous calls to {@code io_uring_enter()}.

        If the {@code io_uring} instance was configured for polling, by specifying #SETUP_IOPOLL in the call to {@code io_uring_setup()}, then
        {@code min_complete} has a slightly different meaning. Passing a value of 0 instructs the kernel to return any events which are already complete,
        without blocking. If {@code min_complete} is a non-zero value, the kernel will still return immediately if any completion events are available. If no
        event completions are available, then the call will poll either until one or more completions become available, or until the process has exceeded its
        scheduler time slice.

        Note that, for interrupt driven I/O (where {@code IORING_SETUP_IOPOLL} was not specified in the call to {@code io_uring_setup()}), an application may
        check the completion queue for event completions without entering the kernel at all.

        When the system call returns that a certain amount of SQEs have been consumed and submitted, it's safe to reuse SQE entries in the ring. This is true
        even if the actual IO submission had to be punted to async context, which means that the SQE may in fact not have been submitted yet. If the kernel
        requires later use of a particular SQE entry, it will have made a private copy of it.
        """,

        int("fd", "the file descriptor returned by #setup()"),
        unsigned("to_submit", "the number of I/Os to submit from the submission queue"),
        unsigned("min_complete", ""),
        unsigned("flags", "", "ENTER_\\w+", LinkMode.BITFIELD),
        nullable..sigset_t.p(
            "sig",
            """
            a pointer to a signal mask (see {@code sigprocmask(2)}); if {@code sig} is not #NULL, {@code io_uring_enter()} first replaces the current signal
            mask by the one pointed to by sig, then waits for events to become available in the completion queue, and then restores the original signal mask.
            The following {@code io_uring_enter()} call:
            ${codeBlock("""
ret = io_uring_enter(fd, 0, 1, IORING_ENTER_GETEVENTS, &sig);
            """)}

            is equivalent to atomically executing the following calls:
            ${codeBlock("""
pthread_sigmask(SIG_SETMASK, &sig, &orig);
ret = io_uring_enter(fd, 0, 1, IORING_ENTER_GETEVENTS, NULL);
pthread_sigmask(SIG_SETMASK, &orig, NULL);""")}

            See the description of {@code pselect(2)} for an explanation of why the {@code sig} parameter is necessary.
            """
        ),

        returnDoc =
        """
        the number of I/Os successfully consumed.

        This can be zero if {@code to_submit} was zero or if the submission queue was empty. Note that if the ring was created with #SETUP_SQPOLL specified,
        then the return value will generally be the same as {@code to_submit} as submission happens outside the context of the system call.

        The errors related to a submission queue entry will be returned through a completion queue entry, rather than through the system call itself.

        Errors that occur not on behalf of a submission queue entry are returned via the system call directly. On such an error, -1 is returned and
        {@code errno} is set appropriately.
        """
    )
}