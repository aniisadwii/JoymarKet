package view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.Session;

public class CustomerMainView {
    public CustomerMainView(Stage stage) {
        String name = Session.getInstance().getUser().getFullName();
        
        BorderPane root = new BorderPane();
        Label welcome = new Label("Welcome Customer, " + name + "! Belanja yuk kak 🛍️");
        welcome.setStyle("-fx-font-size: 20px; -fx-padding: 20;");
        
        root.setCenter(welcome);
        
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Customer Home");
    }
}