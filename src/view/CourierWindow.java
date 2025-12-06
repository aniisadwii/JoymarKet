package view;

import controller.CourierHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Courier;
import java.util.List;

public class CourierWindow {
    
    private CourierHandler courierHandler = new CourierHandler();
    private VBox layout;

    // Constructor Kosong (Dipanggil AdminMainView)
    public CourierWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label lblTitle = new Label("Courier List 🛵");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Tabel Kurir (Modelnya Courier)
        TableView<Courier> table = new TableView<>();
        
        TableColumn<Courier, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        
        TableColumn<Courier, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        TableColumn<Courier, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        TableColumn<Courier, String> colVehicle = new TableColumn<>("Vehicle");
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        
        TableColumn<Courier, String> colPlate = new TableColumn<>("Plate Number");
        colPlate.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));

        table.getColumns().addAll(colId, colName, colPhone, colVehicle, colPlate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load Data dari CourierHandler
        List<Courier> couriers = courierHandler.getAllCouriers();
        table.getItems().setAll(couriers);
        
        layout.getChildren().addAll(lblTitle, table);
    }

    // Method ini Wajib ada buat ditempel di TabPane
    public VBox getView() {
        return layout;
    }
}