/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.linux.liburing;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.system.linux.*;

/** Native bindings to <a target="_blank" href="https://github.com/axboe/liburing">liburing</a>. */
public class LibURing {

    static { Library.initialize(); }

    public static final long LIBURING_UDATA_TIMEOUT = -1L;

    protected LibURing() {
        throw new UnsupportedOperationException();
    }

    // --- [ io_uring_get_probe_ring ] ---

    /** Unsafe version of: {@link #io_uring_get_probe_ring get_probe_ring} */
    public static native long nio_uring_get_probe_ring(long ring);

    /**
     * Return an allocated {@code io_uring_probe} structure, or {@code NULL} if probe fails (for example, if it is not available).
     * 
     * <p>The caller is responsible for freeing it.</p>
     */
    @Nullable
    @NativeType("struct io_uring_probe *")
    public static IOURingProbe io_uring_get_probe_ring(@NativeType("struct io_uring *") IOURing ring) {
        long __result = nio_uring_get_probe_ring(ring.address());
        return IOURingProbe.createSafe(__result);
    }

    // --- [ io_uring_get_probe ] ---

    /** Unsafe version of: {@link #io_uring_get_probe get_probe} */
    public static native long nio_uring_get_probe();

    /** Same as {@link #io_uring_get_probe_ring get_probe_ring}, but takes care of ring init and teardown. */
    @Nullable
    @NativeType("struct io_uring_probe *")
    public static IOURingProbe io_uring_get_probe() {
        long __result = nio_uring_get_probe();
        return IOURingProbe.createSafe(__result);
    }

    // --- [ io_uring_free_probe ] ---

    /** Unsafe version of: {@link #io_uring_free_probe free_probe} */
    public static native void nio_uring_free_probe(long probe);

    /** Frees a probe allocated through {@link #io_uring_get_probe get_probe} or {@link #io_uring_get_probe_ring get_probe_ring}. */
    public static void io_uring_free_probe(@NativeType("struct io_uring_probe *") IOURingProbe probe) {
        nio_uring_free_probe(probe.address());
    }

    // --- [ io_uring_opcode_supported ] ---

    public static native int nio_uring_opcode_supported(long p, int op);

    public static int io_uring_opcode_supported(@NativeType("struct io_uring_probe const *") IOURingProbe p, int op) {
        return nio_uring_opcode_supported(p.address(), op);
    }

    // --- [ io_uring_queue_init_params ] ---

    public static native int nio_uring_queue_init_params(int entries, long ring, long p);

    public static int io_uring_queue_init_params(@NativeType("unsigned") int entries, @NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_params *") IOURingParams p) {
        return nio_uring_queue_init_params(entries, ring.address(), p.address());
    }

    // --- [ io_uring_queue_init ] ---

    /** Unsafe version of: {@link #io_uring_queue_init queue_init} */
    public static native int nio_uring_queue_init(int entries, long ring, int flags);

    /**
     * Executes the {@link LibIOURing#io_uring_setup setup} syscall to  initialize the submission and completion queues in the kernel with at least {@code entries} entries and then maps the
     * resulting file descriptor to memory shared between the application and the kernel.
     * 
     * <p>On success, the resources held by {@code ring} should be released via a corresponding call to {@link #io_uring_queue_exit queue_exit}.</p>
     *
     * @param flags will be passed through to the {@link LibIOURing#io_uring_setup setup} syscall
     *
     * @return 0 on success and {@code ring} will point to the shared memory containing the {@code io_uring} queues. On failure {@code -errno} is returned.
     */
    public static int io_uring_queue_init(@NativeType("unsigned") int entries, @NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int flags) {
        return nio_uring_queue_init(entries, ring.address(), flags);
    }

    // --- [ io_uring_queue_mmap ] ---

    /** Unsafe version of: {@link #io_uring_queue_mmap queue_mmap} */
    public static native int nio_uring_queue_mmap(int fd, long p, long ring);

    /**
     * For users that want to specify {@code sq_thread_cpu} or {@code sq_thread_idle}, this interface is a convenient helper for {@code mmap()}ing the rings.
     *
     * @param fd   a file descriptor returned by {@link LibIOURing#io_uring_setup setup}
     * @param ring on success, contains the necessary information to read/write to the rings
     *
     * @return {@code -errno} on error, or zero on success
     */
    public static int io_uring_queue_mmap(int fd, @NativeType("struct io_uring_params *") IOURingParams p, @NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_queue_mmap(fd, p.address(), ring.address());
    }

    // --- [ io_uring_ring_dontfork ] ---

    /** Unsafe version of: {@link #io_uring_ring_dontfork ring_dontfork} */
    public static native int nio_uring_ring_dontfork(long ring);

    /**
     * Ensure that the {@code mmap}'ed rings aren't available to a child after a {@code fork(2)}.
     * 
     * <p>This uses {@code madvise(..., MADV_DONTFORK)} on the {@code mmap}'ed ranges.</p>
     */
    public static int io_uring_ring_dontfork(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_ring_dontfork(ring.address());
    }

    // --- [ io_uring_queue_exit ] ---

    /** Unsafe version of: {@link #io_uring_queue_exit queue_exit} */
    public static native void nio_uring_queue_exit(long ring);

    /**
     * Will release all resources acquired and initialized by {@link #io_uring_queue_init queue_init}.
     * 
     * <p>It first unmaps the memory shared between the application and the kernel and then closes the {@code io_uring} file descriptor.</p>
     */
    public static void io_uring_queue_exit(@NativeType("struct io_uring *") IOURing ring) {
        nio_uring_queue_exit(ring.address());
    }

    // --- [ io_uring_peek_batch_cqe ] ---

    /** Unsafe version of: {@link #io_uring_peek_batch_cqe peek_batch_cqe} */
    public static native int nio_uring_peek_batch_cqe(long ring, long cqes, int count);

    /**
     * Fill in an array of IO completions up to count, if any are available.
     *
     * @return the amount of IO completions filled
     */
    @NativeType("unsigned")
    public static int io_uring_peek_batch_cqe(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqes) {
        return nio_uring_peek_batch_cqe(ring.address(), memAddress(cqes), cqes.remaining());
    }

    // --- [ io_uring_wait_cqes ] ---

    /** Unsafe version of: {@link #io_uring_wait_cqes wait_cqes} */
    public static native int nio_uring_wait_cqes(long ring, long cqe_ptr, int wait_nr, long ts, long sigmask);

    /**
     * Returns {@code wait_nr} IO completions from the queue belonging to the {@code ring} param, waiting for it if necessary or until the timeout {@code ts}
     * expires.
     * 
     * <p>If {@code ts} is specified, the application does not need to call {@link #io_uring_submit submit} before calling {@code io_uring_wait_cqes()}.</p>
     *
     * @param cqe_ptr filled in on success
     * @param sigmask the set of signals to block. The prevailing signal mask is restored before returning.
     *
     * @return 0 on success and the {@code cqe_ptr} param is filled in. On failure it returns {@code -errno}.
     */
    public static int io_uring_wait_cqes(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqe_ptr, @Nullable @NativeType("struct __kernel_timespec *") KernelTimespec ts, @NativeType("sigset_t *") long sigmask) {
        return nio_uring_wait_cqes(ring.address(), memAddress(cqe_ptr), cqe_ptr.remaining(), memAddressSafe(ts), sigmask);
    }

    // --- [ io_uring_wait_cqe_timeout ] ---

    /** Unsafe version of: {@link #io_uring_wait_cqe_timeout wait_cqe_timeout} */
    public static native int nio_uring_wait_cqe_timeout(long ring, long cqe_ptr, long ts);

    /**
     * Returns one IO completion from the queue belonging to the {@code ring} param, waiting for it if necessary or until the timeout {@code ts} expires.
     * 
     * <p>If {@code ts} is specified, the application does not need to call {@link #io_uring_submit submit} before calling {@code io_uring_wait_cqe_timeout()}.</p>
     *
     * @param cqe_ptr filled in on success
     *
     * @return 0 on success and the {@code cqe_ptr} param is filled in. On failure it returns {@code -errno}.
     */
    public static int io_uring_wait_cqe_timeout(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqe_ptr, @Nullable @NativeType("struct __kernel_timespec *") KernelTimespec ts) {
        if (CHECKS) {
            check(cqe_ptr, 1);
        }
        return nio_uring_wait_cqe_timeout(ring.address(), memAddress(cqe_ptr), memAddressSafe(ts));
    }

    // --- [ io_uring_submit ] ---

    /** Unsafe version of: {@link #io_uring_submit submit} */
    public static native int nio_uring_submit(long ring);

    /**
     * Submits the next events to the submission queue belonging to the {@code ring}.
     * 
     * <p>After the caller retrieves a submission queue entry (SQE) with {@link #io_uring_get_sqe get_sqe}, prepares the SQE, it can be submitted with {@code io_uring_submit()}.</p>
     *
     * @return the number of submitted submission queue entries on success. On failure it returns {@code -errno}.
     */
    public static int io_uring_submit(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_submit(ring.address());
    }

    // --- [ io_uring_submit_and_wait ] ---

    /** Unsafe version of: {@link #io_uring_submit_and_wait submit_and_wait} */
    public static native int nio_uring_submit_and_wait(long ring, int wait_nr);

    /**
     * Submits the next events to the submission queue belonging to the {@code ring} and waits for {@code wait_nr} completion events.
     * 
     * <p>After the caller retrieves a submission queue entry (SQE) with {@link #io_uring_get_sqe get_sqe}, prepares the SQE, it can be submitted with
     * {@code io_uring_submit_and_wait()}.</p>
     *
     * @return the number of submitted submission queue entries on success. On failure it returns {@code -errno}.
     */
    public static int io_uring_submit_and_wait(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int wait_nr) {
        return nio_uring_submit_and_wait(ring.address(), wait_nr);
    }

    // --- [ io_uring_submit_and_wait_timeout ] ---

    /** Unsafe version of: {@link #io_uring_submit_and_wait_timeout submit_and_wait_timeout} */
    public static native int nio_uring_submit_and_wait_timeout(long ring, long cqe_ptr, int wait_nr, long ts, long sigmask);

    /**
     * Submits the next events to the submission queue belonging to the {@code ring} and waits for {@code wait_nr} completion events or until the timeout
     * {@code ts} expires.The completion events are stored in the {@code cqe_ptr} array.
     * 
     * <p>After the caller retrieves a submission queue entry (SQE) with {@link #io_uring_get_sqe get_sqe}, prepares the SQE, it can be submitted with
     * {@code io_uring_submit_and_wait_timeout()}.</p>
     *
     * @param sigmask the set of signals to block. The prevailing signal mask is restored before returning.
     *
     * @return the number of submitted submission queue entries on success. On failure it returns {@code -errno}.
     */
    public static int io_uring_submit_and_wait_timeout(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqe_ptr, @Nullable @NativeType("struct __kernel_timespec *") KernelTimespec ts, @NativeType("sigset_t *") long sigmask) {
        return nio_uring_submit_and_wait_timeout(ring.address(), memAddress(cqe_ptr), cqe_ptr.remaining(), memAddressSafe(ts), sigmask);
    }

    // --- [ io_uring_get_sqe ] ---

    /** Unsafe version of: {@link #io_uring_get_sqe get_sqe} */
    public static native long nio_uring_get_sqe(long ring);

    /**
     * Gets the next available submission queue entry from the submission queue belonging to the {@code ring} param.
     * 
     * <p>If a submission queue event is returned, it should be filled out via one of the prep functions such as {@link #io_uring_prep_read prep_read} and submitted via {@link #io_uring_submit submit}.</p>
     *
     * @return a pointer to the next submission queue event on success and {@code NULL} on failure
     */
    @Nullable
    @NativeType("struct io_uring_sqe *")
    public static IOURingSQE io_uring_get_sqe(@NativeType("struct io_uring *") IOURing ring) {
        long __result = nio_uring_get_sqe(ring.address());
        return IOURingSQE.createSafe(__result);
    }

    // --- [ io_uring_register_buffers ] ---

    /** Unsafe version of: {@link #io_uring_register_buffers register_buffers} */
    public static native int nio_uring_register_buffers(long ring, long iovecs, int nr_iovecs);

    /**
     * Registers {@code nr_iovecs} number of buffers defined by the array {@code iovecs} belonging to the {@code ring}.
     * 
     * <p>After the caller has registered the buffers, they can be used with one of the fixed buffers functions.</p>
     *
     * @return 0 on success. On failure it returns {@code -errno}.
     */
    public static int io_uring_register_buffers(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct iovec const *") IOVec.Buffer iovecs) {
        return nio_uring_register_buffers(ring.address(), iovecs.address(), iovecs.remaining());
    }

    // --- [ io_uring_register_buffers_tags ] ---

    public static native int nio_uring_register_buffers_tags(long ring, long iovecs, long tags, int nr);

    public static int io_uring_register_buffers_tags(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct iovec const *") IOVec.Buffer iovecs, @NativeType("__u64 const *") LongBuffer tags) {
        if (CHECKS) {
            check(tags, iovecs.remaining());
        }
        return nio_uring_register_buffers_tags(ring.address(), iovecs.address(), memAddress(tags), iovecs.remaining());
    }

    // --- [ io_uring_register_buffers_update_tag ] ---

    public static native int nio_uring_register_buffers_update_tag(long ring, int off, long iovecs, long tags, int nr);

    public static int io_uring_register_buffers_update_tag(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int off, @NativeType("struct iovec const *") IOVec.Buffer iovecs, @NativeType("__u64 const *") LongBuffer tags) {
        if (CHECKS) {
            check(tags, iovecs.remaining());
        }
        return nio_uring_register_buffers_update_tag(ring.address(), off, iovecs.address(), memAddress(tags), iovecs.remaining());
    }

    // --- [ io_uring_unregister_buffers ] ---

    /** Unsafe version of: {@link #io_uring_unregister_buffers unregister_buffers} */
    public static native int nio_uring_unregister_buffers(long ring);

    /**
     * Unregisters the fixed buffers previously registered to the {@code ring}.
     *
     * @return 0 on success. On failure it returns {@code -errno}.
     */
    public static int io_uring_unregister_buffers(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_unregister_buffers(ring.address());
    }

    // --- [ io_uring_register_files ] ---

    /** Unsafe version of: {@link #io_uring_register_files register_files} */
    public static native int nio_uring_register_files(long ring, long files, int nr_files);

    /**
     * Registers {@code nr_files} number of file descriptors defined by the array {@code files} belonging to the {@code ring} for subsequent operations.
     * 
     * <p>After the caller has registered the buffers, they can be used with the submission queue polling operations.</p>
     *
     * @return 0 on success. On failure it returns {@code -errno}.
     */
    public static int io_uring_register_files(@NativeType("struct io_uring *") IOURing ring, @NativeType("int const *") IntBuffer files) {
        return nio_uring_register_files(ring.address(), memAddress(files), files.remaining());
    }

    // --- [ io_uring_register_files_tags ] ---

    public static native int nio_uring_register_files_tags(long ring, long files, long tags, int nr);

    public static int io_uring_register_files_tags(@NativeType("struct io_uring *") IOURing ring, @NativeType("int const *") IntBuffer files, @NativeType("__u64 const *") LongBuffer tags) {
        if (CHECKS) {
            check(tags, files.remaining());
        }
        return nio_uring_register_files_tags(ring.address(), memAddress(files), memAddress(tags), files.remaining());
    }

    // --- [ io_uring_register_files_update_tag ] ---

    public static native int nio_uring_register_files_update_tag(long ring, int off, long files, long tags, int nr_files);

    public static int io_uring_register_files_update_tag(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int off, @NativeType("int const *") IntBuffer files, @NativeType("__u64 const *") LongBuffer tags) {
        if (CHECKS) {
            check(tags, files.remaining());
        }
        return nio_uring_register_files_update_tag(ring.address(), off, memAddress(files), memAddress(tags), files.remaining());
    }

    // --- [ io_uring_unregister_files ] ---

    public static native int nio_uring_unregister_files(long ring);

    public static int io_uring_unregister_files(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_unregister_files(ring.address());
    }

    // --- [ io_uring_register_files_update ] ---

    public static native int nio_uring_register_files_update(long ring, int off, long files, int nr_files);

    public static int io_uring_register_files_update(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int off, @NativeType("int *") IntBuffer files) {
        return nio_uring_register_files_update(ring.address(), off, memAddress(files), files.remaining());
    }

    // --- [ io_uring_register_eventfd ] ---

    public static native int nio_uring_register_eventfd(long ring, int fd);

    public static int io_uring_register_eventfd(@NativeType("struct io_uring *") IOURing ring, int fd) {
        return nio_uring_register_eventfd(ring.address(), fd);
    }

    // --- [ io_uring_register_eventfd_async ] ---

    public static native int nio_uring_register_eventfd_async(long ring, int fd);

    public static int io_uring_register_eventfd_async(@NativeType("struct io_uring *") IOURing ring, int fd) {
        return nio_uring_register_eventfd_async(ring.address(), fd);
    }

    // --- [ io_uring_unregister_eventfd ] ---

    public static native int nio_uring_unregister_eventfd(long ring);

    public static int io_uring_unregister_eventfd(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_unregister_eventfd(ring.address());
    }

    // --- [ io_uring_register_probe ] ---

    public static native int nio_uring_register_probe(long ring, long p, int nr);

    public static int io_uring_register_probe(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_probe *") IOURingProbe p, @NativeType("unsigned") int nr) {
        return nio_uring_register_probe(ring.address(), p.address(), nr);
    }

    // --- [ io_uring_register_personality ] ---

    public static native int nio_uring_register_personality(long ring);

    public static int io_uring_register_personality(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_register_personality(ring.address());
    }

    // --- [ io_uring_unregister_personality ] ---

    public static native int nio_uring_unregister_personality(long ring, int id);

    public static int io_uring_unregister_personality(@NativeType("struct io_uring *") IOURing ring, int id) {
        return nio_uring_unregister_personality(ring.address(), id);
    }

    // --- [ io_uring_register_restrictions ] ---

    public static native int nio_uring_register_restrictions(long ring, long res, int nr_res);

    public static int io_uring_register_restrictions(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_restriction *") IOURingRestriction.Buffer res) {
        return nio_uring_register_restrictions(ring.address(), res.address(), res.remaining());
    }

    // --- [ io_uring_enable_rings ] ---

    public static native int nio_uring_enable_rings(long ring);

    public static int io_uring_enable_rings(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_enable_rings(ring.address());
    }

    // --- [ __io_uring_sqring_wait ] ---

    public static native int n__io_uring_sqring_wait(long ring);

    public static int __io_uring_sqring_wait(@NativeType("struct io_uring *") IOURing ring) {
        return n__io_uring_sqring_wait(ring.address());
    }

    // --- [ io_uring_register_iowq_aff ] ---

    public static native int nio_uring_register_iowq_aff(long ring, long cpusz, long mask);

    public static int io_uring_register_iowq_aff(@NativeType("struct io_uring *") IOURing ring, @NativeType("size_t") long cpusz, @NativeType("cpu_set_t const *") long mask) {
        if (CHECKS) {
            check(mask);
        }
        return nio_uring_register_iowq_aff(ring.address(), cpusz, mask);
    }

    // --- [ io_uring_unregister_iowq_aff ] ---

    public static native int nio_uring_unregister_iowq_aff(long ring);

    public static int io_uring_unregister_iowq_aff(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_unregister_iowq_aff(ring.address());
    }

    // --- [ io_uring_register_iowq_max_workers ] ---

    public static native int nio_uring_register_iowq_max_workers(long ring, long values);

    public static int io_uring_register_iowq_max_workers(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned int *") IntBuffer values) {
        if (CHECKS) {
            check(values, 2);
        }
        return nio_uring_register_iowq_max_workers(ring.address(), memAddress(values));
    }

    // --- [ io_uring_cqe_seen ] ---

    /** Unsafe version of: {@link #io_uring_cqe_seen cqe_seen} */
    public static native void nio_uring_cqe_seen(long ring, long cqe);

    /**
     * Marks the IO completion {@code cqe} belonging to the {@code ring} param as processed.
     * 
     * <p>After the caller has submitted a request with {@link #io_uring_submit submit}, they can retrieve the completion with {@link #io_uring_wait_cqe wait_cqe} and mark it then as processed with
     * {@code io_uring_cqe_seen()}.</p>
     * 
     * <p>Completions must be marked as completed, so their slot can get reused.</p>
     */
    public static void io_uring_cqe_seen(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe *") IOURingCQE cqe) {
        nio_uring_cqe_seen(ring.address(), cqe.address());
    }

    // --- [ io_uring_sqe_set_data ] ---

    /** Unsafe version of: {@link #io_uring_sqe_set_data sqe_set_data} */
    public static native void nio_uring_sqe_set_data(long sqe, long data);

    /**
     * Stores a {@code user_data} pointer with the submission queue entry {@code sqe}.
     * 
     * <p>After the caller has requested an submission queue entry (SQE) with {@link #io_uring_get_sqe get_sqe}, they can associate a data pointer with the SQE. Once the completion
     * arrives, the function {@link #io_uring_cqe_get_data cqe_get_data} can be called to identify the user request.</p>
     */
    public static void io_uring_sqe_set_data(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("void *") long data) {
        if (CHECKS) {
            check(data);
        }
        nio_uring_sqe_set_data(sqe.address(), data);
    }

    // --- [ io_uring_cqe_get_data ] ---

    /** Unsafe version of: {@link #io_uring_cqe_get_data cqe_get_data} */
    public static native long nio_uring_cqe_get_data(long cqe);

    /**
     * Returns the {@code user_data} with the completion queue entry {@code cqe}.
     * 
     * <p>After the caller has received a completion queue entry (CQE) with {@link #io_uring_wait_cqe wait_cqe}, they can call the {@code io_uring_cqe_get_data()} function to retrieve
     * the {@code user_data} value. This requires that {@code user_data} has been set earlier with the function {@link #io_uring_sqe_set_data sqe_set_data}.</p>
     */
    @NativeType("void *")
    public static long io_uring_cqe_get_data(@NativeType("struct io_uring_cqe const *") IOURingCQE cqe) {
        return nio_uring_cqe_get_data(cqe.address());
    }

    // --- [ io_uring_sqe_set_data64 ] ---

    /** Unsafe version of: {@link #io_uring_sqe_set_data64 sqe_set_data64} */
    public static native void nio_uring_sqe_set_data64(long sqe, long data);

    /**
     * Assign a 64-bit value to this {@code sqe}, which can get retrieved at completion time with {@link #io_uring_cqe_get_data64 cqe_get_data64}.
     * 
     * <p>Just like the non-64 variants, except these store a 64-bit type rather than a data pointer.</p>
     */
    public static void io_uring_sqe_set_data64(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("__u64") long data) {
        nio_uring_sqe_set_data64(sqe.address(), data);
    }

    // --- [ io_uring_cqe_get_data64 ] ---

    /** Unsafe version of: {@link #io_uring_cqe_get_data64 cqe_get_data64} */
    public static native long nio_uring_cqe_get_data64(long cqe);

    /** See {@link #io_uring_sqe_set_data64 sqe_set_data64}. */
    @NativeType("__u64")
    public static long io_uring_cqe_get_data64(@NativeType("struct io_uring_cqe const *") IOURingCQE cqe) {
        return nio_uring_cqe_get_data64(cqe.address());
    }

    // --- [ io_uring_sqe_set_flags ] ---

    public static native void nio_uring_sqe_set_flags(long sqe, int flags);

    public static void io_uring_sqe_set_flags(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("unsigned int") int flags) {
        nio_uring_sqe_set_flags(sqe.address(), flags);
    }

    // --- [ io_uring_prep_splice ] ---

    /** Unsafe version of: {@link #io_uring_prep_splice prep_splice} */
    public static native void nio_uring_prep_splice(long sqe, int fd_in, long off_in, int fd_out, long off_out, int nbytes, int splice_flags);

    /**
     * Precondition: Either {@code fd_in} or {@code fd_out} must be a pipe.
     * 
     * <p>This splice operation can be used to implement {@code sendfile} by splicing to an intermediate pipe first, then splice to the final destination. In
     * fact, the implementation of {@code sendfile} in kernel uses {@code splice} internally.</p>
     * 
     * <p>NOTE that even if {@code fd_in} or {@code fd_out} refers to a pipe, the splice operation can still failed with {@code EINVAL} if one of the fd doesn't
     * explicitly support splice operation, e.g. reading from terminal is unsupported from kernel 5.7 to 5.11. Check issue #291 for more information.</p>
     *
     * @param off_in       if {@code fd_in} refers to a pipe, {@code off_in} must be {@code (int64_t) -1}; If {@code fd_in} does not refer to a pipe and {@code off_in} is
     *                     {@code (int64_t) -1}, then bytes are read from {@code fd_in} starting from the file offset and it is adjust appropriately; If {@code fd_in} does
     *                     not refer to a pipe and {@code off_in} is not {@code (int64_t) -1}, then the starting {@code offset} of {@code fd_in} will be {@code off_in}.
     * @param off_out      the description of {@code off_in} also applied to {@code off_out}
     * @param splice_flags see man {@code splice(2)} for description of flags
     */
    public static void io_uring_prep_splice(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd_in, @NativeType("int64_t") long off_in, int fd_out, @NativeType("int64_t") long off_out, @NativeType("unsigned int") int nbytes, @NativeType("unsigned int") int splice_flags) {
        nio_uring_prep_splice(sqe.address(), fd_in, off_in, fd_out, off_out, nbytes, splice_flags);
    }

    // --- [ io_uring_prep_tee ] ---

    public static native void nio_uring_prep_tee(long sqe, int fd_in, int fd_out, int nbytes, int splice_flags);

    public static void io_uring_prep_tee(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd_in, int fd_out, @NativeType("unsigned int") int nbytes, @NativeType("unsigned int") int splice_flags) {
        nio_uring_prep_tee(sqe.address(), fd_in, fd_out, nbytes, splice_flags);
    }

    // --- [ io_uring_prep_readv ] ---

    /** Unsafe version of: {@link #io_uring_prep_readv prep_readv} */
    public static native void nio_uring_prep_readv(long sqe, int fd, long iovecs, int nr_vecs, int offset);

    /**
     * Prepares a vectored IO read request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start reading {@code nr_vecs} into the {@code iovecs} array at
     * the specified {@code offset}.</p>
     * 
     * <p>On files that support seeking, if the {@code offset} is set to -1, the read operation commences at the file offset, and the file offset is incremented
     * by the number of bytes read. See {@code read(2)} for more details.</p>
     * 
     * <p>On files that are not capable of seeking, the offset is ignored.</p>
     * 
     * <p>After the write has been prepared it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_readv(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct iovec const *") IOVec.Buffer iovecs, int offset) {
        nio_uring_prep_readv(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset);
    }

    // --- [ io_uring_prep_readv2 ] ---

    /** Unsafe version of: {@link #io_uring_prep_readv2 prep_readv2} */
    public static native void nio_uring_prep_readv2(long sqe, int fd, long iovecs, int nr_vecs, int offset, int flags);

    /**
     * Prepares a vectored IO read request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start reading {@code nr_vecs} into the {@code iovecs} array at
     * the specified {@code offset}.</p>
     * 
     * <p>The behavior of the function can be controlled with the {@code flags} parameter. Supported values for flags are:</p>
     * 
     * <ul>
     * <li>{@code RWF_HIPRI} - High priority request, poll if possible</li>
     * <li>{@code RWF_DSYNC} - per-IO {@code O_DSYNC}</li>
     * <li>{@code RWF_SYNC} - per-IO {@code O_SYNC}</li>
     * <li>{@code RWF_NOWAIT} - per-IO, return {@code -EAGAIN} if operation would block</li>
     * <li>{@code RWF_APPEND} - per-IO {@code O_APPEND}</li>
     * </ul>
     * 
     * <p>On files that support seeking, if the {@code offset} is set to -1, the read operation commences at the file offset, and the file offset is incremented
     * by the number of bytes read. See {@code read(2)} for more details.</p>
     * 
     * <p>On files that are not capable of seeking, the offset is ignored.</p>
     * 
     * <p>After the write has been prepared, it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_readv2(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct iovec const *") IOVec.Buffer iovecs, int offset, int flags) {
        nio_uring_prep_readv2(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset, flags);
    }

    // --- [ io_uring_prep_read_fixed ] ---

    public static native void nio_uring_prep_read_fixed(long sqe, int fd, long buf, int nbytes, int offset, int buf_index);

    public static void io_uring_prep_read_fixed(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("void *") ByteBuffer buf, int offset, int buf_index) {
        nio_uring_prep_read_fixed(sqe.address(), fd, memAddress(buf), buf.remaining(), offset, buf_index);
    }

    // --- [ io_uring_prep_writev ] ---

    /** Unsafe version of: {@link #io_uring_prep_writev prep_writev} */
    public static native void nio_uring_prep_writev(long sqe, int fd, long iovecs, int nr_vecs, int offset);

    /**
     * Prepares a vectored IO write request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start writing {@code nr_vecs} from the {@code iovecs} array at
     * the specified {@code offset}.</p>
     * 
     * <p>On files that support seeking, if the {@code offset} is set to -1, the write operation commences at the file offset, and the file offset is incremented
     * by the number of bytes written. See {@code write(2)} for more details.</p>
     * 
     * <p>On files that are not capable of seeking, the offset is ignored.</p>
     * 
     * <p>After the write has been prepared it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_writev(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct iovec const *") IOVec.Buffer iovecs, int offset) {
        nio_uring_prep_writev(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset);
    }

    // --- [ io_uring_prep_writev2 ] ---

    /** Unsafe version of: {@link #io_uring_prep_writev2 prep_writev2} */
    public static native void nio_uring_prep_writev2(long sqe, int fd, long iovecs, int nr_vecs, int offset, int flags);

    /**
     * Prepares a vectored IO write request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start writing {@code nr_vecs} from the {@code iovecs} array at
     * the specified {@code offset}.</p>
     * 
     * <p>The behavior of the function can be controlled with the {@code flags} parameter. Supported values for flags are:</p>
     * 
     * <ul>
     * <li>{@code RWF_HIPRI} - High priority request, poll if possible</li>
     * <li>{@code RWF_DSYNC} - per-IO {@code O_DSYNC}</li>
     * <li>{@code RWF_SYNC} - per-IO {@code O_SYNC}</li>
     * <li>{@code RWF_NOWAIT} - per-IO, return {@code -EAGAIN} if operation would block</li>
     * <li>{@code RWF_APPEND} - per-IO {@code O_APPEND}</li>
     * </ul>
     * 
     * <p>On files that support seeking, if the {@code offset} is set to -1, the write operation commences at the file offset, and the file offset is incremented
     * by the number of bytes written. See {@code write(2)} for more details.</p>
     * 
     * <p>On files that are not capable of seeking, the offset is ignored.</p>
     * 
     * <p>After the write has been prepared, it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_writev2(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct iovec const *") IOVec.Buffer iovecs, int offset, int flags) {
        nio_uring_prep_writev2(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset, flags);
    }

    // --- [ io_uring_prep_write_fixed ] ---

    public static native void nio_uring_prep_write_fixed(long sqe, int fd, long buf, int nbytes, int offset, int buf_index);

    public static void io_uring_prep_write_fixed(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("void const *") ByteBuffer buf, int offset, int buf_index) {
        nio_uring_prep_write_fixed(sqe.address(), fd, memAddress(buf), buf.remaining(), offset, buf_index);
    }

    // --- [ io_uring_prep_recvmsg ] ---

    public static native void nio_uring_prep_recvmsg(long sqe, int fd, long msg, int flags);

    public static void io_uring_prep_recvmsg(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct msghdr *") Msghdr msg, @NativeType("unsigned int") int flags) {
        nio_uring_prep_recvmsg(sqe.address(), fd, msg.address(), flags);
    }

    // --- [ io_uring_prep_sendmsg ] ---

    public static native void nio_uring_prep_sendmsg(long sqe, int fd, long msg, int flags);

    public static void io_uring_prep_sendmsg(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct msghdr const *") Msghdr msg, @NativeType("unsigned int") int flags) {
        if (CHECKS) {
            Msghdr.validate(msg.address());
        }
        nio_uring_prep_sendmsg(sqe.address(), fd, msg.address(), flags);
    }

    // --- [ io_uring_prep_poll_add ] ---

    public static native void nio_uring_prep_poll_add(long sqe, int fd, int poll_mask);

    public static void io_uring_prep_poll_add(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("unsigned int") int poll_mask) {
        nio_uring_prep_poll_add(sqe.address(), fd, poll_mask);
    }

    // --- [ io_uring_prep_poll_multishot ] ---

    public static native void nio_uring_prep_poll_multishot(long sqe, int fd, int poll_mask);

    public static void io_uring_prep_poll_multishot(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("unsigned int") int poll_mask) {
        nio_uring_prep_poll_multishot(sqe.address(), fd, poll_mask);
    }

    // --- [ io_uring_prep_poll_remove ] ---

    public static native void nio_uring_prep_poll_remove(long sqe, long user_data);

    public static void io_uring_prep_poll_remove(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("__u64") long user_data) {
        nio_uring_prep_poll_remove(sqe.address(), user_data);
    }

    // --- [ io_uring_prep_poll_update ] ---

    public static native void nio_uring_prep_poll_update(long sqe, long old_user_data, long new_user_data, int poll_mask, int flags);

    public static void io_uring_prep_poll_update(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("__u64") long old_user_data, @NativeType("__u64") long new_user_data, @NativeType("unsigned int") int poll_mask, @NativeType("unsigned int") int flags) {
        nio_uring_prep_poll_update(sqe.address(), old_user_data, new_user_data, poll_mask, flags);
    }

    // --- [ io_uring_prep_fsync ] ---

    public static native void nio_uring_prep_fsync(long sqe, int fd, int fsync_flags);

    public static void io_uring_prep_fsync(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("unsigned int") int fsync_flags) {
        nio_uring_prep_fsync(sqe.address(), fd, fsync_flags);
    }

    // --- [ io_uring_prep_nop ] ---

    public static native void nio_uring_prep_nop(long sqe);

    public static void io_uring_prep_nop(@NativeType("struct io_uring_sqe *") IOURingSQE sqe) {
        nio_uring_prep_nop(sqe.address());
    }

    // --- [ io_uring_prep_timeout ] ---

    public static native void nio_uring_prep_timeout(long sqe, long ts, int count, int flags);

    public static void io_uring_prep_timeout(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("struct __kernel_timespec *") KernelTimespec ts, @NativeType("unsigned int") int count, @NativeType("unsigned int") int flags) {
        nio_uring_prep_timeout(sqe.address(), ts.address(), count, flags);
    }

    // --- [ io_uring_prep_timeout_remove ] ---

    public static native void nio_uring_prep_timeout_remove(long sqe, long user_data, int flags);

    public static void io_uring_prep_timeout_remove(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("__u64") long user_data, @NativeType("unsigned int") int flags) {
        nio_uring_prep_timeout_remove(sqe.address(), user_data, flags);
    }

    // --- [ io_uring_prep_timeout_update ] ---

    public static native void nio_uring_prep_timeout_update(long sqe, long ts, long user_data, int flags);

    public static void io_uring_prep_timeout_update(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("struct __kernel_timespec *") KernelTimespec ts, @NativeType("__u64") long user_data, @NativeType("unsigned int") int flags) {
        nio_uring_prep_timeout_update(sqe.address(), ts.address(), user_data, flags);
    }

    // --- [ io_uring_prep_accept ] ---

    public static native void nio_uring_prep_accept(long sqe, int fd, long addr, long addrlen, int flags);

    public static void io_uring_prep_accept(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct sockaddr *") Sockaddr addr, @NativeType("socklen_t *") IntBuffer addrlen, int flags) {
        if (CHECKS) {
            check(addrlen, 1);
        }
        nio_uring_prep_accept(sqe.address(), fd, addr.address(), memAddress(addrlen), flags);
    }

    // --- [ io_uring_prep_accept_direct ] ---

    public static native void nio_uring_prep_accept_direct(long sqe, int fd, long addr, long addrlen, int flags, int file_index);

    public static void io_uring_prep_accept_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct sockaddr *") Sockaddr addr, @NativeType("socklen_t *") IntBuffer addrlen, int flags, @NativeType("unsigned int") int file_index) {
        if (CHECKS) {
            check(addrlen, 1);
        }
        nio_uring_prep_accept_direct(sqe.address(), fd, addr.address(), memAddress(addrlen), flags, file_index);
    }

    // --- [ io_uring_prep_cancel ] ---

    public static native void nio_uring_prep_cancel(long sqe, long user_data, int flags);

    public static void io_uring_prep_cancel(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("__u64") long user_data, int flags) {
        nio_uring_prep_cancel(sqe.address(), user_data, flags);
    }

    // --- [ io_uring_prep_link_timeout ] ---

    public static native void nio_uring_prep_link_timeout(long sqe, long ts, int flags);

    public static void io_uring_prep_link_timeout(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("struct __kernel_timespec *") KernelTimespec ts, @NativeType("unsigned int") int flags) {
        nio_uring_prep_link_timeout(sqe.address(), ts.address(), flags);
    }

    // --- [ io_uring_prep_connect ] ---

    public static native void nio_uring_prep_connect(long sqe, int fd, long addr, int addrlen);

    public static void io_uring_prep_connect(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct sockaddr const *") Sockaddr addr, @NativeType("socklen_t") int addrlen) {
        nio_uring_prep_connect(sqe.address(), fd, addr.address(), addrlen);
    }

    // --- [ io_uring_prep_files_update ] ---

    public static native void nio_uring_prep_files_update(long sqe, long fds, int nr_fds, int offset);

    public static void io_uring_prep_files_update(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("int *") IntBuffer fds, int offset) {
        nio_uring_prep_files_update(sqe.address(), memAddress(fds), fds.remaining(), offset);
    }

    // --- [ io_uring_prep_fallocate ] ---

    public static native void nio_uring_prep_fallocate(long sqe, int fd, int mode, long offset, long len);

    public static void io_uring_prep_fallocate(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, int mode, @NativeType("off_t") long offset, @NativeType("off_t") long len) {
        nio_uring_prep_fallocate(sqe.address(), fd, mode, offset, len);
    }

    // --- [ io_uring_prep_openat ] ---

    public static native void nio_uring_prep_openat(long sqe, int dfd, long path, int flags, int mode);

    public static void io_uring_prep_openat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, int flags, int mode) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_openat(sqe.address(), dfd, memAddress(path), flags, mode);
    }

    public static void io_uring_prep_openat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, int flags, int mode) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_openat(sqe.address(), dfd, pathEncoded, flags, mode);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_openat_direct ] ---

    public static native void nio_uring_prep_openat_direct(long sqe, int dfd, long path, int flags, int mode, int file_index);

    public static void io_uring_prep_openat_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, int flags, int mode, @NativeType("unsigned int") int file_index) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_openat_direct(sqe.address(), dfd, memAddress(path), flags, mode, file_index);
    }

    public static void io_uring_prep_openat_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, int flags, int mode, @NativeType("unsigned int") int file_index) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_openat_direct(sqe.address(), dfd, pathEncoded, flags, mode, file_index);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_close ] ---

    public static native void nio_uring_prep_close(long sqe, int fd);

    public static void io_uring_prep_close(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd) {
        nio_uring_prep_close(sqe.address(), fd);
    }

    // --- [ io_uring_prep_close_direct ] ---

    public static native void nio_uring_prep_close_direct(long sqe, int file_index);

    public static void io_uring_prep_close_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("unsigned int") int file_index) {
        nio_uring_prep_close_direct(sqe.address(), file_index);
    }

    // --- [ io_uring_prep_read ] ---

    /** Unsafe version of: {@link #io_uring_prep_read prep_read} */
    public static native void nio_uring_prep_read(long sqe, int fd, long buf, int nbytes, int offset);

    /**
     * Prepares an IO read request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start reading {@code nbytes} into the buffer {@code buf} at
     * the specified {@code offset}.</p>
     * 
     * <p>On files that support seeking, if the {@code offset} is set to -1, the read operation commences at the file offset, and the file offset is incremented
     * by the number of bytes read. See {@code read(2)} for more details.</p>
     * 
     * <p>On files that are not capable of seeking, the {@code offset} is ignored.</p>
     * 
     * <p>After the read has been prepared it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_read(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("void *") ByteBuffer buf, int offset) {
        nio_uring_prep_read(sqe.address(), fd, memAddress(buf), buf.remaining(), offset);
    }

    // --- [ io_uring_prep_write ] ---

    /** Unsafe version of: {@link #io_uring_prep_write prep_write} */
    public static native void nio_uring_prep_write(long sqe, int fd, long buf, int nbytes, int offset);

    /**
     * Prepares an IO write request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start writing {@code nbytes} from the buffer {@code buf} at
     * the specified {@code offset}.</p>
     * 
     * <p>On files that support seeking, if the {@code offset} is set to -1, the write operation commences at the file offset, and the file offset is incremented
     * by the number of bytes written. See {@code write(2)} for more details.</p>
     * 
     * <p>On files that are not capable of seeking, the offset is ignored.</p>
     * 
     * <p>After the write has been prepared, it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_write(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("void const *") ByteBuffer buf, int offset) {
        nio_uring_prep_write(sqe.address(), fd, memAddress(buf), buf.remaining(), offset);
    }

    // --- [ io_uring_prep_statx ] ---

    public static native void nio_uring_prep_statx(long sqe, int dfd, long path, int flags, int mask, long statxbuf);

    public static void io_uring_prep_statx(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, int flags, @NativeType("unsigned int") int mask, @NativeType("struct statx *") Statx statxbuf) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_statx(sqe.address(), dfd, memAddress(path), flags, mask, statxbuf.address());
    }

    public static void io_uring_prep_statx(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, int flags, @NativeType("unsigned int") int mask, @NativeType("struct statx *") Statx statxbuf) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_statx(sqe.address(), dfd, pathEncoded, flags, mask, statxbuf.address());
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_fadvise ] ---

    public static native void nio_uring_prep_fadvise(long sqe, int fd, int offset, long len, int advice);

    public static void io_uring_prep_fadvise(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, int offset, @NativeType("off_t") long len, int advice) {
        nio_uring_prep_fadvise(sqe.address(), fd, offset, len, advice);
    }

    // --- [ io_uring_prep_madvise ] ---

    public static native void nio_uring_prep_madvise(long sqe, long addr, long length, int advice);

    public static void io_uring_prep_madvise(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("void *") ByteBuffer addr, int advice) {
        nio_uring_prep_madvise(sqe.address(), memAddress(addr), addr.remaining(), advice);
    }

    // --- [ io_uring_prep_send ] ---

    public static native void nio_uring_prep_send(long sqe, int sockfd, long buf, long len, int flags);

    public static void io_uring_prep_send(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType("void const *") ByteBuffer buf, int flags) {
        nio_uring_prep_send(sqe.address(), sockfd, memAddress(buf), buf.remaining(), flags);
    }

    // --- [ io_uring_prep_recv ] ---

    public static native void nio_uring_prep_recv(long sqe, int sockfd, long buf, long len, int flags);

    public static void io_uring_prep_recv(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType("void *") ByteBuffer buf, int flags) {
        nio_uring_prep_recv(sqe.address(), sockfd, memAddress(buf), buf.remaining(), flags);
    }

    // --- [ io_uring_prep_openat2 ] ---

    public static native void nio_uring_prep_openat2(long sqe, int dfd, long path, long how);

    public static void io_uring_prep_openat2(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, @NativeType("struct open_how *") OpenHow how) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_openat2(sqe.address(), dfd, memAddress(path), how.address());
    }

    public static void io_uring_prep_openat2(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, @NativeType("struct open_how *") OpenHow how) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_openat2(sqe.address(), dfd, pathEncoded, how.address());
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_openat2_direct ] ---

    /** Unsafe version of: {@link #io_uring_prep_openat2_direct prep_openat2_direct} */
    public static native void nio_uring_prep_openat2_direct(long sqe, int dfd, long path, long how, int file_index);

    /** open directly into the fixed file table */
    public static void io_uring_prep_openat2_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, @NativeType("struct open_how *") OpenHow how, @NativeType("unsigned int") int file_index) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_openat2_direct(sqe.address(), dfd, memAddress(path), how.address(), file_index);
    }

    /** open directly into the fixed file table */
    public static void io_uring_prep_openat2_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, @NativeType("struct open_how *") OpenHow how, @NativeType("unsigned int") int file_index) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_openat2_direct(sqe.address(), dfd, pathEncoded, how.address(), file_index);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_epoll_ctl ] ---

    public static native void nio_uring_prep_epoll_ctl(long sqe, int epfd, int fd, int op, long ev);

    public static void io_uring_prep_epoll_ctl(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int epfd, int fd, int op, @NativeType("struct epoll_event *") EpollEvent ev) {
        nio_uring_prep_epoll_ctl(sqe.address(), epfd, fd, op, ev.address());
    }

    // --- [ io_uring_prep_provide_buffers ] ---

    public static native void nio_uring_prep_provide_buffers(long sqe, long addr, int len, int nr, int bgid, int bid);

    public static void io_uring_prep_provide_buffers(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("void *") ByteBuffer addr, int nr, int bgid, int bid) {
        nio_uring_prep_provide_buffers(sqe.address(), memAddress(addr), addr.remaining(), nr, bgid, bid);
    }

    // --- [ io_uring_prep_remove_buffers ] ---

    public static native void nio_uring_prep_remove_buffers(long sqe, int nr, int bgid);

    public static void io_uring_prep_remove_buffers(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int nr, int bgid) {
        nio_uring_prep_remove_buffers(sqe.address(), nr, bgid);
    }

    // --- [ io_uring_prep_shutdown ] ---

    public static native void nio_uring_prep_shutdown(long sqe, int fd, int how);

    public static void io_uring_prep_shutdown(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, int how) {
        nio_uring_prep_shutdown(sqe.address(), fd, how);
    }

    // --- [ io_uring_prep_unlinkat ] ---

    public static native void nio_uring_prep_unlinkat(long sqe, int dfd, long path, int flags);

    public static void io_uring_prep_unlinkat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, int flags) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_unlinkat(sqe.address(), dfd, memAddress(path), flags);
    }

    public static void io_uring_prep_unlinkat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, int flags) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_unlinkat(sqe.address(), dfd, pathEncoded, flags);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_renameat ] ---

    public static native void nio_uring_prep_renameat(long sqe, int olddfd, long oldpath, int newdfd, long newpath, int flags);

    public static void io_uring_prep_renameat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType("char const *") ByteBuffer oldpath, int newdfd, @NativeType("char const *") ByteBuffer newpath, int flags) {
        if (CHECKS) {
            checkNT1(oldpath);
            checkNT1(newpath);
        }
        nio_uring_prep_renameat(sqe.address(), olddfd, memAddress(oldpath), newdfd, memAddress(newpath), flags);
    }

    public static void io_uring_prep_renameat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType("char const *") CharSequence oldpath, int newdfd, @NativeType("char const *") CharSequence newpath, int flags) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(oldpath, true);
            long oldpathEncoded = stack.getPointerAddress();
            stack.nUTF8(newpath, true);
            long newpathEncoded = stack.getPointerAddress();
            nio_uring_prep_renameat(sqe.address(), olddfd, oldpathEncoded, newdfd, newpathEncoded, flags);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_sync_file_range ] ---

    public static native void nio_uring_prep_sync_file_range(long sqe, int fd, int len, int offset, int flags);

    public static void io_uring_prep_sync_file_range(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("unsigned int") int len, int offset, int flags) {
        nio_uring_prep_sync_file_range(sqe.address(), fd, len, offset, flags);
    }

    // --- [ io_uring_prep_mkdirat ] ---

    public static native void nio_uring_prep_mkdirat(long sqe, int dfd, long path, int mode);

    public static void io_uring_prep_mkdirat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") ByteBuffer path, int mode) {
        if (CHECKS) {
            checkNT1(path);
        }
        nio_uring_prep_mkdirat(sqe.address(), dfd, memAddress(path), mode);
    }

    public static void io_uring_prep_mkdirat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType("char const *") CharSequence path, int mode) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            nio_uring_prep_mkdirat(sqe.address(), dfd, pathEncoded, mode);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_symlinkat ] ---

    public static native void nio_uring_prep_symlinkat(long sqe, long target, int newdirfd, long linkpath);

    public static void io_uring_prep_symlinkat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("char const *") ByteBuffer target, int newdirfd, @NativeType("char const *") ByteBuffer linkpath) {
        if (CHECKS) {
            checkNT1(target);
            checkNT1(linkpath);
        }
        nio_uring_prep_symlinkat(sqe.address(), memAddress(target), newdirfd, memAddress(linkpath));
    }

    public static void io_uring_prep_symlinkat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("char const *") CharSequence target, int newdirfd, @NativeType("char const *") CharSequence linkpath) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(target, true);
            long targetEncoded = stack.getPointerAddress();
            stack.nUTF8(linkpath, true);
            long linkpathEncoded = stack.getPointerAddress();
            nio_uring_prep_symlinkat(sqe.address(), targetEncoded, newdirfd, linkpathEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_linkat ] ---

    public static native void nio_uring_prep_linkat(long sqe, int olddfd, long oldpath, int newdfd, long newpath, int flags);

    public static void io_uring_prep_linkat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType("char const *") ByteBuffer oldpath, int newdfd, @NativeType("char const *") ByteBuffer newpath, int flags) {
        if (CHECKS) {
            checkNT1(oldpath);
            checkNT1(newpath);
        }
        nio_uring_prep_linkat(sqe.address(), olddfd, memAddress(oldpath), newdfd, memAddress(newpath), flags);
    }

    public static void io_uring_prep_linkat(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType("char const *") CharSequence oldpath, int newdfd, @NativeType("char const *") CharSequence newpath, int flags) {
        MemoryStack stack = stackGet(); int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(oldpath, true);
            long oldpathEncoded = stack.getPointerAddress();
            stack.nUTF8(newpath, true);
            long newpathEncoded = stack.getPointerAddress();
            nio_uring_prep_linkat(sqe.address(), olddfd, oldpathEncoded, newdfd, newpathEncoded, flags);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    // --- [ io_uring_prep_getdents ] ---

    /** Unsafe version of: {@link #io_uring_prep_getdents prep_getdents} */
    public static native void nio_uring_prep_getdents(long sqe, int fd, long buf, int count, long offset);

    /**
     * Prepares a {@code getdents64} request.
     * 
     * <p>The submission queue entry {@code sqe} is setup to use the file descriptor {@code fd} to start writing up to {@code count} bytes into the buffer
     * {@code buf} starting at {@code offset}.</p>
     * 
     * <p>After the {@code getdents} call has been prepared it can be submitted with one of the submit functions.</p>
     */
    public static void io_uring_prep_getdents(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("void *") ByteBuffer buf, @NativeType("uint64_t") long offset) {
        nio_uring_prep_getdents(sqe.address(), fd, memAddress(buf), buf.remaining(), offset);
    }

    // --- [ io_uring_sq_ready ] ---

    /** Unsafe version of: {@link #io_uring_sq_ready sq_ready} */
    public static native int nio_uring_sq_ready(long ring);

    /** Returns number of unconsumed (if {@code SQPOLL}) or unsubmitted entries exist in the SQ ring. */
    @NativeType("unsigned int")
    public static int io_uring_sq_ready(@NativeType("struct io_uring const *") IOURing ring) {
        if (CHECKS) {
            IOURing.validate(ring.address());
        }
        return nio_uring_sq_ready(ring.address());
    }

    // --- [ io_uring_sq_space_left ] ---

    /** Unsafe version of: {@link #io_uring_sq_space_left sq_space_left} */
    public static native int nio_uring_sq_space_left(long ring);

    /** Returns how much space is left in the SQ ring. */
    @NativeType("unsigned int")
    public static int io_uring_sq_space_left(@NativeType("struct io_uring const *") IOURing ring) {
        if (CHECKS) {
            IOURing.validate(ring.address());
        }
        return nio_uring_sq_space_left(ring.address());
    }

    // --- [ io_uring_sqring_wait ] ---

    /** Unsafe version of: {@link #io_uring_sqring_wait sqring_wait} */
    public static native int nio_uring_sqring_wait(long ring);

    /**
     * Only applicable when using {@code SQPOLL} - allows the caller to wait for space to free up in the SQ ring, which happens when the kernel side thread
     * has consumed one or more entries.
     * 
     * <p>If the SQ ring is currently non-full, no action is taken. Note: may return {@code -EINVAL} if the kernel doesn't support this feature.</p>
     */
    public static int io_uring_sqring_wait(@NativeType("struct io_uring *") IOURing ring) {
        return nio_uring_sqring_wait(ring.address());
    }

    // --- [ io_uring_cq_ready ] ---

    /** Unsafe version of: {@link #io_uring_cq_ready cq_ready} */
    public static native int nio_uring_cq_ready(long ring);

    /** Returns how many unconsumed entries are ready in the CQ ring. */
    @NativeType("unsigned int")
    public static int io_uring_cq_ready(@NativeType("struct io_uring const *") IOURing ring) {
        if (CHECKS) {
            IOURing.validate(ring.address());
        }
        return nio_uring_cq_ready(ring.address());
    }

    // --- [ io_uring_cq_eventfd_enabled ] ---

    /** Unsafe version of: {@link #io_uring_cq_eventfd_enabled cq_eventfd_enabled} */
    public static native boolean nio_uring_cq_eventfd_enabled(long ring);

    /** Returns true if the {@code eventfd} notification is currently enabled. */
    @NativeType("bool")
    public static boolean io_uring_cq_eventfd_enabled(@NativeType("struct io_uring const *") IOURing ring) {
        if (CHECKS) {
            IOURing.validate(ring.address());
        }
        return nio_uring_cq_eventfd_enabled(ring.address());
    }

    // --- [ io_uring_cq_eventfd_toggle ] ---

    /** Unsafe version of: {@link #io_uring_cq_eventfd_toggle cq_eventfd_toggle} */
    public static native int nio_uring_cq_eventfd_toggle(long ring, boolean enabled);

    /** Toggle {@code eventfd} notification on or off, if an {@code eventfd} is registered with the ring. */
    public static int io_uring_cq_eventfd_toggle(@NativeType("struct io_uring *") IOURing ring, @NativeType("bool") boolean enabled) {
        return nio_uring_cq_eventfd_toggle(ring.address(), enabled);
    }

    // --- [ io_uring_wait_cqe_nr ] ---

    /** Unsafe version of: {@link #io_uring_wait_cqe_nr wait_cqe_nr} */
    public static native int nio_uring_wait_cqe_nr(long ring, long cqe_ptr, int wait_nr);

    /**
     * Returns {@code wait_nr} IO completion events from the queue belonging to the {@code ring} param, waiting for it if necessary. The {@code cqe_ptr} param
     * is filled in on success.
     * 
     * <p>After the caller has submitted a request with {@link #io_uring_submit submit}, they can retrieve the completion with {@code io_uring_wait_cqe_nr()}.</p>
     *
     * @return 0 on success and the {@code cqe_ptr} param is filled in. On failure it returns {@code -errno}.
     */
    public static int io_uring_wait_cqe_nr(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqe_ptr) {
        return nio_uring_wait_cqe_nr(ring.address(), memAddress(cqe_ptr), cqe_ptr.remaining());
    }

    // --- [ io_uring_peek_cqe ] ---

    /** Unsafe version of: {@link #io_uring_peek_cqe peek_cqe} */
    public static native int nio_uring_peek_cqe(long ring, long cqe_ptr);

    /**
     * Returns an IO completion, if one is readily available.
     *
     * @return 0 with {@code cqe_ptr} filled in on success, {@code -errno} on failure
     */
    public static int io_uring_peek_cqe(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqe_ptr) {
        if (CHECKS) {
            check(cqe_ptr, 1);
        }
        return nio_uring_peek_cqe(ring.address(), memAddress(cqe_ptr));
    }

    // --- [ io_uring_wait_cqe ] ---

    /** Unsafe version of: {@link #io_uring_wait_cqe wait_cqe} */
    public static native int nio_uring_wait_cqe(long ring, long cqe_ptr);

    /**
     * Returns an IO completion from the queue belonging to the {@code ring} param, waiting for it if necessary. The {@code cqe_ptr} param is filled in on
     * success.
     * 
     * <p>After the caller has submitted a request with {@link #io_uring_submit submit}, they can retrieve the completion with {@code io_uring_wait_cqe()}.</p>
     *
     * @return 0 on success and the {@code cqe_ptr} param is filled in. On failure it returns {@code -errno}.
     */
    public static int io_uring_wait_cqe(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct io_uring_cqe **") PointerBuffer cqe_ptr) {
        if (CHECKS) {
            check(cqe_ptr, 1);
        }
        return nio_uring_wait_cqe(ring.address(), memAddress(cqe_ptr));
    }

    // --- [ io_uring_mlock_size ] ---

    /**
     * Return required {@code ulimit -l} memory space for a given ring setup. See {@link #io_uring_mlock_size_params mlock_size_params}.
     *
     * @param flags {@code io_uring_params} flags
     */
    public static native int io_uring_mlock_size(@NativeType("unsigned") int entries, @NativeType("unsigned") int flags);

    // --- [ io_uring_mlock_size_params ] ---

    /** Unsafe version of: {@link #io_uring_mlock_size_params mlock_size_params} */
    public static native int nio_uring_mlock_size_params(int entries, long p);

    /**
     * Returns the required {@code ulimit -l memlock} memory required for a given ring setup, in bytes.
     * 
     * <p>May return {@code -errno} on error. On newer (5.12+) kernels, {@code io_uring} no longer requires any {@code memlock} memory, and hence this function
     * will return 0 for that case. On older (5.11 and prior) kernels, this will return the required memory so that the caller can ensure that enough space is
     * available before setting up a ring with the specified parameters.</p>
     */
    public static int io_uring_mlock_size_params(@NativeType("unsigned") int entries, @NativeType("struct io_uring_params *") IOURingParams p) {
        return nio_uring_mlock_size_params(entries, p.address());
    }

    /** Array version of: {@link #nio_uring_register_buffers_tags} */
    public static native int nio_uring_register_buffers_tags(long ring, long iovecs, long[] tags, int nr);

    /** Array version of: {@link #io_uring_register_buffers_tags register_buffers_tags} */
    public static int io_uring_register_buffers_tags(@NativeType("struct io_uring *") IOURing ring, @NativeType("struct iovec const *") IOVec.Buffer iovecs, @NativeType("__u64 const *") long[] tags) {
        if (CHECKS) {
            check(tags, iovecs.remaining());
        }
        return nio_uring_register_buffers_tags(ring.address(), iovecs.address(), tags, iovecs.remaining());
    }

    /** Array version of: {@link #nio_uring_register_buffers_update_tag} */
    public static native int nio_uring_register_buffers_update_tag(long ring, int off, long iovecs, long[] tags, int nr);

    /** Array version of: {@link #io_uring_register_buffers_update_tag register_buffers_update_tag} */
    public static int io_uring_register_buffers_update_tag(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int off, @NativeType("struct iovec const *") IOVec.Buffer iovecs, @NativeType("__u64 const *") long[] tags) {
        if (CHECKS) {
            check(tags, iovecs.remaining());
        }
        return nio_uring_register_buffers_update_tag(ring.address(), off, iovecs.address(), tags, iovecs.remaining());
    }

    /** Array version of: {@link #nio_uring_register_files} */
    public static native int nio_uring_register_files(long ring, int[] files, int nr_files);

    /** Array version of: {@link #io_uring_register_files register_files} */
    public static int io_uring_register_files(@NativeType("struct io_uring *") IOURing ring, @NativeType("int const *") int[] files) {
        return nio_uring_register_files(ring.address(), files, files.length);
    }

    /** Array version of: {@link #nio_uring_register_files_tags} */
    public static native int nio_uring_register_files_tags(long ring, int[] files, long[] tags, int nr);

    /** Array version of: {@link #io_uring_register_files_tags register_files_tags} */
    public static int io_uring_register_files_tags(@NativeType("struct io_uring *") IOURing ring, @NativeType("int const *") int[] files, @NativeType("__u64 const *") long[] tags) {
        if (CHECKS) {
            check(tags, files.length);
        }
        return nio_uring_register_files_tags(ring.address(), files, tags, files.length);
    }

    /** Array version of: {@link #nio_uring_register_files_update_tag} */
    public static native int nio_uring_register_files_update_tag(long ring, int off, int[] files, long[] tags, int nr_files);

    /** Array version of: {@link #io_uring_register_files_update_tag register_files_update_tag} */
    public static int io_uring_register_files_update_tag(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int off, @NativeType("int const *") int[] files, @NativeType("__u64 const *") long[] tags) {
        if (CHECKS) {
            check(tags, files.length);
        }
        return nio_uring_register_files_update_tag(ring.address(), off, files, tags, files.length);
    }

    /** Array version of: {@link #nio_uring_register_files_update} */
    public static native int nio_uring_register_files_update(long ring, int off, int[] files, int nr_files);

    /** Array version of: {@link #io_uring_register_files_update register_files_update} */
    public static int io_uring_register_files_update(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned") int off, @NativeType("int *") int[] files) {
        return nio_uring_register_files_update(ring.address(), off, files, files.length);
    }

    /** Array version of: {@link #nio_uring_register_iowq_max_workers} */
    public static native int nio_uring_register_iowq_max_workers(long ring, int[] values);

    /** Array version of: {@link #io_uring_register_iowq_max_workers register_iowq_max_workers} */
    public static int io_uring_register_iowq_max_workers(@NativeType("struct io_uring *") IOURing ring, @NativeType("unsigned int *") int[] values) {
        if (CHECKS) {
            check(values, 2);
        }
        return nio_uring_register_iowq_max_workers(ring.address(), values);
    }

    /** Array version of: {@link #nio_uring_prep_accept} */
    public static native void nio_uring_prep_accept(long sqe, int fd, long addr, int[] addrlen, int flags);

    /** Array version of: {@link #io_uring_prep_accept prep_accept} */
    public static void io_uring_prep_accept(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct sockaddr *") Sockaddr addr, @NativeType("socklen_t *") int[] addrlen, int flags) {
        if (CHECKS) {
            check(addrlen, 1);
        }
        nio_uring_prep_accept(sqe.address(), fd, addr.address(), addrlen, flags);
    }

    /** Array version of: {@link #nio_uring_prep_accept_direct} */
    public static native void nio_uring_prep_accept_direct(long sqe, int fd, long addr, int[] addrlen, int flags, int file_index);

    /** Array version of: {@link #io_uring_prep_accept_direct prep_accept_direct} */
    public static void io_uring_prep_accept_direct(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType("struct sockaddr *") Sockaddr addr, @NativeType("socklen_t *") int[] addrlen, int flags, @NativeType("unsigned int") int file_index) {
        if (CHECKS) {
            check(addrlen, 1);
        }
        nio_uring_prep_accept_direct(sqe.address(), fd, addr.address(), addrlen, flags, file_index);
    }

    /** Array version of: {@link #nio_uring_prep_files_update} */
    public static native void nio_uring_prep_files_update(long sqe, int[] fds, int nr_fds, int offset);

    /** Array version of: {@link #io_uring_prep_files_update prep_files_update} */
    public static void io_uring_prep_files_update(@NativeType("struct io_uring_sqe *") IOURingSQE sqe, @NativeType("int *") int[] fds, int offset) {
        nio_uring_prep_files_update(sqe.address(), fds, fds.length, offset);
    }

}