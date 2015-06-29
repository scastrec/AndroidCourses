package cesi.com.tchatapp.model;

/**
 * Created by sca on 07/06/15.
 */
public class User {

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
}
