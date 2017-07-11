package DataModels;

import java.util.Date;

/**
 * Created by snapschool on 1/9/16.
 * event infor data model, stores event title, description and timestamp
 */
public class EventInfo {
    private String noticeTitle;
    private String noticeDescription;
    private String timeStamp;
    private Date eventDate;
    public EventInfo(String noticeTitle, String noticeDescription, String timeStamp, Date eventDate) {
        this.noticeTitle = noticeTitle;
        this.noticeDescription = noticeDescription;
        this.timeStamp = timeStamp;
        this.eventDate = eventDate;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Date getEventDate() {
        return eventDate;
    }
}
