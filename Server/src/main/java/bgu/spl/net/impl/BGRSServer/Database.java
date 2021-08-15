package bgu.spl.net.impl.BGRSServer;


import bgu.spl.net.impl.passiveObjects.Admin;
import bgu.spl.net.impl.passiveObjects.Course;
import bgu.spl.net.impl.passiveObjects.Student;
import bgu.spl.net.impl.passiveObjects.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
	private static class SingletonHolder {
		private static Database instance = new Database();
	}

	private final ConcurrentHashMap<String, Student> StudentsMap;
	private final ConcurrentHashMap<String, Admin> AdminsMap;
	private final ConcurrentHashMap<Short, Course> CoursesMap;
	private final ConcurrentHashMap<String, User> LoggedInMap;
	private final ArrayList<Short> CoursesList;

	//to prevent user from creating new Database
	private Database() {
		StudentsMap = new ConcurrentHashMap<>();
		AdminsMap = new ConcurrentHashMap<>();
		CoursesMap = new ConcurrentHashMap<>();
		LoggedInMap = new ConcurrentHashMap<>();
		CoursesList = new ArrayList<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
	 * loades the courses from the file path specified 
	 * into the Database, returns true if successful.
	 */
		boolean initialize(String coursesFilePath) {
		// read file from Path
		File file = new File(coursesFilePath);
		StringBuilder Kdam = new StringBuilder();
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String[] currentLine = sc.nextLine().split("[|]");
				Course newCourse = new Course(Short.parseShort(currentLine[0]), currentLine[1], new ArrayList<>(), Integer.parseInt(currentLine[3]));
				Kdam.append(currentLine[0]).append("|").append(currentLine[2]).append("|");
				CoursesMap.put(Short.parseShort(currentLine[0]), newCourse);
				CoursesList.add(Short.parseShort(currentLine[0]));
			}
		}
		catch (FileNotFoundException e) {
			return false;
		}
		// add Kdam Course for each course
		String[] couresesAndKdams = Kdam.toString().split("[|]");
		for (int i = 0; i < couresesAndKdams.length; i = i + 2) {
			Course currentCourse = CoursesMap.get(Short.parseShort(couresesAndKdams[i]));
			String[] Kdams = couresesAndKdams[i+1].substring(1,couresesAndKdams[i+1].length()-1).split(",");
			for (int j = 0; j < Kdams.length && !Kdams[j].equals(""); j++) {
				currentCourse.addKdamCourse(CoursesMap.get(Short.parseShort(Kdams[j])));
			}
		}
		return true;
	}

	/**
	 * checks if this username is already registered
	 * @param userName is the username we check if registered
	 * @return true if registered to database and false
	 */
	public boolean isRegistered(String userName) {
		return AdminsMap.containsKey(userName) || StudentsMap.containsKey(userName);
	}

	/**
	 *
	 * @param userName is the student we want to check
	 * @return true if the {@link Student} username exists in the students hashmap
	 */
	public boolean isStudentExist(String userName) {
		return StudentsMap.containsKey(userName);
	}

	/**
	 * @return true if {@param password} is the valid password for the student that holds {@param username}
	 */
	public boolean isValidPassword(String userName, String Password) {
		String toCompare;
		if (AdminsMap.containsKey(userName)) toCompare = AdminsMap.get(userName).getPassword();
		else toCompare = StudentsMap.get(userName).getPassword();
		return Password.equals(toCompare);
	}

	/**
	 * @return true if {@param numOfCourse} exists in the the courses map
	 */
	public boolean isCourseExist(short numOfCourse) {
		return CoursesMap.containsKey(numOfCourse);
	}

	/**
	 * @return true if {@param userName} completed all the 'kdam' {@link Course} required for {@param numOfCourse}
	 */
	public boolean isKdamDone(String userName, short numOfCourse) {
		List<Course> KdamList = CoursesMap.get(numOfCourse).getKdamCoursesList();
		Student currentStudent = StudentsMap.get(userName);
		boolean result = true;
		for (Course course : KdamList) {
			result = currentStudent.checkCourse(course);
			if (!result) break;
		}
		return result;
	}

	/**
	 * @return true if {@param username} is registered as {@link Admin}
	 */
	public boolean isAdmin(String username) {
		return AdminsMap.containsKey(username);
	}

	/**
	 * register a new User to the database
	 * @param userName: the {@link User} we want to register
	 * @param Password: the password will be needed for this student to log in
	 * @param isAdmin: determines if the {@link User} will be added to {@param AdminMap} or to {@param StudentMap}
	 * @return true if the user was successfully registered
	 */
	public boolean register(String userName, String Password, boolean isAdmin) {
		if (isAdmin) {
			synchronized (AdminsMap) {
				if (isRegistered(userName)) return false;
				Admin newAdmin = new Admin(userName, Password);
				AdminsMap.put(userName, newAdmin);
			}
		}
		else {
			synchronized (StudentsMap) {
				if (isRegistered(userName)) return false;
				Student newStudent = new Student(userName, Password, new ArrayList<>()); // maybe change arraylist
				StudentsMap.put(userName, newStudent);
			}
		}
		return true;
	}

	/**
	 * adds a course to a students list of courses in case there is room available in this course
	 * @param userName: the {@link Student} that takes the course
	 * @param numOfCourse: {@link Course} that need to be added
	 * @return true if the course successfully added
	 */
	public boolean courseRegister(String userName, short numOfCourse) {
		Course currentCourse = CoursesMap.get(numOfCourse);
		synchronized (CoursesMap.get(numOfCourse)) {
			if (currentCourse.isRoomAvailable()) {
				synchronized (StudentsMap.get(userName)) {
					StudentsMap.get(userName).addCourse(currentCourse);
				}
				currentCourse.increaseNumOfRegistered();
				return true;
			}
			else return false;
		}
	}

	/**
	 *
	 * @param numOfCourse: the {@link Course} we want to check it's kdam courses
	 * @return a list of all the kdam {@link Course} required for the specific course.
	 */
	public String KdamCheck(short numOfCourse) {
		ArrayList<Course> list = CoursesMap.get(numOfCourse).getKdamCoursesList();
		return list.toString().replaceAll("\\s","");
	}

	/**
	 * composes Course status: course number, course name, number of available seats, list of registered students.
	 * @param numOfCourse: the number of the {@link Course} it's status will be returned
	 * @return the status as String
	 */
	public String ComposeCourseStat(short numOfCourse) {
		String output;
		Course currentCourse = CoursesMap.get(numOfCourse);
		synchronized (CoursesMap.get(numOfCourse)) {
			output = "Course: (" + numOfCourse + ") " + currentCourse.getCourseName() + "\n" +
					"Seats Available: " + currentCourse.getAvailableSeats() + "/" + currentCourse.getNumOfMaxStudents() + "\n";
			output = output + getStudentsRegisteredList(currentCourse);
		}
		return output;
	}

	/**
	 * composes Student status: username, list of courses taken
	 * @param userName: the username of the {@link Student} it's status will be returned
	 * @return the status as String
	 */	public String ComposeStudentStat(String userName) {
		Student currentStudent = StudentsMap.get(userName);
		synchronized (StudentsMap.get(userName)) {
			ArrayList<Course> toBeSorted = currentStudent.getCourses();
			sortCourses(toBeSorted);
			String sortedList = toBeSorted.toString().replaceAll("\\s", "");
			return "Student: " + currentStudent.getUsername() + "\n" + "Courses: " + sortedList;
		}
	}

	/**
	 * checks is the studetns that hold {@param username} is regisred to the course with {@param numOfCourse}
	 * @param userName: username of the student
	 * @param numOfCourse: number of the course
	 * @return "REGISTERED" or "UNREGISTERED" string
	 */
	public String courseCheck(String userName, short numOfCourse) {
		Course course = CoursesMap.get(numOfCourse);
		if (StudentsMap.get(userName).checkCourse(course)) return "REGISTERED";
		else return "NOT REGISTERED";
	}

	/**
	 * unregister a {@link Student} from {@link Course}
	 * @param userName: the username of the students we want to unregister
	 * @param numOfCourse: the number of course we want to unregister the student from
	 */
	public void unregister(String userName, short numOfCourse) {
		Course course = CoursesMap.get(numOfCourse);
		synchronized (StudentsMap.get(userName)) {
			StudentsMap.get(userName).removeCourse(course);
		}
		synchronized (CoursesMap.get(numOfCourse)) {
			course.decreaseNumOfRegistered();
		}
	}

	/**
	 * return a sorted (according to course input file) list of {@link Course} for a spesific {@link Student}
	 * @param userName: the username of the students it's courses list will be returned
	 * @return String of all the courses
	 */
	public String myCourses(String userName) {
		ArrayList<Course> myCourses = StudentsMap.get(userName).getCourses();
		sortCourses(myCourses);
		return myCourses.toString().replaceAll("\\s","");
	}

	/**
	 * changes a {@link User} status to "logged in"  in case it is registered and logged out
	 * @param username: the user we want to log in
	 * @return true if successfully logged in
	 */
	public boolean logIn(String username){
		synchronized (LoggedInMap) {
			if (!isLoggedIn(username)) {
				if (AdminsMap.containsKey(username)) {
					Admin admin = AdminsMap.get(username);
					LoggedInMap.put(username, admin);
				} else {
					Student student = StudentsMap.get(username);
					LoggedInMap.put(username, student);
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * changes a {@link User} status to "logged out"
	 * @param username: the user we want to log out
	 */
	public void logOut(String username){
		LoggedInMap.remove(username);
	}

	/**
	 * checks if a {@User} is logged in
	 * @param username
	 * @return true if the user that holds this username is in the {@param LoggedInMap}
	 */
	public boolean isLoggedIn(String username){
		return LoggedInMap.containsKey(username);
	}

	/**
	 *
	 * @param courseToCheck
	 * @return String with the list of {@link Student} registered to this specific {@link Course}
	 */
	private String getStudentsRegisteredList(Course courseToCheck) {
		ArrayList<String> registeredStudents = new ArrayList<>();
		for (Student student : StudentsMap.values()) {
			synchronized (StudentsMap.get(student.getUsername())) {
				if (student.checkCourse(courseToCheck)) registeredStudents.add(student.getUsername());
			}
		}
		registeredStudents.sort(Comparator.naturalOrder());
		return "Students Registered: " + registeredStudents.toString().replaceAll("\\s","");
	}

	/**
	 * sorts a given list of {@link Course} according to the input course file
	 * @param courses
	 */
	private void sortCourses(ArrayList<Course> courses) {
		Comparator<Course> comparator = Comparator.comparingInt(o -> CoursesList.indexOf(o.getCourseNum()));
		courses.sort(comparator);
	}
}
