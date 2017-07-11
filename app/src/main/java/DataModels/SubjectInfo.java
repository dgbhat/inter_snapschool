package DataModels;

/**
 * Created by snapschool on 12/28/15.
 * subject info data model. stores subject name and teacher name
 */
public class SubjectInfo {
    private String subjectId;
    private String subjectName;
    private String teacherName;
    public SubjectInfo(String subjectId, String subjectName, String teacherName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
    }

    public String getSubjectId() {
        return subjectId;
    }
    public String getSubjectName() {
        return subjectName;
    }
    public String getTeacherName() {
        return teacherName;
    }
}
