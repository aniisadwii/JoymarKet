package util;

import model.User;

public class Session {
    private static Session instance;
    private User loggedInUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User getUser() {
        return loggedInUser;
    }

    public void setUser(User user) {
        this.loggedInUser = user;
    }
}