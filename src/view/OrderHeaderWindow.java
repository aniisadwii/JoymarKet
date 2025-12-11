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

// class ini untuk menampilkan daftar pesanan (history untuk customer, all orders untuk admin)
public class OrderHeaderWindow {
    
    private Stage stage;
    private OrderHandler orderHandler = new OrderHandler();
    private String currentUserId;
    private String currentUserRole;
    
    private VBox layout;
    private TableView<OrderHeader> table;

    // constructor ini digunakan saat dipanggil dari dashboard utama admin/customer
    public OrderHeaderWindow(Stage stage) {
        this.stage = stage;
        this.currentUserId = Session.getInstance().getUser().getIdUser();
        this.currentUserRole = Session.getInstance().getUser().getRole();
        initialize();
    }
    
    // constructor tanpa stage, untuk dipanggil sebagai konten dalam tabpane admin
    public OrderHeaderWindow() {
        this.currentUserId = Session.getInstance().getUser().getIdUser();
        this.currentUserRole = Session.getInstance().getUser().getRole();
        initialize();
    }

    // method ini menyusun layout tampilan list pesanan berdasarkan role user
    private void initialize() {
        layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // judul window menyesuaikan role
        String titleText = currentUserRole.equals("Admin") ? "All Orders Report" : "My Order History";
        Label lblTitle = new Label(titleText);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 1. ambil data pesanan dari controller sesuai role (admin: semua, customer: punya sendiri)
        List<OrderHeader> orders;
        if (currentUserRole.equals("Admin")) {
            orders = orderHandler.getAllOrders();
        } else {
            orders = orderHandler.getCustomerOrders(currentUserId);
        }

        // 2. cek apakah data kosong atau ada isinya
        if (orders.isEmpty()) {
            Label lblEmpty = new Label("No Orders Available");
            lblEmpty.setStyle("-fx-text-fill: grey; -fx-font-size: 16px;");
            
            // tampilkan tombol kembali jika ini window terpisah
            if (stage != null) {
                Button btnBack = new Button("Back to Home");
                btnBack.setOnAction(e -> new CustomerWindow(stage));
                layout.getChildren().addAll(lblTitle, lblEmpty, btnBack);
            } else {
                layout.getChildren().addAll(lblTitle, lblEmpty);
            }
            
        } else {
            // jika ada data, setup tabel dan tampilkan
            setupTable(); 
            table.getItems().setAll(orders); 

            // area tombol aksi di bawah tabel
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER);

            // tombol untuk melihat detail item dalam pesanan
            Button btnDetail = new Button("View Details");
            btnDetail.setOnAction(e -> showOrderDetails());
            actions.getChildren().add(btnDetail);
            
            // tombol refresh khusus admin untuk update data terbaru
            if (currentUserRole.equals("Admin")) {
                Button btnRefresh = new Button("Refresh");
                btnRefresh.setOnAction(e -> refreshData());
                actions.getChildren().add(btnRefresh);
            }

            // tombol kembali khusus customer
            if (stage != null && currentUserRole.equals("Customer")) {
                Button btnBack = new Button("Back to Home");
                btnBack.setOnAction(e -> new CustomerWindow(stage));
                actions.getChildren().add(btnBack);
            }

            layout.getChildren().addAll(lblTitle, table, actions);
        }

        // jika ada stage, tampilkan sebagai window baru
        if (stage != null) {
            Scene scene = new Scene(layout, 600, 500);
            stage.setScene(scene);
            stage.setTitle("JoyMarket - Orders");
            stage.show();
        }
    }
    
    // method ini mengatur kolom-kolom tabel yang akan ditampilkan
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
        
        // khusus admin, tampilkan kolom customer id agar tahu pemilik pesanan
        if (currentUserRole.equals("Admin")) {
            TableColumn<OrderHeader, String> colCust = new TableColumn<>("Customer ID");
            colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
            table.getColumns().add(1, colCust); 
        }
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    // method ini untuk memuat ulang data tabel dari database
    public void refreshData() {
        if (table != null) {
            List<OrderHeader> orders = currentUserRole.equals("Admin") ? 
                                       orderHandler.getAllOrders() : 
                                       orderHandler.getCustomerOrders(currentUserId);
            table.getItems().setAll(orders);
        }
    }

    // method ini menampilkan popup detail pesanan (item apa saja yang dibeli)
    private void showOrderDetails() {
        OrderHeader selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an order first");
            return;
        }

        // ambil data detail dari controller
        OrderHeader header = orderHandler.getCustomerOrderHeader(selected.getIdOrder(), selected.getIdCustomer());
        List<CartItem> items = orderHandler.getOrderItems(selected.getIdOrder());
        
        // susun string detail pesanan
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(header.getIdOrder()).append("\n");
        details.append("Customer: ").append(header.getIdCustomer()).append("\n"); 
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