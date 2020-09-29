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

int PollFileService::iPollStatus = 0;
int PollFileService::iPollRet = 0;
int PollFileService::iPollRevents = 0;

PollFileService::PollFileService(const char *pathName /* = nullptr */, int timeMilliSec /* = -1 */) : iValue(23), fd(-1)
{
    iPollStatus = 0;
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
            {fd, POLLPRI, 0},
            {0}
        };
    nfds_t nfds = 1;

    if (fd < 0 && pathName) {
        fd = open (pathName, O_RDONLY);
        fdList[0].fd = fd;
    }
    iPollStatus = PollErrorUNKNOWN;
    int iRet = poll(fdList, nfds, timeMilliSec);

    if (iRet == 0) {
        iPollStatus = PollTimeOut;
    } else if (iRet < 0) {
        switch (errno) {
            case EFAULT:
                iPollStatus = PollErrorEFAULT;
                break;
            case EINTR:
                iPollStatus = PollErrorEINTR;
                break;
            case EINVAL:
                iPollStatus = PollErrorEINVAL;
                break;
            case ENOMEM:
                iPollStatus = PollErrorENOMEM;
                break;
            default:
                iPollStatus = PollErrorUNKNOWN;
                break;
        }
    } else if (iRet > 0) {
        // successful call now determine what we should return.
        iPollRevents = fdList[0].revents; /* & (POLLIN | POLLPRI | POLLERR); */
        switch (fdList[0].revents & (POLLIN | POLLPRI | POLLERR /* | POLLNVAL | POLLHUP*/)) {
            case (POLLIN):                // value of 1
            case (POLLPRI):               // value of 2
            case (POLLIN | POLLPRI):       // value of 3
                iPollStatus = PollSuccess;
                break;

            case (POLLHUP):
                iPollStatus = PollErrorPOLLHUP;
                break;
            case (POLLERR):               // value of 8
                iPollStatus = PollErrorPOLLERR;
                break;
            case (POLLNVAL):
                iPollStatus = PollErrorPOLLNVAL;
                break;
            case (POLLERR | POLLNVAL):
                iPollStatus = PollErrorPOLLERRNVAL;
                break;

            default:
                iPollStatus = PollErrorPOLLERRDEFLT;
                break;
        }
    }

    return iPollStatus;
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
//  -   -5  -> poll(2) error - POLLERR
//  -   -6  -> poll(2) error - POLLNVAL
//  -   -7  -> poll(2) error - POLLERR | POLLNVAL
//  -   -8  -> poll(2) error - POLLHUP
//  -   -9  -> poll(2) error - poll(2) revent indicator Unknown
//  - -100 -> poll(2) error - Unknown error
//
static int lastRevents = 0;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollFileWithTimeOut (JNIEnv* pEnv, jobject pThis, jstring pKey, jint timeMS)
{
    char *pathName;
    int timeMilliSec;
    PollFileService  myPoll;

    const char *str = pEnv->GetStringUTFChars(pKey, 0);
    int  timeMSint = timeMS;

#if 1
    int iStatus = myPoll.PollFileCheck(str, timeMSint);
#else
    int iStatus = myPoll.PollFileRead(str);
#endif

    pEnv->ReleaseStringUTFChars(pKey, str);

    lastRevents = myPoll.iPollRevents;

    return iStatus;
}

#if 0
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollGetLastStatus (JNIEnv* pEnv, jobject pThis) {
    return PollFileService::iPollStatus;
}
#endif

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollGetLastRevents (JNIEnv* pEnv, jobject pThis)
{
    return lastRevents;
}
