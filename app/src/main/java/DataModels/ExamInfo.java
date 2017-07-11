package DataModels;

/**
 * Created by snapschool on 1/4/16.
 * stores exam information. exam id and exam name
 */
public class ExamInfo {
    private String examId;
    private String examName;
    public ExamInfo(String examId, String examName) {
        this.examId = examId;
        this.examName = examName;
    }
    public String getExamId() {
        return examId;
    }
    public String getExamName() {
        return examName;
    }
}
