package controllers;

import entities.Controller;
import models.Employee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddEmployeeController extends Controller implements Initializable{

    @FXML private TextField textField1;
    @FXML private TextField textField2;
    @FXML private TextField textField3;
    @FXML private TextField textField4;
    @FXML private TextField textField5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
        clearTextBox();
    }

    public void addButtonPushed(ActionEvent event) {
        if (!textField1.getText().isEmpty()) {
            Employee temp = new Employee();
            temp.setFirstName(textField1.getText());
            temp.setLastName(textField2.getText());
            temp.setEmail(textField3.getText());
            temp.setAddress(textField4.getText());
            temp.setPhoneNumber(textField5.getText());
            temp.setActive(true);
                if (temp.add()) {
                    temp.getIDFromDB();
                    employeeObservableList.add(temp);
                    employeeChecksum = Employee.getChecksum();
                    clearTextBox();
                    closeChildWindow(event);
                } else {
                    failureAlert.setContentText("There was an error adding the record to the database.");
                    failureAlert.showAndWait();
                }
        } else {
            failureAlert.setContentText("Please enter a name.");
            failureAlert.showAndWait();
        }
    }

    public void cancelButtonPushed(ActionEvent event){
        closeChildWindow(event);
    }

    public void clearTextBox(){
        textField1.clear();
        textField2.clear();
        textField3.clear();
        textField4.clear();
        textField5.clear();
    }
}

