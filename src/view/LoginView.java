package view;

import controller.UserController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    
    private Stage stage;
    private UserController userController = new UserController();

    public LoginView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        // Components
        Label lblTitle = new Label("JoyMarKet Login");
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
            
            boolean success = userController.login(email, pass);
            if (success) {
                // TODO: Pindah ke Home Page
                System.out.println("Login Sukses! OTW Home...");
            } else {
                lblError.setText("Invalid Credentials / Gagal Login bestie");
            }
        });

        btnRegister.setOnAction(e -> {
            // Pindah ke Register View
            new RegisterView(stage);
        });

        // Layout
        VBox root = new VBox(10); // Spacing 10
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lblTitle, txtEmail, txtPass, btnLogin, btnRegister, lblError);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Login");
        stage.show();
    }
}