package controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import entities.Controller;

import entities.ExportToExcel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController extends Controller implements Initializable {

    @FXML private JFXComboBox<String> cbReport;
    @FXML private Label label1;
    @FXML private Label label2;

    @FXML private JFXDatePicker dpFrom;
    @FXML private JFXDatePicker dpTo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> reportList = new ArrayList<>();
        reportList.add("Accounting");
        reportList.add("Conversion Rate");
        reportList.add("Best Locations");
        reportList.add("Best Addons");
        reportList.add("Most Picnic Types");

        ObservableList<String> cbReportsList = FXCollections.observableList(reportList);
        cbReport.setItems(cbReportsList);

        cbReport.valueProperty().addListener((observable, oldValue, newValue) -> {
            reportSelected(newValue);
        });
    }

    public void reportSelected(String report){
        switch (report){
            case "Accounting":
            case "Conversion Rate": {
                label1.setVisible(true);
                label2.setVisible(true);

                dpFrom.setVisible(true);
                dpTo.setVisible(true);

                dpFrom.setValue(LocalDate.now().withDayOfMonth(1));
                dpTo.setValue(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
                break;
            }
            case "Best Locations":
            case "Most Picnic Types":
            case "Best Addons":
            default:
                label1.setVisible(false);
                label2.setVisible(false);

                dpFrom.setVisible(false);
                dpTo.setVisible(false);
                break;
        }
    }

    public void generatePushed(ActionEvent event){
        String report = cbReport.getSelectionModel().getSelectedItem();
        LocalDate fromDate = dpFrom.getValue();
        LocalDate toDate =dpTo.getValue();

        ExportToExcel exportToExcel = new ExportToExcel();
        exportToExcel.startExport(report, fromDate, toDate);
    }
}
