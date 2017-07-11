package DataModels;

/**
 * Created by snapschool on 12/23/15.
 * stores child information - id, name, class id
 */
public class ChildInfo {
    private String childId;
    private String childName;
    private String classId;
    public ChildInfo(String childId, String childName, String classId) {
        this.childId = childId;
        this.childName = childName;
        this.classId = classId;
    }

    public String getChildId() {
        return childId;
    }

    public String getChildName() {
        return this.childName;
    }

    public String getClassId() {
        return classId;
    }
}
