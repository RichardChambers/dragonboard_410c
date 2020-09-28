//
// Created by rchamber on 9/24/2020.
//

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <math.h>
#include <errno.h>
#include <poll.h>

#include <jni.h>

#include "PollFileService.h"

PollFileService::PollFileService(const char *pathName /* = nullptr */, int timeMilliSec /* = -1 */) : iValue(23), fd(-1)
{
    if (pathName) {
        fd = open (pathName, O_RDONLY);
    }
}

PollFileService::~PollFileService()
{
    if (fd >= 0) {
        close (fd);
        fd = -1;
    }
}

int PollFileService::PollFileCheck(const char *pathName, int timeMilliSec /* = -1 */)
{
    struct pollfd fdList[] = {
            {fd, POLLIN | POLLPRI, 0},
            {0}
        };
    nfds_t nfds = 1;

    if (fd < 0 && pathName) {
        fd = open (pathName, O_RDONLY);
        fdList[0].fd = fd;
    }
    int iStatus = PollErrorUNKNOWN;
    int iRet = poll(fdList, nfds, timeMilliSec);

    if (iRet == 0) {
        iStatus = PollTimeOut;
    } else if (iRet < 0) {
        switch (errno) {
            case EFAULT:
                iStatus = PollErrorEFAULT;
                break;
            case EINTR:
                iStatus = PollErrorEINTR;
                break;
            case EINVAL:
                iStatus = PollErrorEINVAL;
                break;
            case ENOMEM:
                iStatus = PollErrorENOMEM;
                break;
            default:
                iStatus = PollErrorUNKNOWN;
                break;
        }
    } else if (iRet > 0) {
        // successful call now determine what we should return.
        switch (fdList[0].revents & (POLLIN | POLLPRI | POLLERR | POLLNVAL)) {
            case (POLLIN):
            case (POLLPRI):
            case (POLLIN | POLLPRI):
                iStatus = PollSuccess;
                break;

            case (POLLERR):
            case (POLLNVAL):
            case (POLLERR | POLLNVAL):
                iStatus = PollErrorPOLLERR;
                break;

            default:
                iStatus = PollErrorPOLLERR;
                break;
        }
    }

    return iStatus;
}

int PollFileService::PollFileRead (const char *pathName /* = nullptr */)
{
    char  buffer[12] = {0};
    int iRet = -1;

    if (fd < 0 && pathName) {
        fd = open (pathName, O_RDONLY);
    }
    int nCount = read (fd, buffer, 10);
    if (nCount > 0) {
        iRet = atoi (buffer);
    }

    return iRet;
}

// Check the specified file using the poll(2) service and
// return a status as follows:
//  -    0  -> poll(2) success indicating something is available
//  -    1  -> poll(2) failed with time out before anything available
//  -   -1  -> poll(2) error - EFAULT
//  -   -2  -> poll(2) error - EINTR
//  -   -3  -> poll(2) error - EINVAL
//  -   -4  -> poll(2) error - ENOMEM
//  - -100 -> poll(2) error - Unknown error
//
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollFileWithTimeOut (JNIEnv* pEnv, jobject pThis, jstring pKey, jint timeMS)
{
    char *pathName;
    int timeMilliSec;
    PollFileService  myPoll;

    const char *str = pEnv->GetStringUTFChars(pKey, 0);

#if 0
    int iStatus = myPoll.PollFileCheck(str);
#else
    int iStatus = myPoll.PollFileRead(str);
#endif

    pEnv->ReleaseStringUTFChars(pKey, str);

    return iStatus;
}