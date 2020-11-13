package controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import entities.Controller;

import entities.ControllerType;
import entities.GmailService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import models.SquareEmail;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class EmailController extends Controller implements Initializable {

    @FXML private JFXDatePicker dpFrom;
    @FXML private JFXDatePicker dpTo;
    @FXML private JFXComboBox<Integer> cbMaxEmails;

    @FXML private TableColumn<SquareEmail, Date> column1;
    @FXML private TableColumn<SquareEmail, String> column2;
    @FXML private TableColumn<SquareEmail, String> column3;
    @FXML private TableColumn<SquareEmail, String> column4;
    @FXML private TableColumn<SquareEmail, String> column5;

    @FXML private TableView<SquareEmail> tableView;

    private SquareEmail selectedObject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearSelectedObjects();
        dpFrom.setValue(LocalDate.now());
        dpTo.setValue(LocalDate.now());
        Integer[] maxEmailsArray = {1, 5, 10, 25, 50, 100};
        List<Integer> maxEmailsList = Arrays.asList(maxEmailsArray);
        ObservableList<Integer> maxEmailsObservableList = FXCollections.observableList(maxEmailsList);
        cbMaxEmails.setItems(maxEmailsObservableList);
        cbMaxEmails.setValue(10);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Generate Invoice");
        menuItem1.setOnAction(this::generateInvoice);
        MenuItem menuItem2 = new MenuItem("Ignore Email");
        menuItem2.setOnAction(event -> ignoreEmail() );



        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedObject = newSelection;
            }
        });

        contextMenu.getItems().add(menuItem1);
        contextMenu.getItems().add(menuItem2);
        tableView.setContextMenu(contextMenu);

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.30));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.27));
        column4.maxWidthProperty().bind(tableView.widthProperty().multiply(0.13));
        column5.maxWidthProperty().bind(tableView.widthProperty().multiply(0.10));

        column1.setCellValueFactory(new PropertyValueFactory<>("emailDate"));
        column2.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        column3.setCellValueFactory(new PropertyValueFactory<>("eventEmail"));
        column4.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        column5.setCellValueFactory(new PropertyValueFactory<>("eventGuestCount"));
    }

    public void reloadTablePushed() throws InterruptedException {
        GmailService gmail = new GmailService();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue().plusDays(1);
        int maxEmails = cbMaxEmails.getValue();



        JFrame frame = new JFrame("");
        URL url = getClass().getResource("/images/ajax-loader.gif");
        Icon icon = new ImageIcon(url);
        frame.add(new JLabel("", icon, JLabel.CENTER));

        frame.setSize(icon.getIconWidth(), icon.getIconWidth());
        double xCord = window.getX() + (window.getWidth() / 2) - (icon.getIconWidth() / 2.0);
        double yCord = window.getY() + (window.getHeight() / 2) + (icon.getIconHeight());
        frame.setLocation((int)xCord, (int)yCord);
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        frame.setVisible(true);
        frame.toFront();

        Task<Boolean> fetchEmails = new Task<Boolean>() {
            @Override
            protected Boolean call()  {
                squareEmailObservableList = FXCollections.observableList(gmail.getEmails(from, to, maxEmails));
                return null;
            }
        };

        Thread loadData = new Thread(fetchEmails);
        loadData.start();
        loadData.join();
        frame.setVisible(false);

        if (!squareEmailObservableList.isEmpty()) {
            tableView.setItems(squareEmailObservableList);
        }
        else {
            tableView.getItems().clear();
            warningAlert.setContentText("No new emails found for the selected date.");
            warningAlert.showAndWait();
        }
    }

    private void generateInvoice(ActionEvent event){
        if(selectedObject != null) {
            selectedSquareEmail = selectedObject;
            try {
                loadEventScene((Stage) dpFrom.getScene().getWindow(), "/views/AddEvent.fxml", ControllerType.ADD_EVENT, true, true, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void ignoreEmail() {
        if(selectedObject != null) {
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                selectedSquareEmail = selectedObject;
                selectedSquareEmail.setIsIgnored(true);
                selectedSquareEmail.add();
                squareEmailObservableList.remove(selectedObject);
                tableView.refresh();
                clearSelectedObjects();
            });
        }
    }

    public void resetIgnoresPushed() throws InterruptedException {
        SquareEmail.resetIgnoredEmails(dpFrom.getValue().toString(), dpTo.getValue().plusDays(1).toString());
        reloadTablePushed();
    }
}

