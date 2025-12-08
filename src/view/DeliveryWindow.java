package view;

import controller.DeliveryHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.OrderHeader;
import util.Session;
import java.util.*;

public class DeliveryWindow {
    
    private DeliveryHandler deliveryHandler = new DeliveryHandler();
    private VBox layout;
    private TableView<OrderHeader> table;
    
    // Variabel Class (Global) biar bisa diakses refreshTable()
    private Label lblEmpty; 
    private Label lblTitle;
    private HBox actions; // Container tombol
    
    private String userRole;
    private String userId;

    public DeliveryWindow() {
        this.userRole = Session.getInstance().getUser().getRole();
        this.userId = Session.getInstance().getUser().getIdUser();
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // 1. Setup Judul
        String title = userRole.equals("Admin") ? "Incoming Orders (Pending)" : "My Assigned Deliveries";
        lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // 2. Setup Label Kosong
        lblEmpty = new Label(userRole.equals("Admin") ? "No Pending Orders" : "No Assigned Delivery Available");
        lblEmpty.setStyle("-fx-text-fill: grey; -fx-font-size: 16px;");

        // 3. Setup Tabel
        setupTable();
        
        // 4. Setup Tombol Actions (Disimpan di variabel global 'actions')
        actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        if (userRole.equals("Admin")) {
            Button btnAssign = new Button("Assign to Courier");
            btnAssign.setOnAction(e -> handleAssignCourier());
            actions.getChildren().add(btnAssign);
            
        } else if (userRole.equals("Courier")) {
            Button btnUpdateStatus = new Button("Update Status");
            btnUpdateStatus.setOnAction(e -> handleUpdateStatus());
            actions.getChildren().add(btnUpdateStatus);
        }

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshTable());
        actions.getChildren().add(btnRefresh);

        // 5. Panggil Refresh buat nentuin tampilan awal (Tabel atau Pesan Kosong?)
        refreshTable();
    }
    
    private void setupTable() {
        table = new TableView<>();
        
        TableColumn<OrderHeader, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        
        TableColumn<OrderHeader, String> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
        
        TableColumn<OrderHeader, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<OrderHeader, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colCust, colTotal, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // === REVISI PENTING DI SINI ===
    // Method ini sekarang ngatur logika tampilan sesuai Activity Diagram Decision Node
    private void refreshTable() {
        List<OrderHeader> data;
        
        // Fetch List (Activity Diagram)
        if (userRole.equals("Admin")) {
            data = deliveryHandler.getPendingOrders();
        } else {
            data = deliveryHandler.getDeliveries(userId);
        }
        
        // Bersihkan Layout dulu biar gak numpuk
        layout.getChildren().clear();
        
        // Decision Node: Empty?
        if (data.isEmpty()) {
            // FLOW NO: Tampilkan Pesan Kosong + Tombol Refresh
            layout.getChildren().addAll(lblTitle, lblEmpty, actions);
        } else {
            // FLOW YES: Tampilkan Tabel + Tombol Aksi
            table.getItems().setAll(data);
            layout.getChildren().addAll(lblTitle, table, actions);
        }
    }

    // ... (Sisa method handleAssignCourier, handleUpdateStatus, getView, showAlert SAMA PERSIS, copy aja dari yang lama) ...
    
    private void handleAssignCourier() {
        OrderHeader selectedOrder = table.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an Order first");
            return;
        }

        List<String> couriers = deliveryHandler.getCouriersList();
        if (couriers.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No couriers available");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(couriers.get(0), couriers);
        dialog.setTitle("Assign Order");
        dialog.setHeaderText("Select Courier for Order " + selectedOrder.getIdOrder());
        dialog.setContentText("Courier:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedCourierString -> {
            String idCourier = selectedCourierString.split(" - ")[0];
            String status = deliveryHandler.assignCourierToOrder(selectedOrder.getIdOrder(), idCourier);
            
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success! Courier assigned.");
                refreshTable(); 
            }
        });
    }

    private void handleUpdateStatus() {
        OrderHeader selectedJob = table.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job first");
            return;
        }

        List<String> statusOptions = List.of("Pending", "In Progress", "Delivered");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedJob.getStatus(), statusOptions);
        dialog.setTitle("Update Delivery Status");
        dialog.setHeaderText("Update status for Order: " + selectedJob.getIdOrder());
        dialog.setContentText("Select New Status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStatus -> {
        	String updateResult = deliveryHandler.editDeliveryStatus(selectedJob.getIdOrder(), userId, newStatus);
            
            if (updateResult.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Status updated successfully to: " + newStatus);
                refreshTable(); 
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update status");
            }
        });
    }

    public VBox getView() {
        return layout;
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}