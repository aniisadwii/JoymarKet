package main;

import util.Connect;

public class Main {
    public Main() {
        // Coba panggil koneksi
        Connect.getInstance();
    }

    public static void main(String[] args) {
        new Main();
    }
}