package DataModels;

/**
 * Created by snapschool on 1/4/16.
 * stores student mark for a subject - student roll, student name and obtained mark
 */
public class MarkInfo {
    private String studentRoll;
    private String studentName;
    private String mark;
    public MarkInfo(String studentRoll, String studentName, String mark) {
        this.studentRoll = studentRoll;
        this.studentName = studentName;
        this.mark = mark;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getMark() {
        return mark;
    }
}
