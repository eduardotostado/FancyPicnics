package controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import entities.Controller;

import entities.GmailService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.SquareEmail;

import javax.swing.*;
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
        menuItem1.setOnAction(event -> generateInvoice() );
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

    public void reloadTablePushed(){
        GmailService gmail = new GmailService();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue().plusDays(1);
        int maxEmails = cbMaxEmails.getValue();

        JFrame frame = new JFrame("Loading");
        ImageIcon loading = new ImageIcon("ajax-loader.gif");
        frame.add(new JLabel("loading...", loading, JLabel.CENTER));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setVisible(true);

        Task<Boolean> fetchEmails = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                squareEmailObservableList = FXCollections.observableList(gmail.getEmails(from, to, maxEmails));
                if (!squareEmailObservableList.isEmpty())
                    tableView.setItems(squareEmailObservableList);
                else {
                    tableView.getItems().clear();
                    warningAlert.setContentText("No new emails found for the selected date.");
                    warningAlert.showAndWait();
                }
                frame.setVisible(false);
                return true;
            }
        };

        new Thread(fetchEmails).start();


    }

    private void generateInvoice(){
        // Pre-populate add invoice screen.
        selectedSquareEmail = selectedObject;
    }

    private void ignoreEmail(){
        // Check if fake invoice exists else create fake invoice
        // Create square email with fake invoice id
    }
}

