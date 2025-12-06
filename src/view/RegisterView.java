package view;

import controller.UserController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterView {
    
    private Stage stage;
    private UserController userController = new UserController();

    public RegisterView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        Label lblTitle = new Label("Register Customer");
        
        TextField txtName = new TextField(); txtName.setPromptText("Full Name");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password");
        PasswordField txtConfirmPass = new PasswordField(); txtConfirmPass.setPromptText("Confirm Password");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Phone");
        TextArea txtAddress = new TextArea(); txtAddress.setPromptText("Address"); txtAddress.setMaxHeight(60);

        Button btnSubmit = new Button("Register");
        Button btnBack = new Button("Back to Login");
        Label lblMsg = new Label();

        btnSubmit.setOnAction(e -> {
            // Panggil method yang udah direname jadi saveDataUser
            String result = userController.saveDataUser(
                txtName.getText(), txtEmail.getText(), txtPass.getText(), 
                txtConfirmPass.getText(), txtPhone.getText(), txtAddress.getText(), ""
            );

            if (result.equals("Success")) {
                // Sesuai Activity Diagram: Display success message -> Display Login Form
                
                // 1. Tampilin Alert Success (Biar lebih kerasa "Display Message"-nya)
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Message");
                alert.setHeaderText(null);
                alert.setContentText("Register Berhasil! Silakan Login.");
                alert.showAndWait();

                // 2. Pindah ke Login Form
                new LoginView(stage);
                
            } else {
                // Sesuai Activity Diagram: Display error message
                lblMsg.setStyle("-fx-text-fill: red;");
                lblMsg.setText(result);
            }
        });

        btnBack.setOnAction(e -> new LoginView(stage));

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lblTitle, txtName, txtEmail, txtPass, txtConfirmPass, txtPhone, txtAddress, btnSubmit, btnBack, lblMsg);

        Scene scene = new Scene(root, 400, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Register");
        stage.show();
    }
}