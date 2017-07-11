package DataModels;

/**
 * Created by snapschool on 1/7/16.
 * stores accounting information - title, description, amount and date
 */
public class AccountingInfo {
    private String title;
    private String amount;
    private String timestamp;
    private String dueAmount;
    public AccountingInfo(String title, String amount, String dueAmount, String timestamp) {
        this.title = title;
        this.amount =amount;
        this.dueAmount = dueAmount;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDueAmount() {
        return dueAmount;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
