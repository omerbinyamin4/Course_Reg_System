#include "../include/readFromKB.h"
#include <mutex>
#include <iostream>
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"

using namespace std;


readFromKB::readFromKB(int id, std::mutex &mutex, ConnectionHandler &handler, bool* shouldTerminate): id(id), mutex(mutex), handler(handler), shouldTerminate(shouldTerminate)  {}

void readFromKB::run() {
    while (!(*shouldTerminate)) {
        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
        string line(buf);
        vector<string> message;
        boost::split(message, line, boost::is_any_of(" "));
        char opCode[2];
        char courseNum[2];

        if (message[0] == "ADMINREG") {
            if (message.size() != 3) {
                cout << "ERROR 1" << endl;
                continue;
            }
            shortToBytes(1, opCode);
            handler.sendBytes(opCode, 2);
            handler.sendLine(message[1]);
            handler.sendLine(message[2]);
        }
        else if (message[0] == "STUDENTREG") {
            if (message.size() != 3) {
                cout << "ERROR 2" << endl;
                continue;
            }
            shortToBytes(2, opCode);
            handler.sendBytes(opCode, 2);
            handler.sendLine(message[1]);
            handler.sendLine(message[2]);
        }
        else if (message[0] == "LOGIN") {
            if (message.size() != 3) {
                cout << "ERROR 3" << endl;
                continue;
            }
            shortToBytes(3, opCode);
            handler.sendBytes(opCode, 2);
            handler.sendLine(message[1]);
            handler.sendLine(message[2]);
        }
        else if (message[0] == "LOGOUT") {
            if (message.size() != 1) {
                cout << "ERROR 4" << endl;
                continue;
            }
            shortToBytes(4, opCode);
            handler.sendBytes(opCode, 2);
            mutex.lock();
            mutex.unlock();
        }
        else if (message[0] == "COURSEREG") {
            if (message.size() != 2) {
                cout << "ERROR 5" << endl;
                continue;
            }
            short courseNumber;
            try {
                courseNumber = boost::lexical_cast<short>(message[1]);
            }
            catch (const std::exception &e){
                cout << "ERROR 5" << endl;
                continue;
            }
            shortToBytes(5, opCode);
            handler.sendBytes(opCode, 2);
            shortToBytes(courseNumber, courseNum);
            handler.sendBytes(courseNum, 2);
        }
        else if (message[0] == "KDAMCHECK") {
            if (message.size() != 2) {
                cout << "ERROR 6" << endl;
                continue;
            }
            short courseNumber;
            try {
                courseNumber = boost::lexical_cast<short>(message[1]);
            }
            catch (const std::exception &e){
                cout << "ERROR 6" << endl;
                continue;
            }
            shortToBytes(6, opCode);
            handler.sendBytes(opCode, 2);
            shortToBytes(courseNumber, courseNum);
            handler.sendBytes(courseNum, 2);
        }
        else if (message[0] == "COURSESTAT") {
            if (message.size() != 2) {
                cout << "ERROR 7" << endl;
                continue;
            }
            short courseNumber;
            try {
                courseNumber = boost::lexical_cast<short>(message[1]);
            }
            catch (const std::exception &e){
                cout << "ERROR 7" << endl;
                continue;
            }
            shortToBytes(7, opCode);
            handler.sendBytes(opCode, 2);
            shortToBytes(courseNumber, courseNum);
            handler.sendBytes(courseNum, 2);
        }
        else if (message[0] == "STUDENTSTAT") {
            if (message.size() != 2) {
                cout << "ERROR 8" << endl;
                continue;
            }
            shortToBytes(8, opCode);
            handler.sendBytes(opCode, 2);
            handler.sendLine(message[1]);
        }
        else if (message[0] == "ISREGISTERED") {
            if (message.size() != 2) {
                cout << "ERROR 9" << endl;
                continue;
            }
            short courseNumber;
            try {
                courseNumber = boost::lexical_cast<short>(message[1]);
            }
            catch (const std::exception &e){
                cout << "ERROR 9" << endl;
                continue;
            }
            shortToBytes(9, opCode);
            handler.sendBytes(opCode, 2);
            shortToBytes(courseNumber, courseNum);
            handler.sendBytes(courseNum, 2);
        }
        else if (message[0] == "UNREGISTER") {
            if (message.size() != 2) {
                cout << "ERROR 10" << endl;
                continue;
            }
            short courseNumber;
            try {
                courseNumber = boost::lexical_cast<short>(message[1]);
            }
            catch (const std::exception &e){
                cout << "ERROR 10" << endl;
                continue;
            }
            shortToBytes(10, opCode);
            handler.sendBytes(opCode, 2);
            shortToBytes(courseNumber, courseNum);
            handler.sendBytes(courseNum, 2);
        }
        else if (message[0] == "MYCOURSES") {
            if (message.size() != 1) {
                cout << "ERROR 11" << endl;
                continue;
            }
            shortToBytes(11, opCode);
            handler.sendBytes(opCode, 2);
        }
        else cout << "Illegal command!" << endl; // delete
    }
}

void readFromKB::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}


