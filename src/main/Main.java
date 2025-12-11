package main;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    // method utama yg jalan pas aplikasi dibuka
    public void start(Stage primaryStage) throws Exception {
        // langsung arahin ke halaman login
        new LoginView(primaryStage);
    }
}