package bgu.spl.net.impl.passiveObjects;

import java.util.ArrayList;

public class Course {
/*---------------------------------fields---------------------------------*/
    short courseNum;
    String courseName;
    ArrayList<Course> KdamCoursesList;
    int numOfMaxStudents;
    int numOfRegistered;
/*-------------------------------constructors------------------------------*/
    public Course(short courseNum, String courseName, ArrayList<Course> kdamCoursesList, int numOfMaxStudents) {
        this.courseNum = courseNum;
        this.courseName = courseName;
        KdamCoursesList = kdamCoursesList;
        this.numOfMaxStudents = numOfMaxStudents;
        this.numOfRegistered = 0;
    }
/*---------------------------------getters---------------------------------*/
    public short getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public ArrayList<Course> getKdamCoursesList() {
        return KdamCoursesList;
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

/*---------------------------------methods---------------------------------*/
    public void increaseNumOfRegistered(){
        if (numOfRegistered < numOfMaxStudents) numOfRegistered++;
    }

    public void decreaseNumOfRegistered(){
        numOfRegistered--;
    }

    public boolean isRoomAvailable(){
        return numOfRegistered < numOfMaxStudents;
    }

    public String toString(){
        return "" + courseNum;
    }

    public void addKdamCourse(Course courseToAdd) {
        KdamCoursesList.add(courseToAdd);
    }

    public int getAvailableSeats(){
        return numOfMaxStudents - numOfRegistered;
    }
}


