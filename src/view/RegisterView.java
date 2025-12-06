package view;

import controller.UserHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RegisterView {
    
    private Stage stage;
    private UserHandler userHandler = new UserHandler();

    public RegisterView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        Label lblTitle = new Label("Register Customer");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
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
            String result = userHandler.saveDataUser(
                txtName.getText(), txtEmail.getText(), txtPass.getText(), 
                txtConfirmPass.getText(), txtPhone.getText(), txtAddress.getText()
            );

            if (result.equals("Success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Message");
                alert.setHeaderText(null);
                alert.setContentText("Registration Successful! Please Login.");
                alert.showAndWait();

                new LoginView(stage);
            } else {
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