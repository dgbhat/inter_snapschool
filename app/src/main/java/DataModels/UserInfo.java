package DataModels;

import android.graphics.Bitmap;

/**
 * Created by snapschool on 12/23/15.
 * Stores user information - id, name, imageURL
 */
public class UserInfo {
    private String userId;
    private String userName;
    private String imageURL;
    private Bitmap image;
    public UserInfo(String userId, String userName, String imageURL) {
        this.userId = userId;
        this.userName = userName;
        this.imageURL = imageURL;
        this.image = null;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
