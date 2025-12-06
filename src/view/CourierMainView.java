package view;

import controller.DeliveryHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import util.Session;
import java.util.List;
import java.util.Optional;

public class CourierMainView {
    private Stage stage;
    private DeliveryHandler deliveryHandler = new DeliveryHandler();

    public CourierMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        String courierName = Session.getInstance().getUser().getFullName();
        String courierId = Session.getInstance().getUser().getIdUser();

        Label lblTitle = new Label("Courier Dashboard - " + courierName);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Order> table = new TableView<>();
        
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

        refreshTable(table, courierId);

        Button btnUpdateStatus = new Button("Update Status");
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> {
            new UserWindow(); 
        });
        Button btnLogout = new Button("Logout");

        btnUpdateStatus.setOnAction(e -> {
            Order selectedJob = table.getSelectionModel().getSelectedItem();
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
                String updateResult = deliveryHandler.editDeliveryStatus(selectedJob.getIdOrder(), newStatus);
                
                if (updateResult.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Status updated successfully to: " + newStatus);
                    refreshTable(table, courierId); 
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to update status");
                }
            });
        });

        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblTitle, new Label("Active Delivery List:"), table, btnUpdateStatus, btnEditProfile, btnLogout);

        BorderPane root = new BorderPane();
        root.setCenter(layout);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Courier App");
        stage.show();
    }

    private void refreshTable(TableView<Order> table, String courierId) {
        table.getItems().setAll(deliveryHandler.getDeliveries(courierId));
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}