package view;

import controller.CustomerHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Customer;
import util.Session;
import java.util.Optional;

public class CustomerWindow {
    private Stage stage;
    private CustomerHandler customerHandler = new CustomerHandler(); 

    public CustomerWindow(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        Customer customer = (Customer) Session.getInstance().getUser(); 

        Label lblWelcome = new Label("Welcome, " + customer.getFullName());
        lblWelcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblBalance = new Label("Balance: Rp " + customer.getBalance());
        lblBalance.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        
        Button btnTopUp = new Button("Top Up");
        btnTopUp.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Top Up Balance");
            dialog.setHeaderText("Enter Top Up Amount (Min. 10.000)");
            dialog.setContentText("Amount:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(amountStr -> {
                String status = customerHandler.topUpBalance(amountStr); 
                
                if (status.equals("Success")) {
                    lblBalance.setText("Balance: Rp " + customer.getBalance()); 
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Top Up Successful");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", status);
                }
            });
        });

        Button btnViewCart = new Button("View Cart");
        btnViewCart.setOnAction(e -> new OrderHeaderWindow(stage));
        
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> new UserWindow());
        
        Button btnHistory = new Button("Order History");
        btnHistory.setOnAction(e -> new OrderHistoryWindow(stage));
        
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });

        HBox balanceBox = new HBox(10, lblBalance, btnTopUp);
        VBox topLayout = new VBox(10);
        topLayout.setPadding(new Insets(10));
        topLayout.setStyle("-fx-background-color: #f0f0f0;");
        topLayout.getChildren().addAll(lblWelcome, balanceBox, btnViewCart, btnHistory, btnEditProfile, btnLogout);

        ProductWindow productWindow = new ProductWindow();
        
        BorderPane root = new BorderPane();
        root.setTop(topLayout);
        root.setCenter(productWindow.getView());

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Customer Dashboard");
        stage.show();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}