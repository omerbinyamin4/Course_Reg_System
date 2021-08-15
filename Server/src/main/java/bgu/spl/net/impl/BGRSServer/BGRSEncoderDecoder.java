package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<Message> {
    /**
     *
     */

    private byte[] shortBytes = new byte[2];
    private byte[] stringBytes = new byte[1 << 10];
    private int stringBytesLength = 0;
    private int optCounter = 0; //can be replaced with length ?
    private int shortCounter = 0; //can be replaced with length ?
    private short optNum = 0;
    private String username = "";
    private String password = "";

    @Override

    public Message decodeNextByte(byte nextByte) {
        //decoding opt

        if (optCounter < 2) {
            shortBytes[optCounter] = nextByte;
            optCounter++;
            if (optCounter == 2) {
                optNum = bytesToShort(shortBytes);
                optCounter++;
                if (optNum == 4 | optNum == 11){ //opt is 4 or 11 TODO: make prettier
                    Message decoded = new Message(optNum);
                    shortCounter = 0;
                    optNum = 0;
                    clear();
                    return decoded;
                }
                return null;
            }
        }
        //decoding rest of the message

        else if (optNum != 0 & optNum < 4 | optNum == 8) { //opt is 1-3 or 8 (decode String)
            return decodeString(nextByte);
        }

        else if (optNum != 0){ //opt is 5-7 or 9-10 (decode short)
            if (shortCounter < 2){
                shortBytes[shortCounter] = nextByte;
                shortCounter++;
                if (shortCounter == 2){
                    Message decoded = new Message(optNum);
                    decoded.setCourseNum(bytesToShort(shortBytes));
                    clear();
                    return decoded;
                }
                return null;
            }
        }
        return null; // if opNum is still 0 (?)
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public Message decodeString(byte nextByte){
        if (nextByte == '\0' & username.isEmpty()) {
            username = new String(stringBytes, 0, stringBytesLength, StandardCharsets.UTF_8);
            stringBytesLength = 0;
            if (optNum == 8) return composeMessage();
            else return null;
        }
        else if (nextByte == '\0') {
            password = new String(stringBytes, 0, stringBytesLength, StandardCharsets.UTF_8);
            stringBytesLength = 0;
            return composeMessage();
        }
        else {
            stringBytes[stringBytesLength] = nextByte;
            stringBytesLength++;
            return null;
        }
    }

    public Message composeMessage(){
        Message decoded = new Message(optNum);
        decoded.setUsername(username);
        decoded.setPassword(password);
        clear();
        return decoded;
    }

    @Override
    public byte[] encode(Message message) {
        short opCode = message.getOpcode();
        if (opCode == 12) return encodeACK(message);
        else return encodeERR(message);
    }

    private byte[] encodeACK(Message message) {
        byte[] opcode = shortToBytes(message.getOpcode());
        byte[] messageOpcode = shortToBytes(message.getMsgOpcode());
        byte[] optional = new byte[0];
        if (message.containOptional()) optional = message.getOptional().getBytes();
        byte[] endMsg = {'\0'};
        ByteBuffer encoded = ByteBuffer.allocate(opcode.length + messageOpcode.length + optional.length + endMsg.length);
        encoded.put(opcode);
        encoded.put(messageOpcode);
        encoded.put(optional);
        encoded.put(endMsg);
        return encoded.array();
    }

    private byte[] encodeERR(Message message) {
        byte[] opcode = shortToBytes(message.getOpcode());
        byte[] messageOpcode = shortToBytes(message.getMsgOpcode());
        ByteBuffer encoded = ByteBuffer.allocate(opcode.length + messageOpcode.length);
        encoded.put(opcode);
        encoded.put(messageOpcode);
        return encoded.array();
    }

    private byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private void clear(){
        shortBytes = new byte[2];
        stringBytes = new byte[1 << 10];
        optCounter = 0;
        shortCounter = 0;
        stringBytesLength = 0;
        optNum = 0;
        username = "";
        password = "";
    }
}
