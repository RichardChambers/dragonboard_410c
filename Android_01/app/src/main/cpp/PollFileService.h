//
// Created by rchamber on 9/24/2020.
//

#ifndef MY_APPLICATION_POLLFILESERVICE_H
#define MY_APPLICATION_POLLFILESERVICE_H


class PollFileService {
private:
    int iValue;
    int fd;         /* file descriptor */

public:
    // See poll(2) man page at https://linux.die.net/man/2/poll
    static const int PollSuccess = 0;
    static const int PollTimeOut = 1;
    static const int PollErrorEFAULT = -1;
    static const int PollErrorEINTR  = -2;
    static const int PollErrorEINVAL = -3;
    static const int PollErrorENOMEM = -4;
    static const int PollErrorPOLLERR = -5;
    static const int PollErrorPOLLNVAL = -6;

    static const int PollErrorUNKNOWN = -100;

    PollFileService(const char *pathName = nullptr, int timeMilliSec = -1);
    ~PollFileService();

    int PollFileCheck (const char *pathName, int timeMilliSec = -1);
    int PollFileRead (const char *pathName = nullptr);
};

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_MainActivity_pollFileWithTimeOut (JNIEnv* pEnv, jobject pThis, jstring pKey, jint timeMS);

#endif //MY_APPLICATION_POLLFILESERVICE_H
