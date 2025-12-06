package view;

import controller.DeliveryHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Order;
import java.util.List;
import java.util.Optional;

// RENAME CLASS JADI DeliveryWindow (Sesuai Sequence Diagram)
public class DeliveryWindow {
    
    private DeliveryHandler deliveryHandler = new DeliveryHandler();
    private VBox layout;
    private TableView<Order> table;

    public DeliveryWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label lblTitle = new Label("Incoming Orders (Pending) 📦");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Tabel Order (Sama Persis)
        table = new TableView<>();
        TableColumn<Order, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        TableColumn<Order, String> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
        TableColumn<Order, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colCust, colTotal, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        refreshTable();

        Button btnAssign = new Button("Assign to Courier 🛵");
        Button btnRefresh = new Button("Refresh Data");

        // LOGIC TOMBOL ASSIGN (Diupdate)
        btnAssign.setOnAction(e -> {
            Order selectedOrder = table.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih Order dulu!");
                return;
            }

            List<String> couriers = deliveryHandler.getCouriersList();
            if (couriers.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Gak ada kurir yang tersedia!");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(couriers.get(0), couriers);
            dialog.setTitle("Assign Order");
            dialog.setHeaderText("Pilih Kurir untuk order " + selectedOrder.getIdOrder());
            dialog.setContentText("Courier:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(selectedCourierString -> {
                // --- PERUBAHAN DI SINI ---
                // View yang bertugas "Membersihkan" input sebelum dikirim ke Controller
                // Format string: "CO001 - Budi" -> Kita ambil "CO001"
                String idCourier = selectedCourierString.split(" - ")[0];

                // Kirim ID murni ke Controller
                String status = deliveryHandler.assignCourierToOrder(selectedOrder.getIdOrder(), idCourier);
                
                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil! Kurir meluncur.");
                    refreshTable(); 
                }
            });
        });

        btnRefresh.setOnAction(e -> refreshTable());

        HBox actions = new HBox(10, btnAssign, btnRefresh);
        layout.getChildren().addAll(lblTitle, table, actions);
    }

    public VBox getView() {
        return layout;
    }
    
    private void refreshTable() {
        table.getItems().setAll(deliveryHandler.getPendingOrders());
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}