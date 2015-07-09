package cesi.com.notes.model;

/**
 * Created by sca on 07/06/15.
 */
public class User {


    private String urlPhoto;

    private long date;

    private String username;

    public User(String username, long date, String url) {
        this.username = username;
        this.date = date;
        this.urlPhoto=url;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }
}
