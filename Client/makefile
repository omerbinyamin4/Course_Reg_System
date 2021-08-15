CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: bin/BGRSclient

bin/BGRSclient: bin/Client.o bin/connectionHandler.o bin/readFromKB.o bin/readFromSock.o
	g++ -o bin/BGRSclient bin/Client.o bin/connectionHandler.o bin/readFromKB.o bin/readFromSock.o $(LDFLAGS)

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/readFromKB.o: src/readFromKB.cpp
	g++ $(CFLAGS) -o bin/readFromKB.o src/readFromKB.cpp

bin/readFromSock.o: src/readFromSock.cpp
	g++ $(CFLAGS) -o bin/readFromSock.o src/readFromSock.cpp

.PHONY: clean
clean:
	rm -f bin/*