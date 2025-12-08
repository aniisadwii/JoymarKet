package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.Session;

public class CourierMainView {
    private Stage stage;

    public CourierMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        String courierName = Session.getInstance().getUser().getFullName();

        Label lblTitle = new Label("Courier Dashboard - " + courierName);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Menu Atas
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> new UserWindow());
        
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });
        
        HBox topMenu = new HBox(10, lblTitle, new Region(), btnEditProfile, btnLogout);
        HBox.setHgrow(topMenu.getChildren().get(1), Priority.ALWAYS);
        topMenu.setPadding(new Insets(10));
        topMenu.setStyle("-fx-background-color: #ddd;");

        // Content Utama: Menggunakan DeliveryWindow (Sesuai Diagram)
        // Ini memastikan lifeline sequence diagram terpenuhi
        DeliveryWindow deliveryWindow = new DeliveryWindow();
        
        BorderPane root = new BorderPane();
        root.setTop(topMenu);
        root.setCenter(deliveryWindow.getView());

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Courier App");
        stage.show();
    }
}