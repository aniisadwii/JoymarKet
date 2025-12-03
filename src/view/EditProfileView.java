package view;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;
import util.Session;

public class EditProfileView {
    private Stage stage; // Stage baru (pop-up)
    private UserController userController = new UserController();

    public EditProfileView() {
        this.stage = new Stage();
        initialize();
    }

    private void initialize() {
        User user = Session.getInstance().getUser();

        Label lblTitle = new Label("Edit Profile");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Input Fields (Diisi data lama dulu)
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
            String result = userController.updateProfile(
                user.getIdUser(),
                txtName.getText(),
                txtPhone.getText(),
                txtAddress.getText()
            );

            if (result.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profil berhasil diupdate! Silakan relogin atau refresh dashboard.");
                stage.close(); // Tutup window edit
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
        stage.setTitle("Edit Profile");
        stage.show(); // Tampilkan sebagai window baru
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}