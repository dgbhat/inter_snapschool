package DataModels;

/**
 * Created by snapschool on 12/25/15.
 * stores student mark information. subject name, obtained mark and grade
 */
public class StudentMarkInfo {
    private String subjectName;
    private String mark;
    private String grade;
    public StudentMarkInfo(String subjectName, String mark, String grade) {
        this.subjectName = subjectName;
        this.mark = mark;
        this.grade = grade;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getMark() {
        return mark;
    }

    public String getGrade() {
        return grade;
    }
}
