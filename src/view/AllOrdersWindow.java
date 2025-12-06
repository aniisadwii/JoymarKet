package view;

import controller.OrderHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Order;
import java.util.List;

public class AllOrdersWindow {
    
    private OrderHandler orderHandler = new OrderHandler();
    private VBox layout;
    
    // Field tabel (diakses method refresh)
    private TableView<Order> table;
    private Label lblEmpty;
    private Label lblTitle;

    public AllOrdersWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        lblTitle = new Label("All Orders Report 📊");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Pesan Kosong
        lblEmpty = new Label("No Order Available");
        lblEmpty.setStyle("-fx-text-fill: grey; -fx-font-size: 16px;");

        // Setup Tabel
        setupTable();
        
        // Load Data Pertama Kali
        refresh();
    }
    
    private void setupTable() {
        table = new TableView<>();
        
        TableColumn<Order, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        
        TableColumn<Order, String> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
        
        TableColumn<Order, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Order, String> colDate = new TableColumn<>("Ordered At");
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderedAt"));

        table.getColumns().addAll(colId, colCust, colTotal, colStatus, colDate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Method refresh() dipanggil otomatis sama AdminMainView (Listener)
    public void refresh() {
        List<Order> orders = orderHandler.getAllOrders();
        
        layout.getChildren().clear(); // Reset tampilan
        
        if (orders.isEmpty()) {
            layout.getChildren().addAll(lblTitle, lblEmpty);
        } else {
            table.getItems().setAll(orders); // Update isi tabel
            layout.getChildren().addAll(lblTitle, table);
        }
    }

    public VBox getView() {
        return layout;
    }
}