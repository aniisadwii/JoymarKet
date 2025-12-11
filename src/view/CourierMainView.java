package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.Session;

public class CourierMainView {
    private Stage stage;

    // constructor ini untuk inisialisasi dashboard utama khusus kurir
    public CourierMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    // method ini untuk menyusun tata letak ui dashboard kurir
    private void initialize() {
        // ambil nama kurir yang sedang login dari session untuk ditampilkan di judul
        String courierName = Session.getInstance().getUser().getFullName();

        Label lblTitle = new Label("Courier Dashboard - " + courierName);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // menu navigasi bagian atas
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> new UserWindow());
        
        // tombol logout untuk menghapus sesi dan kembali ke halaman login
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });
        
        HBox topMenu = new HBox(10, lblTitle, new Region(), btnEditProfile, btnLogout);
        HBox.setHgrow(topMenu.getChildren().get(1), Priority.ALWAYS);
        topMenu.setPadding(new Insets(10));
        topMenu.setStyle("-fx-background-color: #ddd;");

        // ini memungkinkan kurir melihat daftar tugas dan mengubah status pengiriman
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