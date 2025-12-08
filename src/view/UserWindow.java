package view;

import controller.UserHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import util.Session;

// CLASS UNIFIED: Menangani fitur 'Register' DAN 'Edit Profile'
// Sesuai dengan lifeline 'UserWindow' yang ada di kedua Sequence Diagram
public class UserWindow {
    
    private Stage stage;
    private UserHandler userHandler = new UserHandler();
    
    // Variabel penentu Mode (Register vs Edit)
    private boolean isEditMode = false;
    private User currentUser;

    // --- CONSTRUCTOR 1: REGISTER MODE ---
    // Dipanggil dari LoginView (saat belum login)
    public UserWindow(Stage stage) {
        this.stage = stage;
        this.isEditMode = false;
        initialize();
    }
    
    // --- CONSTRUCTOR 2: EDIT PROFILE MODE ---
    // Dipanggil dari Dashboard (saat user sudah login)
    public UserWindow() {
        this.isEditMode = true;
        this.currentUser = Session.getInstance().getUser();
        // Buat stage baru (pop-up) agar dashboard tidak tertutup
        this.stage = new Stage();
        initialize();
    }

    private void initialize() {
        // Judul & Teks Tombol Dinamis
        String titleText = isEditMode ? "Edit Profile" : "Register Account";
        Label lblTitle = new Label(titleText);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Komponen UI (Kita deklarasikan semua, tapi nanti ditampilkan selektif)
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

        // Layout Utama
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // === LOGIC TAMPILAN BERDASARKAN MODE ===
        if (isEditMode && currentUser != null) {
            // A. MODE EDIT PROFILE
            // 1. Isi form dengan data lama
            txtName.setText(currentUser.getFullName());
            txtPhone.setText(currentUser.getPhone());
            txtAddress.setText(currentUser.getAddress());
            
            // 2. Tampilkan HANYA field yang boleh diedit (Nama, Phone, Address)
            // Email dan Password TIDAK ditampilkan sesuai instruksi Docs
            root.getChildren().addAll(
                lblTitle, 
                new Label("Full Name:"), txtName, 
                new Label("Phone Number:"), txtPhone, 
                new Label("Address:"), txtAddress, 
                btnSubmit, btnBack, lblMsg
            );
            
        } else {
            // B. MODE REGISTER
            // Tampilkan SEMUA field termasuk Email dan Password
            root.getChildren().addAll(
                lblTitle, 
                txtName, txtEmail, txtPass, txtConfirmPass, txtPhone, txtAddress, 
                btnSubmit, btnBack, lblMsg
            );
        }

        // === EVENT HANDLING (Action Tombol) ===
        btnSubmit.setOnAction(e -> {
            try {
                if (isEditMode) {
                    // --- LOGIC EDIT PROFILE ---
                    // Panggil method editProfile (3 parameter)
                    String result = userHandler.editProfile(
                        currentUser.getIdUser(),
                        txtName.getText(),
                        txtPhone.getText(),
                        txtAddress.getText()
                    );
                    
                    if (result.equals("Success")) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                        stage.close(); // Tutup window edit
                    } else {
                        lblMsg.setText(result);
                    }
                    
                } else {
                    // --- LOGIC REGISTER ---
                    // Validasi confirm password dulu (khusus register)
                    if (!txtPass.getText().equals(txtConfirmPass.getText())) {
                        lblMsg.setText("Password must match confirmation");
                        return;
                    }
                    
                    // Panggil saveDataUser (5 parameter)
                    User newUser = userHandler.saveDataUser(
                        txtName.getText(), txtEmail.getText(), txtPass.getText(), 
                        txtPhone.getText(), txtAddress.getText()
                    );
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration Successful! Welcome, " + newUser.getFullName());
                    new LoginView(stage); // Balik ke login screen
                }

            } catch (Exception ex) {
                // Tangkap error dari UserHandler
                lblMsg.setText(ex.getMessage());
            }
        });

        // Tombol Back/Cancel
        btnBack.setOnAction(e -> {
            if (isEditMode) {
                stage.close(); // Tutup pop-up
            } else {
                new LoginView(stage); // Balik ke halaman login
            }
        });

        // Setup Scene
        // Tinggi window disesuaikan: Edit Profile lebih pendek karena fieldnya dikit
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