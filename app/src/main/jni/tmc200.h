//
// Created by uidq0655 on 2017/12/6.
//

#ifndef SMARTCARD_TMC200_H
#define SMARTCARD_TMC200_H

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <termios.h>
#include <sys/mman.h>

#include "debug.h"

typedef enum{
    TYPE_STR,
    TYPE_NONE
}config_type_e;

typedef struct{
    char *path;
    char *val;
    config_type_e type;
}config_device_t;

#define UART_DEV	"/dev/ttyS6"
#define RST_GPIO	"79"

#define REG_BASE   (0x4A003000)
#define REG_SIZE        1024*4

typedef struct {
    unsigned char *dev;
    unsigned int uart_fd;
} tmc200_t;

int reset_config_status(int status);

#endif //SMARTCARD_TMC200_H
