package entities;

import controllers.AddEventController;

import controllers.EventController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Employee;
import models.Event;
import models.SquareEmail;
import models.UserAccount;

import java.io.IOException;

public abstract class Controller{

    protected static UserAccount SESSION_USER;
    protected static double MIN_WIDTH;
    protected static double MIN_HEIGHT;
    protected static double PREF_WIDTH;
    protected static double PREF_HEIGHT;

    protected static Alert successAlert;
    protected static Alert failureAlert;
    protected static Alert confirmationAlert;
    protected static Alert warningAlert;

    protected static ObservableList<UserAccount> userAccountObservableList;
    protected static int userAccountChecksum;

    protected static ObservableList<Employee> employeeObservableList;
    protected static int employeeChecksum;

    protected static ObservableList<SquareEmail> squareEmailObservableList;
    protected static SquareEmail selectedSquareEmail;

    protected static ObservableList<Event> eventObservableList;
    protected static Event selectedEvent;

    protected static Stage window;

    @FXML public void exitApplication(ActionEvent event){

    }

    public void mainMenuPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/MainMenu.fxml", ControllerType.MAIN_MENU);
    }

    protected void loadScene(ActionEvent event, String file, ControllerType type) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();

        switch (type){
            case MAIN_MENU:
            case USER:
            case LOGIN:
            case EMPLOYEE:
            case EMAIL: {
                MIN_HEIGHT = 600;
                MIN_WIDTH = 600;
                PREF_HEIGHT = 600;
                PREF_WIDTH = 600;
                break;
            }
            case EVENT: {
                MIN_HEIGHT = 600;
                MIN_WIDTH = 600;
                PREF_HEIGHT = 600;
                PREF_WIDTH = 900;
                break;
            }
            case REPORTS:{
                MIN_HEIGHT = 500;
                MIN_WIDTH = 600;
                PREF_HEIGHT = 500;
                PREF_WIDTH = 600;
                break;
            }

            default:
                break;
        }
        Scene scene  = new Scene(parent, PREF_WIDTH, PREF_HEIGHT);
        window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.setMinHeight(MIN_HEIGHT);
        window.setMinWidth(MIN_WIDTH);
        window.show();
    }

    protected void loadChildScene(ActionEvent event, String file, ControllerType type) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();

        switch (type){
            case ADD_USER: {
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                PREF_HEIGHT = 400;
                PREF_WIDTH = 400;
                break;
            }

            case ADD_EMPLOYEE: {
                MIN_HEIGHT = 464;
                MIN_WIDTH = 464;
                PREF_HEIGHT = 464;
                PREF_WIDTH = 464;
                break;
            }

            default:

                break;
        }
        Scene scene  = new Scene(parent, PREF_WIDTH, PREF_HEIGHT);
        Stage childWindow = new Stage();
        Stage parentWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        childWindow.setScene(scene);
        childWindow.setMinHeight(MIN_HEIGHT);
        childWindow.setMinWidth(MIN_WIDTH);
        childWindow.initStyle(StageStyle.DECORATED);
        childWindow.initModality(Modality.WINDOW_MODAL);
        childWindow.initOwner(parentWindow);
        parentWindow.toFront();
        childWindow.show();
    }

    protected void loadEventScene(Stage parentWindow, String file, ControllerType type, boolean isNew, boolean isImport, EventController eventController) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();

        switch (type){
            case ADD_EVENT: {
                AddEventController controller = loader.getController();
                controller.setIsNew(isNew);
                controller.setIsImport(isImport);
                if(eventController != null)
                    controller.setParentController(eventController);
                MIN_HEIGHT = 600;
                MIN_WIDTH = 600;
                PREF_HEIGHT = 900;
                PREF_WIDTH = 900;
                break;
            }

            default:
                break;
        }
        Scene scene  = new Scene(parent, PREF_WIDTH, PREF_HEIGHT);
        Stage childWindow = new Stage();

        childWindow.setScene(scene);
        childWindow.setMinHeight(MIN_HEIGHT);
        childWindow.setMinWidth(MIN_WIDTH);
        childWindow.initStyle(StageStyle.DECORATED);
        childWindow.initModality(Modality.WINDOW_MODAL);
        childWindow.initOwner(parentWindow);

        parentWindow.toFront();
        childWindow.show();
    }

    protected void closeChildWindow(ActionEvent event){
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    protected void clearSelectedObjects(){
        selectedSquareEmail = null;
    }
}