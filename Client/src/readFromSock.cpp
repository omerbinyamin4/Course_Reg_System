#include "../include/readFromSock.h"
#include <mutex>
using namespace std;


readFromSock::readFromSock(int id, std::mutex &mutex, ConnectionHandler &handler, bool* shouldTerminate): id(id), mutex(mutex), handler(handler), shouldTerminate(shouldTerminate){

}
void readFromSock::run() {
    while (!(*shouldTerminate)) {
        mutex.try_lock();
        char message[2];
        handler.getBytes(message, 2);
        short opCode = bytesToShort(message);
        if (opCode == 13) {
            handler.getBytes(message, 2);
            short messageOpCode = bytesToShort(message);
            cout << "ERROR " << messageOpCode << std::endl;
            if (messageOpCode == 4) {
                mutex.unlock();
                sleep(1);
            }
        }
        else if (opCode == 12){
            handler.getBytes(message, 2);
            short messageOpCode = bytesToShort(message);
            cout << "ACK " << messageOpCode << std::endl;
            //print additional
            string optional;
            handler.getLine(optional);
            if (optional != "")
                cout << optional << std::endl;
            if (messageOpCode == 4) {
                *shouldTerminate = true;
                mutex.unlock();
            }

        }
    }
}

short readFromSock::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
