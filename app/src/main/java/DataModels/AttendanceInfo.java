package DataModels;

/**
 * Created by snapschool on 12/29/15.
 * stores attendance information of a student. student roll, student name and attendance status
 */
public class AttendanceInfo {
    private String studentRoll;
    private String studentName;
    private String status;
    public AttendanceInfo(String studentRoll, String studentName, String status) {
        this.studentRoll = studentRoll;
        this.studentName = studentName;
        this.status = status;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStatus() {
        return status;
    }
}
