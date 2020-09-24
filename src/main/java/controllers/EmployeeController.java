package controllers;

import entities.Controller;
import entities.ControllerType;
import models.Employee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeController extends Controller implements Initializable {

    @FXML private TableColumn<Employee, String> column1;
    @FXML private TableColumn<Employee, String> column2;
    @FXML private TableColumn<Employee, String> column3;
    @FXML private TableColumn<Employee, String> column4;
    @FXML private TableColumn<Employee, String> column5;
    @FXML private TableColumn<Employee, String> column6;

    @FXML private TableView<Employee> tableView;

    private Employee selectedObject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
        // Loads the data into the TableView and ComboBox
        loadData();

        // When a row is selected, populates the text fields with the information from the row.
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedObject = newSelection;
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Delete");
        menuItem1.setOnAction(event -> deleteRecord() );
        MenuItem menuItem2 = new MenuItem("Toggle Active");
        menuItem2.setOnAction(event -> toggleActiveRecord() );

        // We don't want to delete Employees
        // contextMenu.getItems().add(menuItem1);

        contextMenu.getItems().add(menuItem2);
        tableView.setContextMenu(contextMenu);

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        column4.maxWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        column5.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column6.maxWidthProperty().bind(tableView.widthProperty().multiply(0.10));

        column1.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        column2.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        column3.setCellValueFactory(new PropertyValueFactory<>("email"));
        column4.setCellValueFactory(new PropertyValueFactory<>("address"));
        column5.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        column6.setCellValueFactory(new PropertyValueFactory<>("active"));

        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        column2.setCellFactory(TextFieldTableCell.forTableColumn());
        column3.setCellFactory(TextFieldTableCell.forTableColumn());
        column4.setCellFactory(TextFieldTableCell.forTableColumn());
        column5.setCellFactory(TextFieldTableCell.forTableColumn());

        column1.setOnEditCommit(event -> editRecord(event, 1) );
        column2.setOnEditCommit(event -> editRecord(event, 2) );
        column3.setOnEditCommit(event -> editRecord(event, 3) );
        column4.setOnEditCommit(event -> editRecord(event, 4) );
        column5.setOnEditCommit(event -> editRecord(event, 5) );
    }

    public void addButtonPushed(ActionEvent event) throws IOException {
         loadChildScene(event, "/views/AddEmployee.fxml", ControllerType.ADD_EMPLOYEE);
    }

    public void editRecord(TableColumn.CellEditEvent<Employee, String> event, int column){
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        switch (column){
            case 1:
                selectedObject.setFirstName(event.getNewValue());
                break;
            case 2:
                selectedObject.setLastName(event.getNewValue());
                break;
            case 3:
                selectedObject.setEmail(event.getNewValue());
                break;
            case 4:
                selectedObject.setAddress(event.getNewValue());
                break;
            case 5:
                selectedObject.setPhoneNumber(event.getNewValue());
                break;
            default:
                break;
        }
        if (selectedObject.edit()) {
            employeeChecksum = Employee.getChecksum();
            tableView.refresh();
        } else {
            switch (column){
                case 1:
                    selectedObject.setFirstName(event.getOldValue());
                    break;
                case 2:
                    selectedObject.setLastName(event.getOldValue());
                    break;
                case 3:
                    selectedObject.setEmail(event.getOldValue());
                    break;
                case 4:
                    selectedObject.setAddress(event.getOldValue());
                    break;
                case 5:
                    selectedObject.setPhoneNumber(event.getOldValue());
                    break;
                default:
                    break;
            }
            tableView.refresh();
            failureAlert.setContentText("There was an error updating the record.");
            failureAlert.showAndWait();
        }
    }


    public void deleteRecord() {
//        selectedObject = tableView.getSelectionModel().getSelectedItem();
//        if (selectedObject != null) {
//            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
//                if (selectedObject.delete()) {
//                    tableView.getItems().remove(selectedObject);
//                    successAlert.setContentText("Record successfully deleted");
//                    successAlert.showAndWait();
//                } else {
//                    failureAlert.setContentText("There was an error deleting the record from the database.");
//                    failureAlert.showAndWait();
//                }
//            });
//        } else {
//            failureAlert.setContentText("Please select a valid record.");
//            failureAlert.showAndWait();
//        }
    }

    public void toggleActiveRecord(){
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        if (selectedObject != null) {
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                selectedObject.setActive(!selectedObject.getActive());
                if (selectedObject.edit()) {
                    employeeChecksum = Employee.getChecksum();
                    tableView.refresh();
                    successAlert.setContentText("Employee successfully activated/deactivated");
                    successAlert.showAndWait();
                } else {
                    selectedObject.setActive(!selectedObject.getActive());
                    tableView.refresh();
                    failureAlert.setContentText("There was an error deleting the record from the database.");
                    failureAlert.showAndWait();
                }
            });
        } else {
            failureAlert.setContentText("Please select a valid record.");
            failureAlert.showAndWait();
        }
    }


    public void loadData() {
        int tmpEmployeeChecksum = Employee.getChecksum();
        if(employeeObservableList == null || employeeChecksum != tmpEmployeeChecksum) {
            employeeObservableList = Employee.findAll();
            employeeChecksum = tmpEmployeeChecksum;
        }

        if (!employeeObservableList.isEmpty())
            tableView.setItems(employeeObservableList);
        else
            tableView.getItems().clear();
    }
}