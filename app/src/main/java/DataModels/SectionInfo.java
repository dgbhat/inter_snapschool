package DataModels;

/**
 * Created by snapschool on 5/26/16.
 * stores section info. section id, section name and associated class id of the section
 */
public class SectionInfo {
    private String sectionID;
    private String sectionName;
    private String classID;

    public SectionInfo(String sectionID, String sectionName, String classID) {
        this.sectionID = sectionID;
        this.sectionName = sectionName;
        this.classID = classID;
    }

    public String getSectionID() {
        return sectionID;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getClassID() {
        return classID;
    }
}
