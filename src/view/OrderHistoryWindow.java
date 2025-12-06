package view;

import controller.OrderHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Order;
import util.Session;
import java.util.List;

public class OrderHistoryWindow {
    
    private Stage stage;
    // Controller sesuai Class Diagram
    private OrderHandler orderHandler = new OrderHandler();
    private String idCustomer;

    public OrderHistoryWindow(Stage stage) {
        this.stage = stage;
        this.idCustomer = Session.getInstance().getUser().getIdUser();
        initialize();
    }

    private void initialize() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("My Order History 📜");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // --- FLOW SESUAI ACTIVITY DIAGRAM ---
        
        // 1. Fetch Order History List (Panggil Controller)
        List<Order> orders = orderHandler.getCustomerOrders(idCustomer);

        // 2. Decision: Are there available order histories?
        if (orders.isEmpty()) {
            // FLOW NO: Display "No Order History Available" Message
            Label lblEmpty = new Label("No Order History Available");
            lblEmpty.setStyle("-fx-text-fill: grey; -fx-font-size: 16px;");
            
            Button btnBack = new Button("Back to Home");
            btnBack.setOnAction(e -> new CustomerWindow(stage));
            
            layout.getChildren().addAll(lblTitle, lblEmpty, btnBack);
            
        } else {
            // FLOW YES: View Order History List
            
            TableView<Order> table = new TableView<>();
            
            // Kolom ID
            TableColumn<Order, String> colId = new TableColumn<>("Order ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
            
            // Kolom Total
            TableColumn<Order, Double> colTotal = new TableColumn<>("Total Amount");
            colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            
            // Kolom Status
            TableColumn<Order, String> colStatus = new TableColumn<>("Status");
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            // Kolom Tanggal (Pastikan di Model Order ada getter untuk ini)
            TableColumn<Order, String> colDate = new TableColumn<>("Ordered At");
            colDate.setCellValueFactory(new PropertyValueFactory<>("orderedAt")); 

            table.getColumns().addAll(colId, colTotal, colStatus, colDate);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Masukkan data
            table.getItems().setAll(orders);

            Button btnBack = new Button("Back to Home");
            btnBack.setOnAction(e -> new CustomerWindow(stage));

            layout.getChildren().addAll(lblTitle, table, btnBack);
        }

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Order History");
        stage.show();
    }
}