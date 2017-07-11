package DataModels;

/**
 * Created by snapschool on 12/28/15.
 * stores information of class routine, subject name, starting and ending time with hour and minute of a class
 */
public class ClassRoutineInfo {
    private String subjectName;
    private String startHour;
    private String startMinute;
    private String endHour;
    private String endMinute;

    public ClassRoutineInfo(String subjectName, String startHour, String startMinute, String endHour, String endMinute) {
        this.subjectName = subjectName;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getStartHour() {
        return startHour;
    }

    public String getStartMinute() {
        return startMinute;
    }

    public String getEndHour() {
        return endHour;
    }

    public String getEndMinute() {
        return endMinute;
    }
}
