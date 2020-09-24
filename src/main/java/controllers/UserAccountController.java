package controllers;

import entities.Controller;
import entities.ControllerType;
import models.Employee;
import models.UserAccount;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserAccountController extends Controller implements Initializable {
    @FXML private TableColumn<UserAccount, String> column1;
    @FXML private TableColumn<UserAccount, Employee> column2;
    @FXML private TableColumn<UserAccount, String> column3;
    @FXML private TableView<UserAccount> tableView;

    private ObservableList<Employee> activeEmployeeObservableList;
    private UserAccount selectedObject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
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
        contextMenu.getItems().add(menuItem1);
        contextMenu.setMinWidth(300);

        contextMenu.setMaxHeight(30);
        tableView.setContextMenu(contextMenu);

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.4));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        column1.setCellValueFactory(new PropertyValueFactory<>("username"));
        column2.setCellValueFactory(new PropertyValueFactory<>("employee"));
        column3.setCellValueFactory(new PropertyValueFactory<>("employeeEmail"));

        column2.setCellFactory(ComboBoxTableCell.forTableColumn(activeEmployeeObservableList));
        column2.setOnEditCommit(this::editRecord);
    }

    public void addButtonPushed(ActionEvent event) throws IOException {
        loadChildScene(event, "/views/AddUser.fxml", ControllerType.ADD_USER);
    }

    public void editRecord(TableColumn.CellEditEvent<UserAccount, Employee> event){
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        selectedObject.setEmployeeID(event.getNewValue().getID());
        if (selectedObject.edit()) {
            selectedObject.clearEmployee();
            selectedObject.setEmployee(event.getNewValue());
            userAccountChecksum = UserAccount.getChecksum();
            tableView.refresh();
            if(SESSION_USER != null){
                if(SESSION_USER.getUsername().equals(selectedObject.getUsername())) {
                    SESSION_USER = SESSION_USER.findByUsername(SESSION_USER.getUsername());
                }
            }
        } else {
            selectedObject.setEmployeeID(event.getOldValue().getID());
            tableView.refresh();
            failureAlert.setContentText("There was an error updating the record.");
            failureAlert.showAndWait();
        }
    }

    public void deleteRecord() {
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        if (selectedObject != null) {
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                if (selectedObject.delete()) {
                    userAccountChecksum = UserAccount.getChecksum();
                    tableView.getItems().remove(selectedObject);
                    successAlert.setContentText("Record successfully deleted");
                    successAlert.showAndWait();
                } else {
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

        int tmpUserAccountChecksum = UserAccount.getChecksum();
        if(userAccountObservableList == null || userAccountChecksum != tmpUserAccountChecksum) {
            userAccountObservableList = UserAccount.findAll();
            userAccountChecksum = tmpUserAccountChecksum;
            for(UserAccount userAccount : userAccountObservableList){
                int employeeID = userAccount.getEmployeeID();
                Employee tempEmployee = employeeObservableList.stream().filter(employee -> employeeID == employee.getID()).findFirst().orElse(null);
                userAccount.setEmployee(tempEmployee);
            }
        }

        List<Employee> activeEmployeeList = new ArrayList<>();
        for (Employee employee: employeeObservableList) {
            if(employee.getActive()){
                activeEmployeeList.add(employee);
            }
        }
        activeEmployeeObservableList = FXCollections.observableList(activeEmployeeList);

        if (!userAccountObservableList.isEmpty())
            tableView.setItems(userAccountObservableList);
        else
            tableView.getItems().clear();
    }
}

