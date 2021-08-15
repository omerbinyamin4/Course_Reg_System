package bgu.spl.net.impl.BGRSServer;

public class Message {
    /*---------------------------------fields---------------------------------*/
    private short opcode; //for all
    private String username; //for adminreg, studentreg, login, logout
    private String password; //for adminreg, studentreg, login, logout
    private short courseNum; //for coursereg, kdamcheck, coursestat, studentdstat, isregistered, unregister
    private short msgOpcode; // for error and acknowledge
    private String optional; //for acknowledge
    /*-------------------------------constructors------------------------------*/
    public Message(short opcode){
        this.opcode = opcode;
    }
    /*---------------------------------getters---------------------------------*/
    public short getOpcode() {
        return opcode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public short getCourseNum() {
        return courseNum;
    }

    public short getMsgOpcode() {
        return msgOpcode;
    }

    public String getOptional() {
        return optional;
    }

    /*---------------------------------setters---------------------------------*/
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCourseNum(short courseNum) {
        this.courseNum = courseNum;
    }

    public void setMsgOpcode(short msgOpcode) {
        this.msgOpcode = msgOpcode;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public boolean containOptional() {
        return optional != null;
    }
    /*---------------------------------methods---------------------------------*/
}
