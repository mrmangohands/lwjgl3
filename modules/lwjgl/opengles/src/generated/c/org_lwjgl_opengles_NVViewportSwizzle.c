/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
#include "common_tools.h"
#include "opengles.h"

typedef void (APIENTRY *glViewportSwizzleNVPROC) (jint, jint, jint, jint, jint);

EXTERN_C_ENTER

JNIEXPORT void JNICALL Java_org_lwjgl_opengles_NVViewportSwizzle_glViewportSwizzleNV(JNIEnv *__env, jclass clazz, jint index, jint swizzlex, jint swizzley, jint swizzlez, jint swizzlew) {
    glViewportSwizzleNVPROC glViewportSwizzleNV = (glViewportSwizzleNVPROC)tlsGetFunction(780);
    UNUSED_PARAM(clazz)
    glViewportSwizzleNV(index, swizzlex, swizzley, swizzlez, swizzlew);
}

EXTERN_C_EXIT
