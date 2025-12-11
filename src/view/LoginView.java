package view;

import controller.UserHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {
    
    private Stage stage;
    private UserHandler userHandler = new UserHandler();

    // constructor ini untuk inisialisasi tampilan login utama saat aplikasi dimulai
    public LoginView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    // method ini untuk menyusun elemen ui login (input email, password, tombol) dan handling event
    private void initialize() {
        // komponen label judul
        Label lblTitle = new Label("JoyMarket Login");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // input field untuk email
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");
        
        // input field untuk password (teks tersembunyi)
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        
        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Register Here");
        
        // label untuk menampilkan pesan error jika login gagal
        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        // logika saat tombol login diklik
        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String pass = txtPass.getText();
            
            // panggil controller untuk cek kredensial user
            model.User user = userHandler.login(email, pass);
            
            if (user != null) {
                // cek role user dan arahkan ke dashboard yang sesuai
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

        // tombol untuk pindah ke halaman registrasi
        btnRegister.setOnAction(e -> {
            new UserWindow(stage);
        });

        // penyusunan layout menggunakan vbox agar vertikal dan rapi di tengah
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lblTitle, txtEmail, txtPass, btnLogin, btnRegister, lblError);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Login");
        stage.show();
    }
}