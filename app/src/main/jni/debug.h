//
// Created by uidq0655 on 2017/12/6.
//

#ifndef SMARTCARD_DEBUG_H
#define SMARTCARD_DEBUG_H

#include <android/log.h>

#define TAG "TMC200_JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)

#define debug(f, a...) LOGD("%s(%d):" f,  __func__, errno , ## a)
#define err(f, a...) LOGE("%s(%d):" f,  __func__, errno , ## a)

static __inline int dump_memory(const char *buf, int count) {
    int ret = 0;
    int i, j;
    char print_buf[1024];
    for (i = 0; i < (count + 15) / 16; i++) {
        sprintf(print_buf, "%-8x: ", i * 16);
        for (j = 0; j < 16; j++) {
            if (i * 16 + j >= count) {
                break;
            }
            sprintf(print_buf, "%s%4x", print_buf, buf[i * 16 + j]);
        }
        LOGD("%s", print_buf);
    }

    return ret;
}

#endif //SMARTCARD_DEBUG_H
