package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;

public class BGRSProtocol implements MessagingProtocol<Message> {
    private Database database = Database.getInstance();
    private String userName;
    private String Password;
    private Boolean isAdmin;
    private boolean isLogged = false; // client may terminate after logOut, maybe not needed
    private boolean shouldTerminate = false;

    @Override
    public Message process(Message msg) {
        short opCode = msg.getOpcode();
        Message answer = null;
        switch (opCode) {
            case 1:
                if (!isLogged && database.register(msg.getUsername(), msg.getPassword(), true)) {
                    answer = composeACK(opCode, null);
                }
                break;
            case 2:
                if (!isLogged && database.register(msg.getUsername(), msg.getPassword(), false)) {
                    answer = composeACK(opCode, null);
                }
                break;
            case 3:
                // Check if Protocol already used login, if User is registered,
                // if User is already login somewhere else and if password is matching username
                if (!isLogged && database.isRegistered(msg.getUsername()) && database.isValidPassword(msg.getUsername(), msg.getPassword())
                    &&  database.logIn(msg.getUsername())){
                    // update data in Protocol
                    userName = msg.getUsername();
                    Password = msg.getPassword();
                    isAdmin = database.isAdmin(userName);
                    isLogged = true;
                    answer = composeACK(opCode, null);
                }
                break;
            case 4:
                // Check if Protocol already used login, if Protocol data is matching msg info
                // and if User is Logged in database
                if (isLogged  && database.isLoggedIn(userName)) {
                    database.logOut(userName);
                    // erase data from Protocol
                    userName = null;
                    Password = null;
                    isLogged = false;
                    isAdmin = null;
                    shouldTerminate = true;
                    answer = composeACK(opCode, null);
                }
                break;
            case 5:
                // Check if the student is not logged in, no such course is exist,
                // no seats are available in this course,the student does not have all the Kdam courses,
                // and student not registered to course
                short CourseNum = msg.getCourseNum();
                if (isLogged && !isAdmin && database.isLoggedIn(userName) && database.isCourseExist(CourseNum)
                        && database.courseCheck(userName, msg.getCourseNum()).equals("NOT REGISTERED") &&
                        database.isKdamDone(userName, CourseNum) && database.courseRegister(userName, CourseNum)) {
                    answer = composeACK(opCode, null);
                }
                break;
            case 6:
                // Check if Course is exist
                if (isLogged && database.isCourseExist(msg.getCourseNum())) {
                    String toAttach = database.KdamCheck(msg.getCourseNum());
                    answer = composeACK(opCode, toAttach);
                }
                break;
            case 7:
                // Check if User is Admin
                if (isLogged && isAdmin) {
                    String toAttach = database.ComposeCourseStat(msg.getCourseNum());
                    answer = composeACK(opCode, toAttach);
                }
                break;
            case 8:
                // Check if User is Admin
                if (isLogged && isAdmin && database.isStudentExist(msg.getUsername())) {
                    String toAttach = database.ComposeStudentStat(msg.getUsername());
                    answer = composeACK(opCode, toAttach);
                }
                break;
            case 9:
                // Check if User is not Admin and if user is logged in
                if (isLogged && !isAdmin && database.isLoggedIn(userName)) {
                    String toAttach = database.courseCheck(userName, msg.getCourseNum());
                    answer = composeACK(opCode, toAttach);
                }
                break;
            case 10:
                // Check if User is not Admin and if user is logged in
                if (isLogged && !isAdmin && database.isLoggedIn(userName) && database.courseCheck(userName, msg.getCourseNum()).equals("REGISTERED")) {
                    database.unregister(userName, msg.getCourseNum());
                    answer = composeACK(opCode, null);
                }
                break;
            case 11:
                // Check if User is not Admin and if user is logged in
                if (isLogged && !isAdmin && database.isLoggedIn(userName)) {
                    String toAttach = database.myCourses(userName);
                    answer = composeACK(opCode, toAttach);
                }
                break;
        }
        if (answer == null) {
            answer = new Message((short) 13);
            answer.setMsgOpcode(msg.getOpcode());
        }
        return answer;
    }

    private Message composeACK(short OpCode, String toAttach) {
        Message output = new Message((short) 12);
        output.setMsgOpcode(OpCode);
        if (toAttach != null) output.setOptional(toAttach);
        return output;
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
