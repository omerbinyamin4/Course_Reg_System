package bgu.spl.net.impl.passiveObjects;

import java.util.ArrayList;

public class Student extends User {
/*---------------------------------fields---------------------------------*/
    private ArrayList<Course> courses;
/*-------------------------------constructors------------------------------*/
    public Student(String username, String password, ArrayList<Course> courses){
        super(username, password);
        this.courses = courses;
    }
/*---------------------------------getters---------------------------------*/
    public ArrayList<Course> getCourses() {
        return courses;
    }
/*---------------------------------methods---------------------------------*/
    public void addCourse(Course toAdd){
        if (toAdd != null)
            courses.add(toAdd);
    }

    public void removeCourse(Course toRemove) {
        courses.remove(toRemove);
    }

    public boolean checkCourse(Course toCheck){
        if (toCheck != null)
            return (courses.contains(toCheck));
        return false;
    }

}
