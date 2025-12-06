package view;

import controller.UserHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;
import util.Session;

// RENAME CLASS: Sesuai Sequence Diagram (UserWindow)
public class UserWindow {
    
    private Stage stage; // Stage baru (pop-up)
    private UserHandler userHandler = new UserHandler();

    // Constructor kosong (karena ini window baru/popup)
    public UserWindow() {
        this.stage = new Stage();
        initialize();
    }

    private void initialize() {
        User user = Session.getInstance().getUser();

        Label lblTitle = new Label("Edit Profile");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Input Fields (Diisi data lama)
        TextField txtName = new TextField(user.getFullName());
        txtName.setPromptText("Full Name");
        
        TextField txtPhone = new TextField(user.getPhone());
        txtPhone.setPromptText("Phone Number");
        
        TextArea txtAddress = new TextArea(user.getAddress());
        txtAddress.setPromptText("Address");
        txtAddress.setPrefHeight(80);

        Button btnSave = new Button("Save Changes 💾");
        Label lblMsg = new Label();

        btnSave.setOnAction(e -> {
            // Panggil UserHandler.editProfile
            String result = userHandler.editProfile(
                user.getIdUser(),
                txtName.getText(),
                txtPhone.getText(),
                txtAddress.getText()
            );

            if (result.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profil berhasil diupdate! Data akan berubah setelah relogin.");
                stage.close(); // Tutup window
            } else {
                lblMsg.setText(result);
                lblMsg.setStyle("-fx-text-fill: red;");
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(lblTitle, new Label("Name:"), txtName, new Label("Phone:"), txtPhone, new Label("Address:"), txtAddress, btnSave, lblMsg);

        Scene scene = new Scene(layout, 350, 400);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Edit Profile");
        stage.show(); // Tampilkan sebagai window baru
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}