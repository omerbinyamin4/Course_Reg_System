#ifndef CLIENT_READFROMKB_H
#define CLIENT_READFROMKB_H

#include <mutex>
#include "../include/connectionHandler.h"


class readFromKB{
private:
    int id;
    std::mutex &mutex;
    ConnectionHandler &handler;
    bool* shouldTerminate;
public:
    readFromKB (int id, std::mutex& mutex, ConnectionHandler& handler, bool* shouldTerminate);
    void run();
    void shortToBytes(short num, char *bytesArr);
};

#endif //CLIENT_READFROMKB_H
