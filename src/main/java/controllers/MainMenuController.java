package controllers;

import entities.Controller;
import entities.ControllerType;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends Controller implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
        successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        failureAlert = new Alert(Alert.AlertType.ERROR);
        failureAlert.setTitle("Failure");
        failureAlert.setHeaderText(null);
        confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to perform this operation?");
    }

    public void userPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/UserAccount.fxml", ControllerType.USER);
    }

    public void logOutPushed(ActionEvent event) throws IOException {
        SESSION_USER = null;
        loadScene(event, "/views/LogIn.fxml", ControllerType.LOGIN);
    }

    public void employeesPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/Employee.fxml", ControllerType.EMPLOYEE);
    }

    public void importEmailsPushed(ActionEvent event) throws IOException{
        loadScene(event, "/views/Email.fxml", ControllerType.EMAIL);
    }
}
