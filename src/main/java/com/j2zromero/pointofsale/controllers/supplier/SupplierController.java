package com.j2zromero.pointofsale.controllers.supplier;
import com.j2zromero.pointofsale.services.supplier.SupplierService;
import com.j2zromero.pointofsale.utils.DialogUtils;
import com.j2zromero.pointofsale.utils.FormUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.List;
import   com.j2zromero.pointofsale.models.suppliers.Supplier;
import javafx.scene.layout.Pane;

public class SupplierController {
    @FXML
    private Pane suppliers_pane;
    @FXML
    private TableView<Supplier> table_supplier;
    @FXML
    private TextField txt_name;
    @FXML
    private TextField txt_contact;
    @FXML
    private TextField txt_direction;
    @FXML
    private TextField txt_extraInfo;
    @FXML
    public TableColumn id_column;
    @FXML
    private TableColumn<Supplier, String> name_column;
    @FXML
    private TableColumn<Supplier, String> contact_column;
    @FXML
    private TableColumn<Supplier, String> direction_column;
    @FXML
    private TableColumn<Supplier, String> extraInfo_column;

    private SupplierService supplierService = new SupplierService();
    private Supplier supplier = new Supplier();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        contact_column.setCellValueFactory(new PropertyValueFactory<>("contact"));
        direction_column.setCellValueFactory(new PropertyValueFactory<>("direction"));
        extraInfo_column.setCellValueFactory(new PropertyValueFactory<>("extraInformation"));

        try {
            loadSupplierData();
        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtils.showWarningAlert("Error", "No se encontraron datos (db).", null);
        }

        table_supplier.setOnMouseClicked(this::handleRowClick);

        // Add KeyEvent listener to handle Delete or Suprimir key press
        table_supplier.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE:
                case BACK_SPACE: // Optionally handle the Backspace key as well
                    delete(); // Call the delete method
                    break;
                default:
                    break;
            }
        });

        // Add KeyEvent listener for Enter key to execute the add method
        suppliers_pane.setOnKeyPressed(event -> {
            if (event.getCode().equals(javafx.scene.input.KeyCode.ENTER)) {
                add(new ActionEvent()); // Trigger the add method
            }
        });
    }



    private void loadSupplierData() throws SQLException {
        List<Supplier> suppliers = supplierService.getAll();
        supplierList.setAll(suppliers);
        table_supplier.setItems(supplierList);
    }

    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 1 && !table_supplier.getSelectionModel().isEmpty()) {
            Supplier selectedSupplier = table_supplier.getSelectionModel().getSelectedItem();

            if (selectedSupplier != null) {
                int id = selectedSupplier.getId();
                String name = selectedSupplier.getName();
                String contact = selectedSupplier.getContact();
                String direction = selectedSupplier.getDirection();
                String extraInfo = selectedSupplier.getExtraInformation();

                txt_name.setText(name);
                txt_contact.setText(contact);
                txt_direction.setText(direction);
                txt_extraInfo.setText(extraInfo);

                supplier.setId(id);
                supplier.setName(name);
                supplier.setContact(contact);
                supplier.setDirection(direction);
                supplier.setExtraInformation(extraInfo);
            }
        }
    }

    @FXML
    public void add(ActionEvent actionEvent) {

        if (txt_name.getText().trim().isEmpty()) {
            DialogUtils.showWarningAlert("Proveedor", "Necesitas agregar el nombre del proveedor.", txt_name);
            return;
        }

        supplier.setName(txt_name.getText());
        supplier.setContact(txt_contact.getText());
        supplier.setDirection(txt_direction.getText());
        supplier.setExtraInformation(txt_extraInfo.getText());

        try {
            supplierService.add(supplier);
            loadSupplierData();
            cleanFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update() {
        if (txt_name.getText().trim().isEmpty()) {
            DialogUtils.showWarningAlert("Proveedor", "Necesitas seleccionar un proveedor.", txt_name);
            return;
        }

        supplier.setName(txt_name.getText());
        supplier.setContact(txt_contact.getText());
        supplier.setDirection(txt_direction.getText());
        supplier.setExtraInformation(txt_extraInfo.getText());

        try {
            supplierService.update(supplier);
            loadSupplierData();
            cleanFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void delete() {
        if (txt_name.getText().trim().isEmpty()) {
            DialogUtils.showWarningAlert("Proveedor", "Necesitas seleccionar un proveedor.", txt_name);
            return;
        }

        // Create a confirmation alert
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmar eliminación");
        confirmationAlert.setHeaderText("¿Estás seguro de eliminar este proveedor?");
        confirmationAlert.setContentText("Esta acción no se puede deshacer.");

        // Show the alert and wait for the user's response
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User confirmed, proceed with deletion
                try {
                    supplierService.delete(supplier.getId());
                    loadSupplierData();
                    cleanFields();
                } catch (SQLException e) {
                    e.printStackTrace();
                    DialogUtils.showWarningAlert("Error", "Ocurrió un error al eliminar el proveedor.", null);
                }
            } else {
                // User cancelled, no action required
                System.out.println("Eliminación cancelada por el usuario.");
            }
        });
    }

    public void cleanFields() {
        supplier = new Supplier();  // Resetea el objeto para futuras adiciones
        txt_name.requestFocus();
        FormUtils.clearAndResetStyles(suppliers_pane);
    }
}
