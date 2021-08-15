#include <mutex>
#include "connectionHandler.h"

#ifndef CLIENT_READFROMSOCK_H
#define CLIENT_READFROMSOCK_H


class readFromSock{
private:
    int id;
    std::mutex &mutex;
    ConnectionHandler &handler;
    bool* shouldTerminate;
public:
    readFromSock (int id, std::mutex& mutex, ConnectionHandler &handler, bool* shouldTerminate);
    void run();

    short bytesToShort(char *bytesArr);
};

#endif //CLIENT_READFROMSOCK_H
