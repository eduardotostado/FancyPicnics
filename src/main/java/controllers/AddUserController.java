package controllers;

import com.jfoenix.controls.JFXComboBox;
import entities.Controller;
import models.Employee;
import models.UserAccount;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddUserController extends Controller implements Initializable {
    @FXML private TextField textField1;
    @FXML private PasswordField passwordField1;
    @FXML private PasswordField passwordField2;
    @FXML private JFXComboBox<Employee> comboBox1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
        loadData();
    }

    public void addButtonPushed(ActionEvent event){
        if(comboBox1.getSelectionModel().getSelectedIndex() != -1) {
            if(!textField1.getText().isEmpty()) {
                if (!passwordField1.getText().equals("") && !passwordField2.getText().equals("")) {
                    if (passwordField1.getText().equals(passwordField2.getText())) {
                        String tempUsername = textField1.getText();
                        int tempEmployeeID = comboBox1.getSelectionModel().getSelectedItem().getID();
                        String tempPassword = passwordField1.getText();
                        UserAccount temp = new UserAccount(tempUsername, tempPassword, tempEmployeeID);
                        if (!temp.exists()) {
                            if (temp.add()) {
                                userAccountObservableList.add(temp);
                                userAccountChecksum = UserAccount.getChecksum();
                                clearTextBox();
                                clearPasswordTextBox();
                                closeChildWindow(event);
                            } else {
                                failureAlert.setContentText("There was an error adding the record to the database.");
                                failureAlert.showAndWait();
                            }
                        } else {
                            failureAlert.setContentText("That username is already taken. Please select a different one.");
                            failureAlert.showAndWait();
                        }
                    } else {
                        clearPasswordTextBox();
                        failureAlert.setContentText("Passwords do not match.");
                        failureAlert.showAndWait();
                    }
                } else {
                    failureAlert.setContentText("Please enter a password.");
                    failureAlert.showAndWait();
                }
            } else {
                failureAlert.setContentText("Please insert a username.");
                failureAlert.showAndWait();
            }
        } else {
            failureAlert.setContentText("Please select an employee.");
            failureAlert.showAndWait();
        }

    }

    public void cancelButtonPushed(ActionEvent event){
        closeChildWindow(event);
    }

    public void clearTextBox(){
        textField1.clear();
        comboBox1.getSelectionModel().clearSelection();
    }

    public void clearPasswordTextBox(){
        passwordField1.clear();
        passwordField2.clear();
    }

    public void loadData(){
        int tmpEmployeeChecksum = Employee.getChecksum();
        if(employeeObservableList == null || employeeChecksum != tmpEmployeeChecksum) {
            employeeObservableList = Employee.findAll();
            employeeChecksum = tmpEmployeeChecksum;
        }
        comboBox1.setItems(employeeObservableList);
    }

    
}

