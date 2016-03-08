package ucc.com.safetyapplication;

/**
 * Created by Damian on 7/03/2016.
 */
public class Message {
    private String message, time;

    public Message() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public Message(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }
    public String getTime() {
        return time;
    }
}