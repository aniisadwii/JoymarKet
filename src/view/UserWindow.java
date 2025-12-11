package view;

import controller.UserHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import util.Session;

// class ini merupakan view gabungan untuk fitur register dan edit profile 
public class UserWindow {
    
    private Stage stage;
    private UserHandler userHandler = new UserHandler();
    
    // variabel penentu mode (register vs edit)
    private boolean isEditMode = false;
    private User currentUser;

    // --- CONSTRUCTOR 1: REGISTER MODE ---
    // constructor ini untuk mode registrasi, dipanggil dari halaman login
    public UserWindow(Stage stage) {
        this.stage = stage;
        this.isEditMode = false;
        initialize();
    }
    
    // --- CONSTRUCTOR 2: EDIT PROFILE MODE ---
    // constructor ini untuk mode edit profile, dipanggil dari dashboard user
    public UserWindow() {
        this.isEditMode = true;
        this.currentUser = Session.getInstance().getUser();
        // buat stage baru (pop-up) agar dashboard tidak tertutup
        this.stage = new Stage();
        initialize();
    }

    // method ini untuk menyusun form ui secara dinamis (tampilkan field sesuai mode)
    private void initialize() {
        // judul & teks tombol dinamis
        String titleText = isEditMode ? "Edit Profile" : "Register Account";
        Label lblTitle = new Label(titleText);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // komponen ui (deklarasi semua, nanti ditampilkan selektif)
        TextField txtName = new TextField(); txtName.setPromptText("Full Name");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password");
        PasswordField txtConfirmPass = new PasswordField(); txtConfirmPass.setPromptText("Confirm Password");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Phone");
        TextArea txtAddress = new TextArea(); txtAddress.setPromptText("Address");
        txtAddress.setPrefHeight(80);

        Button btnSubmit = new Button(isEditMode ? "Save Changes" : "Register");
        Button btnBack = new Button(isEditMode ? "Cancel" : "Back to Login");
        
        Label lblMsg = new Label();
        lblMsg.setStyle("-fx-text-fill: red;");

        // layout utama
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // logika untuk membedakan tampilan antara edit profile (isi data lama) dan register (kosong)
        if (isEditMode && currentUser != null) {
            // A. MODE EDIT PROFILE
            // 1. isi form dengan data lama
            txtName.setText(currentUser.getFullName());
            txtPhone.setText(currentUser.getPhone());
            txtAddress.setText(currentUser.getAddress());
            
            // 2. tampilkan hanya field yang boleh diedit (nama, phone, address)
            // email dan password tidak ditampilkan sesuai instruksi docs
            root.getChildren().addAll(
                lblTitle, 
                new Label("Full Name:"), txtName, 
                new Label("Phone Number:"), txtPhone, 
                new Label("Address:"), txtAddress, 
                btnSubmit, btnBack, lblMsg
            );
            
        } else {
            // B. MODE REGISTER
            // tampilkan semua field termasuk email dan password
            root.getChildren().addAll(
                lblTitle, 
                txtName, txtEmail, txtPass, txtConfirmPass, txtPhone, txtAddress, 
                btnSubmit, btnBack, lblMsg
            );
        }

        // logika tombol submit yang menangani dua skenario berbeda (update atau register)
        btnSubmit.setOnAction(e -> {
            try {
                if (isEditMode) {
                    // --- LOGIC EDIT PROFILE ---
                    // panggil controller untuk update data profil yang diizinkan
                    String result = userHandler.editProfile(
                        currentUser.getIdUser(),
                        txtName.getText(),
                        txtPhone.getText(),
                        txtAddress.getText()
                    );
                    
                    if (result.equals("Success")) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                        stage.close(); // tutup window edit
                    } else {
                        lblMsg.setText(result);
                    }
                    
                } else {
                    // --- LOGIC REGISTER ---
                    // validasi password match dulu sebelum panggil controller register
                    if (!txtPass.getText().equals(txtConfirmPass.getText())) {
                        lblMsg.setText("Password must match confirmation");
                        return;
                    }
                    
                    // panggil method simpan data user baru
                    User newUser = userHandler.saveDataUser(
                        txtName.getText(), txtEmail.getText(), txtPass.getText(), 
                        txtPhone.getText(), txtAddress.getText()
                    );
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration Successful! Welcome, " + newUser.getFullName());
                    new LoginView(stage); // balik ke login screen
                }

            } catch (Exception ex) {
                // tangkap error dari userhandler
                lblMsg.setText(ex.getMessage());
            }
        });

        // tombol back/cancel
        btnBack.setOnAction(e -> {
            if (isEditMode) {
                stage.close(); // tutup pop-up
            } else {
                new LoginView(stage); // balik ke halaman login
            }
        });

        // setup scene dengan tinggi window disesuaikan utk edit profile lebih pendek karena fieldnya dikit
        Scene scene = new Scene(root, 400, isEditMode ? 450 : 550); 
        stage.setScene(scene);
        stage.setTitle("JoyMarket - " + titleText);
        stage.show();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}