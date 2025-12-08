package view;

import controller.OrderHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;
import model.OrderHeader;
import util.Session;
import java.util.List;

// CLASS UNIFIED: Dipakai oleh Customer (History) DAN Admin (View All Orders)
// Sesuai dengan nama lifeline 'OrderHeaderWindow' di kedua Sequence Diagram (Seq 8 & Seq 11)
public class OrderHeaderWindow {
    
    private Stage stage;
    private OrderHandler orderHandler = new OrderHandler();
    private String currentUserId;
    private String currentUserRole;
    
    private VBox layout;
    private TableView<OrderHeader> table;

    // Constructor untuk dipanggil dari Dashboard (Admin/Customer)
    public OrderHeaderWindow(Stage stage) {
        this.stage = stage;
        this.currentUserId = Session.getInstance().getUser().getIdUser();
        this.currentUserRole = Session.getInstance().getUser().getRole();
        initialize();
    }
    
    // Constructor tanpa stage (opsional, kalau dipanggil sebagai tab content di Admin)
    public OrderHeaderWindow() {
        this.currentUserId = Session.getInstance().getUser().getIdUser();
        this.currentUserRole = Session.getInstance().getUser().getRole();
        initialize();
    }

    private void initialize() {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Judul dinamis berdasarkan Role
        String titleText = currentUserRole.equals("Admin") ? "All Orders Report 📊" : "My Order History 📜";
        Label lblTitle = new Label(titleText);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 1. Fetch Data Sesuai Role (Sesuai Class Diagram methods)
        List<OrderHeader> orders;
        if (currentUserRole.equals("Admin")) {
            // Flow Admin: getAllOrders() (Sequence Diagram 11)
            orders = orderHandler.getAllOrders();
        } else {
            // Flow Customer: getCustomerOrders() (Sequence Diagram 8)
            orders = orderHandler.getCustomerOrders(currentUserId);
        }

        // 2. Decision: Empty or Not? (Activity Diagram)
        if (orders.isEmpty()) {
            Label lblEmpty = new Label("No Orders Available");
            lblEmpty.setStyle("-fx-text-fill: grey; -fx-font-size: 16px;");
            
            // Tombol back cuma perlu kalau ini Window terpisah (bukan Tab)
            if (stage != null) {
                Button btnBack = new Button("Back to Home");
                btnBack.setOnAction(e -> new CustomerWindow(stage));
                layout.getChildren().addAll(lblTitle, lblEmpty, btnBack);
            } else {
                layout.getChildren().addAll(lblTitle, lblEmpty);
            }
            
        } else {
            setupTable(); // Setup kolom
            table.getItems().setAll(orders); // Masukkan data

            // Area Tombol Aksi
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER);

            // Tombol View Details (Bisa dipake Admin & Customer)
            Button btnDetail = new Button("View Details");
            btnDetail.setOnAction(e -> showOrderDetails());
            actions.getChildren().add(btnDetail);
            
            // Tombol Refresh (Khusus Admin biasanya butuh)
            if (currentUserRole.equals("Admin")) {
                Button btnRefresh = new Button("Refresh");
                btnRefresh.setOnAction(e -> refreshData());
                actions.getChildren().add(btnRefresh);
            }

            // Tombol Back (Khusus Customer Window)
            if (stage != null && currentUserRole.equals("Customer")) {
                Button btnBack = new Button("Back to Home");
                btnBack.setOnAction(e -> new CustomerWindow(stage));
                actions.getChildren().add(btnBack);
            }

            layout.getChildren().addAll(lblTitle, table, actions);
        }

        if (stage != null) {
            Scene scene = new Scene(layout, 600, 500);
            stage.setScene(scene);
            stage.setTitle("JoyMarket - Orders");
            stage.show();
        }
    }
    
    private void setupTable() {
        table = new TableView<>();
        
        TableColumn<OrderHeader, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        
        TableColumn<OrderHeader, Double> colTotal = new TableColumn<>("Total Amount");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<OrderHeader, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<OrderHeader, String> colDate = new TableColumn<>("Ordered At");
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderedAt")); 
        
        table.getColumns().addAll(colId, colTotal, colStatus, colDate);
        
        // Khusus Admin, tampilkan juga kolom Customer ID biar tau ini punya siapa
        if (currentUserRole.equals("Admin")) {
            TableColumn<OrderHeader, String> colCust = new TableColumn<>("Customer ID");
            colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
            table.getColumns().add(1, colCust); // Insert di posisi ke-2
        }
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    // Method untuk refresh data (dipanggil dari AdminMainView atau tombol refresh)
    public void refreshData() {
        if (table != null) {
            List<OrderHeader> orders = currentUserRole.equals("Admin") ? 
                                       orderHandler.getAllOrders() : 
                                       orderHandler.getCustomerOrders(currentUserId);
            table.getItems().setAll(orders);
        }
    }

    private void showOrderDetails() {
        OrderHeader selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an order first");
            return;
        }

        // Panggil method detail
        OrderHeader header = orderHandler.getCustomerOrderHeader(selected.getIdOrder(), selected.getIdCustomer());
        List<CartItem> items = orderHandler.getOrderItems(selected.getIdOrder());
        
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(header.getIdOrder()).append("\n");
        details.append("Customer: ").append(header.getIdCustomer()).append("\n"); // Info tambahan buat Admin
        details.append("Status: ").append(header.getStatus()).append("\n");
        details.append("------------------------------------------------\n");
        
        for (CartItem item : items) {
            details.append("- ").append(item.getProductName())
                   .append(" x").append(item.getQuantity())
                   .append(" (Rp ").append(item.getPrice()).append(")\n");
        }
        
        details.append("------------------------------------------------\n");
        details.append("TOTAL: Rp ").append(header.getTotalAmount());

        showAlert(Alert.AlertType.INFORMATION, "Order Details", details.toString());
    }

    public VBox getView() {
        return layout;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
    
    private void showAlert(Alert.AlertType type, String content) {
        showAlert(type, "Message", content);
    }
}