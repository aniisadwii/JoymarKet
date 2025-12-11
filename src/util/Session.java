package util;

import model.User;

public class Session {
    private static Session instance;
    private User loggedInUser;

    private Session() {}

    // method ini untuk mendapatkan instance session tunggal agar data user bisa diakses global
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // method ini untuk mengambil data user yang sedang login saat ini
    public User getUser() {
        return loggedInUser;
    }

    // method ini untuk menyimpan data user setelah login atau menghapusnya saat logout
    public void setUser(User user) {
        this.loggedInUser = user;
    }
}