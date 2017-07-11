package com.snapschool.mlps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by snapschool on 12/17/15.
 * server manager class. performs all server requests asynchronously
 */
public class ServerManager {
    // server address. change this to your server address
    final private String SERVER_ADDRESS = "http://dev.snapschool.in/";

    // server response handler interface
    public interface ServerResponseHandler {
        void requestFinished(String response, int requestTag);
        void requestFailed(String errorMessage, int requestTag);
        void imageDownloaded(Bitmap image, int requestTag);
    }

    // source activity from where the request is coming
    private ServerResponseHandler sourceActivity;

    public ServerManager(Activity activity) {
        sourceActivity = (ServerResponseHandler)activity;
    }

    // fetch system info from server
    public void getSystemInfo(int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_system_info";
        HashMap<String, String> params = new HashMap<>();
        params.put("authenticate", "false");
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // login server call
    public void login(String email, String password, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "login";
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("authenticate", "false");
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch summary information
    public void getSummary(String authKey, String loginType, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_total_summary";
        HashMap<String, String> params = new HashMap<>();
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch class list
    public void getClassList(String authKey, String loginType, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_class";
        HashMap<String, String> params = new HashMap<>();
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch child list
    public void getChildList(String authKey, String loginType, String parentId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_children_of_parent";
        HashMap<String, String> params = new HashMap<>();
        params.put("parent_id", parentId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch user list - teacher, parent and student list
    public void getUserList(String authKey, String loginType, String userType, String classId, int requestTag) {
        String requestName = "";
        switch (userType) {
            case "Students":
                requestName = "get_students_of_class";
                break;
            case "Teachers":
                requestName = "get_teachers";
                break;
            case "Parents":
                requestName = "get_parents";
                break;
        }
        String url = SERVER_ADDRESS + "index.php?mobile/" + requestName;
        HashMap<String, String> params = new HashMap<>();
        if (userType.equals("Students"))
            params.put("class_id", classId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch user profile information
    public void getUserProfile(String authKey, String loginType, String userId, String userType, int requestTag) {
        String requestName = "";
        String idKey = "";
        switch (userType) {
            case "Students":
                requestName = "get_student_profile_information";
                idKey = "student_id";
                break;
            case "Teachers":
                requestName = "get_teacher_profile";
                idKey = "teacher_id";
                break;
            case "Parents":
                requestName = "get_parent_profile";
                idKey = "parent_id";
                break;
        }
        String url = SERVER_ADDRESS + "index.php?mobile/" + requestName;
        HashMap<String, String> params = new HashMap<>();
        params.put(idKey, userId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch exam list
    public void getExamList(String authKey, String loginType, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_exam_list";
        HashMap<String, String> params = new HashMap<>();
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch student mark information
    public void getStudentMarksOfExam(String authKey, String loginType, String studentId, String examId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_student_mark_information";
        HashMap<String, String> params = new HashMap<>();
        params.put("exam_id", examId);
        params.put("student_id", studentId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch subject list
    public void getSubjectList(String authKey, String loginType, String classId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_subject_of_class";
        HashMap<String, String> params = new HashMap<>();
        params.put("class_id", classId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch class routine
    public void getClassRoutineOfDay(String authKey, String loginType, String classId, String sectionId, String day, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_class_routine";
        HashMap<String, String> params = new HashMap<>();
        params.put("class_id", classId);
        params.put("section_id", sectionId);
        params.put("day", day);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch attendance information
    public void getAttendance(String authKey, String loginType, int day, int month, int year, String classId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_attendance";
        HashMap<String, String> params = new HashMap<>();
        String dayStr = String.valueOf(day);
        if (day<10)
            dayStr = "0" + dayStr;
        String monthStr = String.valueOf(month);
        if (month<10)
            monthStr = "0" + monthStr;
        params.put("date", dayStr);
        params.put("month", monthStr);
        params.put("year", String.valueOf(year));
        params.put("class_id", classId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch accounting information
    public void getAccountingInfo(String authKey, String loginType, int month, String year, String type, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_accounting";
        HashMap<String, String> params = new HashMap<>();
        params.put("month", String.valueOf(month));
        params.put("year", String.valueOf(year));
        params.put("type", type);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch student accounting information
    public void getStudentAccountingInfo(String authKey, String loginType, String studentId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_single_student_accounting";
        HashMap<String, String> params = new HashMap<>();
        params.put("student_id", studentId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch exam marks
    public void getMarks(String authKey, String loginType, String examId, String classId, String subjectId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_marks";
        HashMap<String, String> params = new HashMap<>();
        params.put("exam_id", examId);
        params.put("class_id", classId);
        params.put("subject_id", subjectId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch events
    public void getEvents(String authKey, String loginType, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_event_calendar";
        HashMap<String, String> params = new HashMap<>();
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // fetch my profile information
    public void getMyProfile(String authKey, String loginType, String userId, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "get_loggedin_user_profile";
        HashMap<String, String> params = new HashMap<>();
        params.put("login_type", loginType);
        params.put("login_user_id", userId);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // update profile information
    public void updateProfile(String authKey, String loginType, String userId, String name, String email, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "update_user_info";
        HashMap<String, String> params = new HashMap<>();
        params.put("login_type", loginType);
        params.put("login_user_id", userId);
        params.put("name", name);
        params.put("email", email);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // update password
    public void updatePassword(String authKey, String loginType, String userId, String newPassword, String oldPassword, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "update_user_password";
        HashMap<String, String> params = new HashMap<>();
        params.put("login_type", loginType);
        params.put("login_user_id", userId);
        params.put("new_password", newPassword);
        params.put("old_password", oldPassword);
        params.put("authentication_key", authKey);
        params.put("user_type", loginType);
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // reset password
    public void resetPassword(String email, int requestTag) {
        String url = SERVER_ADDRESS + "index.php?mobile/" + "reset_password";
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("authenticate", "false");
        AsyncHttpPost requestSender = new AsyncHttpPost(url, params, requestTag);
        requestSender.execute();
    }
    // downloads an image
    public void downloadImage(String imageUrl, int imageSize, int requestTag) {
        ImageDownloadTask imageDownloadTask = new ImageDownloadTask(imageUrl, imageSize, requestTag);
        imageDownloadTask.execute();
    }
    // server request async task
    private class AsyncHttpPost extends AsyncTask<Void, Void, String> {
        private String url = "";
        private HashMap<String, String> postParams = null;
        private int requestTag;
        private String errorMessage = "";

        public AsyncHttpPost(String url, HashMap<String, String> params, int tag) {
            this.url = url;
            postParams = params;
            this.requestTag = tag;
        }

        @Override
        protected String doInBackground(Void... params) {
            byte[] result;
            String resultString = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            try {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                for (String key:postParams.keySet()
                     ) {
                    nameValuePairs.add(new BasicNameValuePair(key, postParams.get(key)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpClient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                    errorMessage = "ok";
                    result = EntityUtils.toByteArray(response.getEntity());
                    resultString = new String(result, "UTF-8");
                }
            }
            catch (UnsupportedEncodingException e) {
                errorMessage = "Encoding is not supported";
            }
            catch (Exception e) {
                errorMessage = "An error occurred";
            }
            return resultString;
        }

        @Override
        protected void onPostExecute(String s) {
            if (errorMessage.equals("ok")) {
                sourceActivity.requestFinished(s, requestTag);
            }
            else
                sourceActivity.requestFailed(errorMessage, requestTag);
        }
    }
    // image download async task
    private class ImageDownloadTask extends AsyncTask<String, String, String> {
        private String imageUrl;
        private int imageSize;
        private int requestTag;
        Bitmap image;
        public ImageDownloadTask(String imageUrl, int imageSize, int requestTag) {
            this.imageUrl = imageUrl;
            this.imageSize = imageSize;
            this.requestTag = requestTag;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(imageUrl);
                InputStream inputStream = new BufferedInputStream(url.openStream());
                image = createScaledBitmapFromStream(inputStream, imageSize);
            }
            catch (Exception e){
                //do nothing
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            sourceActivity.imageDownloaded(image, requestTag);
        }
        protected Bitmap createScaledBitmapFromStream(InputStream inputStream, int minimumDesiredBitmapSize) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 32*1024);
            try {
                BitmapFactory.Options decodeBitmapOptions = new BitmapFactory.Options();
                if (minimumDesiredBitmapSize > 0) {
                    BitmapFactory.Options decodeBoundsOptions = new BitmapFactory.Options();
                    decodeBoundsOptions.inJustDecodeBounds = true;
                    bufferedInputStream.mark(32 * 1024);
                    BitmapFactory.decodeStream(bufferedInputStream, null, decodeBoundsOptions);
                    bufferedInputStream.reset();
                    int originalWidth = decodeBoundsOptions.outWidth;
                    int originalHeight = decodeBoundsOptions.outHeight;
                    decodeBitmapOptions.inSampleSize = Math.max(1, Math.min(originalWidth/minimumDesiredBitmapSize, originalHeight/minimumDesiredBitmapSize));
                }
                return BitmapFactory.decodeStream(bufferedInputStream, null, decodeBitmapOptions);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    bufferedInputStream.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
