package cesi.com.notes.model;

/**
 * Created by sca on 02/06/15.
 */
public class Note {

    private final boolean done;
    String username;
    String msg;
    private String id;

    public Note(String id, String username, String message, long date, boolean done) {
        this.username = username;
        this.msg = message;
        this.date = date;
        this.done = done;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }


    public long getDate() {
        return date;
    }

    long date;

    public boolean isDone() {
        return done;
    }

    public String getId() {
        return id;
    }
}
