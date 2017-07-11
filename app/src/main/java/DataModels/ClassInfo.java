package DataModels;

/**
 * Created by snapschool on 12/23/15.
 * stores class information - id, name
 */
public class ClassInfo {
    private String classId;
    private String className;
    public ClassInfo(String classId, String className) {
        this.classId = classId;
        this.className = className;
    }
    public String getClassId() {
        return this.classId;
    }
    public String getClassName() {
        return this.className;
    }
}
