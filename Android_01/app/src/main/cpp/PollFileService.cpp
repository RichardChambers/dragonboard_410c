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
int PollFileService::iPollLastValue = 0;

static PollFileService myPollLastStatus;  // allow us to access the last status information

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
            {fd, POLLPRI | POLLERR, 0},
            {0}
        };
    nfds_t nfds = 1;
    char tempbuff[256] = {0};

    if (fd < 0 && pathName) {
        fd = open (pathName, O_RDONLY);
        fdList[0].fd = fd;
    }

    if (fd < 0) return (iPollStatus = PollErrorEINVAL);

    // with a edge triggered GPIO that we are going to use the poll(2)
    // function to wait on an event, we need to read from the
    // pin before we do the poll(2). If the read is not done then
    // the poll(2) returns with both POLLPRI and POLLERR set in the
    // revents member. however if we read first then do the poll2()
    // the poll(2) will wait for the event, input voltage change with
    // either a rising edge or a falling edge, depending on the setting
    // in the /edge pseudo file.
    ssize_t iCount = read (fdList[0].fd, tempbuff, 255);
    iPollLastValue = atoi (tempbuff);

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
            case (POLLIN):                 // value of 1
            case (POLLIN | POLLERR):       // value of 9
            case (POLLPRI):                // value of 2
            case (POLLPRI | POLLERR):      // value of 10
            case (POLLIN | POLLPRI):       // value of 3
            case (POLLIN | POLLPRI | POLLERR):   // value of 13
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

    if (fd < 0) return (iPollStatus = iRet = PollErrorEINVAL);

    int nCount = read (fd, buffer, 10);
    if (nCount < 1) {
        iPollStatus = iRet = PollErrorEFAULT;
    } else {
        iPollLastValue = iRet = atoi (buffer);
        iPollStatus = PollSuccess;
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
//  - -2000  -> observable initial value in order to know when pinPollTriggerStatus changes
//

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollFileWithTimeOut (JNIEnv* pEnv, jobject pThis, jstring pKey, jint timeMS)
{
    PollFileService  myPoll;

    const char *str = pEnv->GetStringUTFChars(pKey, 0);
    int  timeMSint = timeMS; // timeMS;

#if 1
    int iStatus = myPoll.PollFileCheck(str, timeMSint);
#else
    // following allows us to do some testing of the architecture
    // using an emulator that does not support GPIO pins.
    int iStatus = sleep(timeMSint / 1000);
#endif

    pEnv->ReleaseStringUTFChars(pKey, str);

    return iStatus;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollGetLastStatus (JNIEnv* pEnv, jobject pThis) {
    return myPollLastStatus.iPollStatus;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollGetLastRevents (JNIEnv* pEnv, jobject pThis)
{
    return myPollLastStatus.iPollRevents;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollGetLastValue (JNIEnv* pEnv, jobject pThis)
{
    return myPollLastStatus.iPollLastValue;
}
