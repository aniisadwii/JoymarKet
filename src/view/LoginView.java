package view;

import controller.UserHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    
    private Stage stage;
    private UserHandler userHandler = new UserHandler();

    public LoginView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        // Components
        Label lblTitle = new Label("JoyMarket Login");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");
        
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        
        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Register Here");
        
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        // Event Handling
        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String pass = txtPass.getText();
            
            model.User user = userHandler.login(email, pass);
            
            if (user != null) {
                // Check Role and Redirect
                if (user.getRole().equals("Customer")) {
                    new CustomerWindow(stage);
                } else if (user.getRole().equals("Admin")) {
                    new AdminMainView(stage);
                } else if (user.getRole().equals("Courier")) {
                    new CourierMainView(stage); 
                } else {
                    lblError.setText("Unknown Role");
                }
            } else {
                lblError.setText("Invalid Email or Password");
            }
        });

        btnRegister.setOnAction(e -> {
            new RegisterView(stage);
        });

        // Layout
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lblTitle, txtEmail, txtPass, btnLogin, btnRegister, lblError);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Login");
        stage.show();
    }
}