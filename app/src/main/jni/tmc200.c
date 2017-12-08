//
// Created by uidq0655 on 2017/12/5.
//
#include "com_desay_uidq0655_smartcard_Tmc200.h"
#include "tmc200.h"

#include "7816.h"
#include "uart.h"

#define PWM_DIR        "/sys/class/pwm/pwmchip2/"
#define PWM_CHANNEL    "pwm0/"

config_device_t config_device_list[] = {
        {PWM_DIR"export",                  "0",   TYPE_STR},
        {PWM_DIR PWM_CHANNEL "period",     "280", TYPE_STR},
        {PWM_DIR PWM_CHANNEL "duty_cycle", "140", TYPE_STR},
        {PWM_DIR PWM_CHANNEL "enable",     "1",   TYPE_STR},
        {"/sys/class/gpio/export",         RST_GPIO,        TYPE_STR},
        {"/sys/class/gpio/gpio"RST_GPIO"/direction", "out", TYPE_STR},
        {"/sys/class/gpio/gpio"RST_GPIO"/value",     "1",   TYPE_STR},
        {NULL,                                       NULL,  TYPE_NONE}
};

config_device_t unconfig_device_list[] = {
        {"/sys/class/gpio/unexport", RST_GPIO,        TYPE_STR},
        {"/sys/class/pwm/pwmchip2/pwm0/enable", "0",  TYPE_STR},
        {"/sys/class/pwm/pwmchip2/unexport",    "0",  TYPE_STR},
        {NULL,                                  NULL, TYPE_NONE}
};

config_device_t tmc200_reset_low[] = {
        {"/sys/class/gpio/gpio"RST_GPIO"/value", "0",  TYPE_STR},
        {NULL,                                   NULL, TYPE_NONE}
};

config_device_t tmc200_reset_high[] = {
        {"/sys/class/gpio/gpio"RST_GPIO"/value", "1",  TYPE_STR},
        {NULL,                                   NULL, TYPE_NONE}
};

int write_file_config(char *loc, char *str, config_type_e type) {
    int ret = 0;
    int fd;
    int count;
    long val;
    int write_only = 0;

    fd = open(loc, O_SYNC | O_RDWR);
    if (fd == -1) {
        write_only = 1;
        fd = open(loc, O_SYNC | O_WRONLY);
        if (fd == -1) {
            err("Open file[%s] fail\n", loc);
            return -1;
        }
    }
#ifdef DEBUG
    /* Read */
    if (write_only == 0) {
        char buf[512];
        int i, j;
        count = read(fd, buf, sizeof(buf));
        for(i=0; i<(count+15)/16; i++){
            debug("%-8x: ", i*16);
            for(j=0; j<16; j++){
                debug("%4x", buf[i*16+j]);
                if(i*16+j > count){
                    break;
                }
            }
            debug("\n");
        }
        debug("\n\n");
    }
#endif
    switch (type) {
        case TYPE_STR:
            count = write(fd, str, strlen(str));
            if ((count == -1) || (count < strlen(str))) {
                err("Write %s[%s], only %d bytes write\n",
                    loc, str, count);
                ret = -1;
            }
            break;

        default:
            err("Unknow type: %d\n", type);
            break;
    }

    close(fd);

    return ret;
}

int reset_config_status(int status) {
    int ret = 0;
    config_device_t *pconf;
    if (status == 0) {
        pconf = tmc200_reset_low;
    } else {
        pconf = tmc200_reset_high;
    }
    ret = write_file_config(pconf->path, pconf->val, pconf->type);
    if (ret != 0) {
        return ret;
    }

    return ret;
}

int check_device_path(void) {
    int ret = 0;
    struct stat fstat;
    ret = stat(PWM_DIR, &fstat);
    if (ret < 0) {
        err("PWM is not enable in dts\n");
        return ret;
    }
    ret = stat(PWM_DIR PWM_CHANNEL, &fstat);
    if (ret == 0) {
        err("PWM %s is exist\n", PWM_CHANNEL);
        return ret;
    }
    config_device_t *pconf;
    for (pconf = config_device_list; pconf->path != NULL; pconf++) {
        ret = write_file_config(pconf->path, pconf->val, pconf->type);
        if (ret != 0) {
            return ret;
        }
    }

    return 0;
}

int unconfig_device_path(void) {
    int ret = 0;

    config_device_t *pconf;
    for (pconf = unconfig_device_list; pconf->path != NULL; pconf++) {
        ret = write_file_config(pconf->path, pconf->val, pconf->type);
        if (ret != 0) {
            err("Unconfig %s[%s] fail\n", pconf->path, pconf->val);
        }
    }

    return 0;
}

JNIEXPORT jboolean JNICALL Java_com_desay_openmobile_Tmc200_open
        (JNIEnv *env, jobject obj)
{
    int ret;
    if (check_device_path() == 0) {
        init_uart(UART_DEV);
        if (ret < 0) {
            return (jboolean)0;
        }
    }
    return (jboolean)1;
}

JNIEXPORT jbyteArray JNICALL Java_com_desay_openmobile_Tmc200_transmit
        (JNIEnv *env, jobject obj, jbyteArray array) {
    jbyte *arrayBody = (*env)->GetByteArrayElements(env, array, 0);
    jsize theArrayLengthJ = (*env)->GetArrayLength(env, array);
    char *command = (char *) arrayBody;
    jbyteArray bytes = 0;
    int len = 0;
    char *ret_cmd;
    int ret;

    LOGD("Command:");
    dump_memory(command, theArrayLengthJ);

    set_apdu_buf(command, theArrayLengthJ);
    ret = trans_t0();
    if (ret == 0) {
        err("Execute cmd fail\n");
    } else {
        len = get_apdu_length();
        ret_cmd = get_apdu();
        dump_memory(ret_cmd, len);

        bytes = (*env)->NewByteArray(env, len);
        if (bytes != 0) {
            (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *) ret_cmd);
        }
    }

    (*env)->ReleaseByteArrayElements(env, array, arrayBody, JNI_COMMIT);

    return bytes;
}


JNIEXPORT jbyteArray JNICALL Java_com_desay_openmobile_Tmc200_reset
        (JNIEnv *env, jobject obj)
{
    jbyteArray bytes = 0;
    char *response;
    int len = 0;

    len = CARDreset();
    if(len > 0) {
        bytes = (*env)->NewByteArray(env, len);
        if (bytes != 0) {
            (*env)->SetByteArrayRegion(env, bytes, 0, get_apdu_length(),
                                       (jbyte *) get_apdu());
        }
    }

    return bytes;
}

JNIEXPORT jbyteArray JNICALL Java_com_desay_openmobile_Tmc200_getATR
        (JNIEnv *env, jobject obj)
{
    jbyteArray bytes = 0;
    char *response;
    int len = 0;

    len = CARDreset();
    if(len > 0) {
        bytes = (*env)->NewByteArray(env, len);
        if (bytes != 0) {
            (*env)->SetByteArrayRegion(env, bytes, 0, get_apdu_length(),
                                       (jbyte *) get_apdu());
        }
    }

    return bytes;
}

JNIEXPORT jboolean JNICALL Java_com_desay_openmobile_Tmc200_close
        (JNIEnv *env, jobject obj)
{
    unconfig_device_path();
    return (jboolean)1;
}