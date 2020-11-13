package controllers;

import com.google.api.services.gmail.GmailScopes;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CustomersApi;
import com.squareup.square.api.InvoicesApi;
import com.squareup.square.api.OrdersApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;
import entities.Controller;

import entities.GmailService;
import entities.GoogleCalendarService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import models.*;
import models.Customer;
import models.Invoice;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

public class AddEventController extends Controller implements Initializable{

    private final float TAX_RATE = 6.25f;
    private final String LOCATION_ID = "4Y2RSJHP22YNJ";
    private final String ACCESS_TOKEN = "EAAAEOW91CrQRyYWRf3plX_j0fJ1abT18SVTi6qSlrK25SMqyzLYmEpN2Sd3D7H4";

    @FXML private Label label1;

    @FXML private TextField customer_textField1;
    @FXML private TextField customer_textField2;
    @FXML private TextField customer_textField3;
    @FXML private TextField customer_textField4;

    @FXML private CheckBox checkbox1;

    @FXML private DatePicker event_datePicker;
    @FXML private TextField event_textfield1;
    @FXML private TextField event_textfield2;
    @FXML private TextField event_textfield3;
    @FXML private TextField event_textfield4;
    @FXML private TextField event_textfield5;
    @FXML private TextField event_textfield6;
    @FXML private TextField event_textfield7;
    @FXML private TextField event_textfield8;
    @FXML private TextField event_textfield9;
    @FXML private TextField textFieldDiscount;

    @FXML private TableColumn<InvoiceItem, String> column1;
    @FXML private TableColumn<InvoiceItem, String> column2;
    @FXML private TableColumn<InvoiceItem, String> column3;
    @FXML private TableColumn<InvoiceItem, String> column4;
    @FXML private TableColumn<InvoiceItem, String> column5;

    @FXML private TableView<InvoiceItem> tableView;

    private ObservableList<InvoiceItem> invoiceItemObservableList;
    private InvoiceItem selectedAddon;

    private EventController eventController;

    private boolean isNew = false;
    private boolean createSquareInvoice = false;
    private boolean isImport = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        invoiceItemObservableList = FXCollections.observableArrayList();
        tableView.setItems(invoiceItemObservableList);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAddon = newSelection;
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Remove Addon");
        menuItem1.setOnAction(event -> removeAddon() );
        MenuItem menuItem2 = new MenuItem("New Addon");
        menuItem2.setOnAction(event -> newAddon() );
        MenuItem menuItem3 = new MenuItem("Add Discount");
        menuItem2.setOnAction(event -> newAddon() );

        contextMenu.getItems().add(menuItem1);
        contextMenu.getItems().add(menuItem2);
        tableView.setContextMenu(contextMenu);

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column4.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column5.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));

        column1.setCellValueFactory(new PropertyValueFactory<>("itemDesc"));
        column2.setCellValueFactory(new PropertyValueFactory<>("itemQuantityString"));
        column3.setCellValueFactory(new PropertyValueFactory<>("itemCostString"));
        column4.setCellValueFactory(new PropertyValueFactory<>("itemSupplierCostString"));
        column5.setCellValueFactory(new PropertyValueFactory<>("note"));

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

        checkbox1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.createSquareInvoice = checkbox1.isSelected();
        });

        checkbox1.setSelected(true);
    }

    public void autoPopulateEdit(){
        if(selectedEvent != null){
            customer_textField1.setText(selectedEvent.getCustomer().getName());
            customer_textField2.setText(selectedEvent.getCustomer().getEmail());
            customer_textField3.setText(selectedEvent.getCustomer().getPhone());
            customer_textField4.setText(selectedEvent.getCustomer().getSource());

            event_datePicker.setValue(selectedEvent.getPicnicDateTime().toLocalDate());
            event_textfield1.setText(selectedEvent.getPicnicTimeString());
            event_textfield2.setText(selectedEvent.getGuestCount());

            InvoiceItem guestInvoiceItem = InvoiceItem.findGuestCountByInvoice(selectedEvent.getInvoiceId());
            if(guestInvoiceItem.getItemCost() > 0){
                event_textfield3.setText(String.valueOf(guestInvoiceItem.getItemCost()));
            }

            event_textfield4.setText(selectedEvent.getEventLocation());
            event_textfield5.setText(selectedEvent.getEventAddress());
            event_textfield6.setText(selectedEvent.getStyle());
            event_textfield7.setText(selectedEvent.getCustomPalette());
            event_textfield8.setText(selectedEvent.getEventType());
            textFieldDiscount.setText(String.valueOf(selectedEvent.getInvoice().getDiscountPercentage()));

            InvoiceItem shippingInvoiceItem = InvoiceItem.findShippingByInvoice(selectedEvent.getInvoiceId());
            if(shippingInvoiceItem.getItemCost() > 0){
                event_textfield9.setText(String.valueOf(shippingInvoiceItem.getItemCost()));
            }

            invoiceItemObservableList = InvoiceItem.findAllAddonsByInvoiceID(selectedEvent.getInvoiceId());
            tableView.setItems(invoiceItemObservableList);
        }
    }

    public void autoPopulateImport(){
        if(selectedSquareEmail != null){
            customer_textField1.setText(selectedSquareEmail.getEventName());
            customer_textField2.setText(selectedSquareEmail.getEventEmail());
            customer_textField3.setText(selectedSquareEmail.getEventPhoneNumber());
            customer_textField4.setText(selectedSquareEmail.getEventSource());

            boolean isAm = selectedSquareEmail.getEventTime().toUpperCase().contains("AM");
            String timeString;
            String dateString = selectedSquareEmail.getEventDate();
            if(isAm)
                timeString = selectedSquareEmail.getEventTime().toUpperCase().split("AM")[0].trim();
            else
                timeString = selectedSquareEmail.getEventTime().toUpperCase().split("PM")[0].trim();

            String[] timeArray = timeString.split(":");
            String[] dateArray = dateString.split("/");
            timeString = "";
            dateString = "";
            for (int i = 0; i < timeArray.length; i++) {
                if(timeArray[i].length() == 1) {
                    timeArray[i] = "0" + timeArray[i];
                }
                if(i == 0){
                    timeArray[0] = isAm ? timeArray[0] : String.valueOf((Integer.parseInt(timeArray[0]) % 12) + 12);
                }
                if(i + 1 < timeArray.length)
                    timeString += timeArray[i] + ":";
                else{
                    timeString += timeArray[i];
                }
            }

            for (int i = 0; i < dateArray.length; i++) {
                if(i < 2 && dateArray[i].length() == 1) {
                    dateArray[i] = "0" + dateArray[i];
                }
                if(i + 1 < timeArray.length)
                    dateString += dateArray[i] + "/";
                else{
                    dateString += dateArray[i];
                }
            }

            DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(dateString, formatter);

            event_datePicker.setValue(date);
            event_textfield1.setText(timeString);
            event_textfield2.setText(selectedSquareEmail.getEventGuestCount());

            Addon guestPriceAddon = new Addon();
            guestPriceAddon.setName(selectedSquareEmail.getEventGuestCount() + " Guests");
            if(guestPriceAddon.exists()){
                guestPriceAddon = Addon.findByName(selectedSquareEmail.getEventGuestCount() + " Guests");
                event_textfield3.setText(String.valueOf(guestPriceAddon.getPrice()));
            }

            event_textfield4.setText(selectedSquareEmail.getEventLocation());
            event_textfield5.setText(selectedSquareEmail.getEventAddress());
            event_textfield6.setText(selectedSquareEmail.getEventStyle());
            event_textfield7.setText(selectedSquareEmail.getCustomPalette());
            event_textfield8.setText(selectedSquareEmail.getEventType());

            String[] addonsArray = selectedSquareEmail.getEventAddonsArray();
            if (addonsArray != null) {
                for (int i = 0; i < addonsArray.length; i++) {
                    if(addonsArray[i].isEmpty())
                        continue;
                    if(addonsArray[i].toLowerCase().trim().equals("none"))
                        continue;
                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setItemDesc(addonsArray[i]);
                    Addon addon = new Addon();
                    addon.setName(addonsArray[i]);
                    if(addon.getName().toLowerCase().equals("marquee letter")) {
                        invoiceItem.setNote(selectedSquareEmail.getMarqueeLetters());
                        if(selectedSquareEmail.getMarqueeLetters() != null)
                            invoiceItem.setItemQuantity(selectedSquareEmail.getMarqueeLetters().replaceAll("\\s", "").length());
                    }
                    if(addon.exists()) {
                        addon = Addon.findByName(addonsArray[i]);
                        invoiceItem.setItemCost(addon.getPrice());
                        invoiceItem.setItemSupplierCost(addon.getSupplierCost());
                    }
                    invoiceItemObservableList.add(invoiceItem);
                }
            }
            tableView.setItems(invoiceItemObservableList);
        }
    }

    public void setIsNew(boolean isNew){
        this.isNew = isNew;
        if(!isNew){
            label1.setText("Edit Event");
            if(selectedEvent.getInvoiceId() >= 10000000 && selectedEvent.getInvoice().getSquareInvoiceID() == null) {
                checkbox1.setVisible(false);
                checkbox1.setDisable(true);
            }else{
                checkbox1.setText("Update Square Invoice");
                checkbox1.setSelected(true);
            }
            customer_textField1.setDisable(true);
            customer_textField2.setDisable(true);
            customer_textField3.setDisable(true);
            customer_textField4.setDisable(true);
            autoPopulateEdit();
            if(selectedEvent != null){
                if(selectedEvent.getInvoice().getIsPaid()){
                    label1.setText("Re-schedule Event");
                    checkbox1.setVisible(false);
                    checkbox1.setDisable(true);
                    event_textfield2.setDisable(true);
                    event_textfield3.setDisable(true);
                    event_textfield4.setDisable(true);
                    event_textfield5.setDisable(true);
                    event_textfield6.setDisable(true);
                    event_textfield7.setDisable(true);
                    event_textfield8.setDisable(true);
                    event_textfield9.setDisable(true);
                    textFieldDiscount.setDisable(true);
                    tableView.setDisable(true);
                }
            }
        }
    }

    public void setIsImport(boolean isImport){
        this.isImport = isImport;
        if(isImport){
            autoPopulateImport();
        }
    }

    public void editRecord(TableColumn.CellEditEvent<InvoiceItem, String> event, int column){
        selectedAddon = tableView.getSelectionModel().getSelectedItem();
        boolean success = false;
        String errorMessage = "There was an error updating the record.";
        try {
            switch (column) {
                case 1:
                    selectedAddon.setItemDesc(event.getNewValue());
                    Addon tmpAddon = new Addon();
                    tmpAddon.setName(event.getNewValue());
                    if(tmpAddon.exists()) {
                        tmpAddon = Addon.findByName(tmpAddon.getName());
                        selectedAddon.setItemSupplierCost(tmpAddon.getSupplierCost());
                        selectedAddon.setItemCost(tmpAddon.getPrice());
                    }
                    success = true;
                    break;
                case 2:
                    int tmpQuantity = Integer.parseInt(event.getNewValue());
                    selectedAddon.setItemQuantity(tmpQuantity);
                    success = true;
                    break;
                case 3:
                    double tmpCost = Double.parseDouble(event.getNewValue());
                    selectedAddon.setItemCost(tmpCost);
                    success = true;
                    break;
                case 4:
                    double tmpSupCost = Double.parseDouble(event.getNewValue());
                    selectedAddon.setItemSupplierCost(tmpSupCost);
                    success = true;
                    break;
                case 5:
                    selectedAddon.setNote(event.getNewValue());
                    success = true;
                    break;
                default:
                    break;
            }
        }catch(Exception ex){
            errorMessage = "Please enter a valid number.";
        }
        if (!success){
            switch (column){
                case 1:
                    selectedAddon.setItemDesc(event.getOldValue());
                    break;
                case 2:
                    selectedAddon.setItemQuantity(Integer.parseInt(event.getOldValue()));
                    break;
                case 3:
                    selectedAddon.setItemCost(Double.parseDouble(event.getOldValue()));
                    break;
                case 4:
                     selectedAddon.setItemSupplierCost(Double.parseDouble(event.getOldValue()));
                    break;
                default:
                    break;
            }
            failureAlert.setContentText(errorMessage);
            failureAlert.showAndWait();
        }
        tableView.refresh();
    }

    public void setParentController(EventController eventController){
        this.eventController = eventController;
    }

    public void removeAddon(){
        if(selectedAddon != null)
            invoiceItemObservableList.remove(selectedAddon);
        tableView.refresh();
    }

    public void newAddon(){
        InvoiceItem tmp = new InvoiceItem();
        tmp.setItemDesc("New Addon");
        invoiceItemObservableList.add(tmp);
        tableView.refresh();
    }

    public void rescheduleEvent(ActionEvent event){
        if(validateFields()) {
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                LocalDate picnicLocalDate = event_datePicker.getValue();
                int year = picnicLocalDate.getYear();
                int month = picnicLocalDate.getMonthValue();
                int day = picnicLocalDate.getDayOfMonth();
                String timeString = event_textfield1.getText();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = year + "-" + month + "-" + day + " " + timeString;
                Date picnicDate;

                try {
                    picnicDate = formatter.parse(dateString);
                    selectedEvent.setPicnicDateTime(Instant.ofEpochMilli(picnicDate.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());

                    if(selectedEvent.edit()) {
                        if(selectedEvent.getGoogleCalendarId() !=  null) {
                            String conString = confirmationAlert.getContentText();
                            confirmationAlert.setContentText("Do you want to update the Google Calendar event?");
                            confirmationAlert.showAndWait().filter(selection -> selection == ButtonType.OK).ifPresent(selection -> {
                                GoogleCalendarService googleCalendarService = new GoogleCalendarService();
                                try {
                                    googleCalendarService.GCalendar();
                                } catch (IOException | GeneralSecurityException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    DateTimeFormatter formatter3= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                    String startTime = selectedEvent.getPicnicDateTime().plusHours(6).format(formatter3);
                                    String endTime = selectedEvent.getPicnicDateTime().plusHours(7).format(formatter3);
                                    googleCalendarService.updateEvent(selectedEvent.getGoogleCalendarId(), startTime, endTime);
                                } catch (IOException | URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            });
                            confirmationAlert.setContentText(conString);
                        }

                        eventController.refreshTable();
                        closeChildWindow(event);
                    } else {
                        failureAlert.setContentText("Error updating the record.");
                        failureAlert.showAndWait();
                    }

                } catch (Exception e) {
                    failureAlert.setContentText("Error updating the record.");
                    failureAlert.showAndWait();
                    e.printStackTrace();
                    return;
                }
            });
        }
    }

    public void editEvent(ActionEvent event){
        if(validateFields()) {
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                float subtotal = 0f;

                double guestPrice = Double.parseDouble(event_textfield3.getText());
                double shippingPrice = Double.parseDouble(event_textfield9.getText());
                Double discountPercentage = 0d;

                if(!textFieldDiscount.getText().trim().equals("")) {
                    discountPercentage = Double.parseDouble(textFieldDiscount.getText().replace("%", "").trim());
                }

                subtotal += guestPrice + shippingPrice;

                String name = customer_textField1.getText();
                String email = customer_textField2.getText();
                String phone = customer_textField3.getText();
                String source = customer_textField4.getText();

                for (InvoiceItem invoiceItem :invoiceItemObservableList) {
                    subtotal += invoiceItem.getItemQuantity() * invoiceItem.getItemCost();
                }

                if(discountPercentage != 0d){
                    subtotal = (float)(subtotal - (subtotal * (discountPercentage / 100)));
                }

                selectedEvent.getInvoice().setSubtotal(subtotal);
                selectedEvent.getInvoice().setTotal(Math.round((subtotal * (1 + (TAX_RATE / 100.0))) * 100.0) / 100.0f);
                selectedEvent.getInvoice().setDiscountPercentage(discountPercentage);
                selectedEvent.getInvoice().edit();

                if(selectedEvent.getInvoiceId() < 10000000 && createSquareInvoice && selectedEvent.getInvoice().getSquareInvoiceID() != null) {
                    DateTimeFormatter formatterPicnicDate = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    DateTimeFormatter formatterPicnicTime = DateTimeFormatter.ofPattern("hh:mm a");
                    LocalTime picnicTime = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String addressForSquare;

                    if(event_textfield4.getText().toLowerCase().contains("home delivery")){
                        addressForSquare = event_textfield5.getText();
                    } else {
                        addressForSquare = event_textfield4.getText().split("\\(")[0] + ", Houston TX";
                    }

                    String guestCountNote = "";
                    guestCountNote += (event_textfield7.getText().isEmpty() ? event_textfield6.getText() : event_textfield7.getText()) + "\n\n";
                    guestCountNote += formatterPicnicDate.format(event_datePicker.getValue()) + "\n";
                    guestCountNote += formatterPicnicTime.format(picnicTime) + "\n";

                    try {
                        SquareClient client = new SquareClient.Builder()
                                .environment(Environment.PRODUCTION)
                                .accessToken(ACCESS_TOKEN)
                                .build();

                        InvoicesApi invoicesApi = client.getInvoicesApi();
                        OrdersApi ordersApi = client.getOrdersApi();

                        com.squareup.square.models.Invoice oldInvoice = invoicesApi.getInvoice(selectedEvent.getInvoice().getSquareInvoiceID()).getInvoice();
                        String squareCustomerID = oldInvoice.getPrimaryRecipient().getCustomerId();

                        CancelInvoiceRequest cancelInvoiceBody = new CancelInvoiceRequest.Builder(oldInvoice.getVersion())
                                .build();

                        invoicesApi.cancelInvoice(selectedEvent.getInvoice().getSquareInvoiceID(), cancelInvoiceBody);

                        LinkedList<OrderLineItem> lineItems = new LinkedList<>();

                        Money basePriceMoneyGC = new Money.Builder()
                                .amount(Math.round(Double.parseDouble(event_textfield3.getText())) * 100)
                                .currency("USD")
                                .build();

                        OrderLineItem orderLineItemGC = new OrderLineItem.Builder("1")
                                .name(event_textfield2.getText() + " Guests")
                                .basePriceMoney(basePriceMoneyGC)
                                .note(guestCountNote)
                                .build();
                        lineItems.add(orderLineItemGC);

                        for (InvoiceItem squareInvoiceItem : invoiceItemObservableList) {
                            String addonNote = squareInvoiceItem.getNote();
                            Money basePriceMoney = new Money.Builder()
                                    .amount(Math.round(squareInvoiceItem.getItemCost()) * 100)
                                    .currency("USD")
                                    .build();

                            OrderLineItem orderLineItem = new OrderLineItem.Builder(String.valueOf(squareInvoiceItem.getItemQuantity()))
                                    .name(squareInvoiceItem.getItemDesc())
                                    .basePriceMoney(basePriceMoney)
                                    .note(addonNote)
                                    .build();
                            lineItems.add(orderLineItem);
                        }

                        Money basePriceMoneySH = new Money.Builder()
                                .amount(Math.round(Double.parseDouble(event_textfield9.getText())) * 100)
                                .currency("USD")
                                .build();

                        OrderLineItem orderLineItemSH = new OrderLineItem.Builder("1")
                                .name("Delivery")
                                .basePriceMoney(basePriceMoneySH)
                                .note(addressForSquare)
                                .build();
                        lineItems.add(orderLineItemSH);

                        OrderLineItemTax orderLineItemTax = new OrderLineItemTax.Builder()
                                .name("Sales Tax")
                                .percentage("6.25")
                                .scope("ORDER")
                                .build();

                        LinkedList<OrderLineItemTax> taxes = new LinkedList<>();
                        taxes.add(orderLineItemTax);

                        Order order;
                        if(!textFieldDiscount.getText().trim().equals(""))
                        {
                            Double discount = Double.parseDouble(textFieldDiscount.getText().replace("%", "").trim());

                            OrderLineItemDiscount orderLineItemDiscount = new OrderLineItemDiscount.Builder()
                                    .name("Discount: ")
                                    .percentage(String.valueOf(discount))
                                    .scope("ORDER")
                                    .build();

                            LinkedList<OrderLineItemDiscount> discounts = new LinkedList<>();
                            discounts.add(orderLineItemDiscount);

                            order = new Order.Builder(LOCATION_ID)
                                    .lineItems(lineItems)
                                    .taxes(taxes)
                                    .discounts(discounts)
                                    .customerId(squareCustomerID)
                                    .build();
                        } else {
                            order = new Order.Builder(LOCATION_ID)
                                    .lineItems(lineItems)
                                    .taxes(taxes)
                                    .customerId(squareCustomerID)
                                    .build();
                        }

                        CreateOrderRequest orderBody = new CreateOrderRequest.Builder()
                                .order(order)
                                .idempotencyKey(UUID.randomUUID().toString())
                                .build();

                        CreateOrderResponse orderResult = ordersApi.createOrder(orderBody);
                        String squareOrderID = orderResult.getOrder().getId();

                        InvoiceRecipient primaryRecipient = new InvoiceRecipient.Builder()
                                .customerId(squareCustomerID)
                                .build();

                        InvoicePaymentReminder invoicePaymentReminder1 = new InvoicePaymentReminder.Builder()
                                .relativeScheduledDays(0)
                                .build();

                        InvoicePaymentReminder invoicePaymentReminder2 = new InvoicePaymentReminder.Builder()
                                .relativeScheduledDays(1)
                                .build();

                        InvoicePaymentReminder invoicePaymentReminder3 = new InvoicePaymentReminder.Builder()
                                .relativeScheduledDays(4)
                                .build();

                        LinkedList<InvoicePaymentReminder> reminders = new LinkedList<>();
                        reminders.add(invoicePaymentReminder1);
                        reminders.add(invoicePaymentReminder2);
                        reminders.add(invoicePaymentReminder3);

                        LocalDate eventDate = event_datePicker.getValue();
                        long daysBetween = DAYS.between(LocalDate.now(), eventDate);
                        LocalDate dueDate = daysBetween < 14 ? LocalDate.now() : eventDate.minusDays(14);
                        DateTimeFormatter formatterDueDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String dueDateString = formatterDueDate.format(dueDate);
                        InvoicePaymentRequest invoicePaymentRequest = new InvoicePaymentRequest.Builder()
                                .requestMethod("EMAIL")
                                .requestType("BALANCE")
                                .dueDate(dueDateString)
                                .tippingEnabled(false)
                                .reminders(reminders)
                                .build();

                        LinkedList<InvoicePaymentRequest> paymentRequests = new LinkedList<>();
                        paymentRequests.add(invoicePaymentRequest);

                        com.squareup.square.models.Invoice squareInvoice = new com.squareup.square.models.Invoice.Builder()
                                .locationId(LOCATION_ID)
                                .orderId(squareOrderID)
                                .primaryRecipient(primaryRecipient)
                                .paymentRequests(paymentRequests)
                                .title("Fancy Picnics")
                                .description("Thanks for choosing Fancy Picnics! We will arrive to the location between 3 and 1 hour prior your starting time!")
                                .build();

                        CreateInvoiceRequest invoiceBody = new CreateInvoiceRequest.Builder(squareInvoice)
                                .idempotencyKey(UUID.randomUUID().toString())
                                .build();

                        CreateInvoiceResponse invoiceResult = invoicesApi.createInvoice(invoiceBody);
                        selectedEvent.getInvoice().setSquareInvoiceID(invoiceResult.getInvoice().getId());
                        PublishInvoiceRequest publishBody = new PublishInvoiceRequest.Builder(
                                invoiceResult.getInvoice().getVersion())
                                .idempotencyKey(UUID.randomUUID().toString())
                                .build();

                        PublishInvoiceResponse publishInvoiceResponse = invoicesApi.publishInvoice(invoiceResult.getInvoice().getId(), publishBody);
                    } catch (ApiException | IOException e) {
                        e.printStackTrace();
                    }
                }

                InvoiceItem.deleteAllFromInvoice(selectedEvent.getInvoiceId());

                InvoiceItem guestItem = new InvoiceItem();
                guestItem.setInvoiceId(selectedEvent.getInvoiceId());
                guestItem.setItemQuantity(1);
                guestItem.setItemCost(guestPrice);
                guestItem.setItemDesc(event_textfield2.getText() + " Guests");
                guestItem.setItemSupplierCost(0);

                Addon addonGuestCount = Addon.findByName(guestItem.getItemDesc());
                if(addonGuestCount == null){
                    addonGuestCount = new Addon();
                    addonGuestCount.setName(guestItem.getItemDesc());
                    addonGuestCount.setTypeCode("GC");
                    addonGuestCount.setPrice(guestItem.getItemCost());
                    addonGuestCount.setSupplierCost(guestItem.getItemSupplierCost());
                    addonGuestCount.add();
                } else {
                    addonGuestCount.setPrice(guestItem.getItemCost());
                    addonGuestCount.setSupplierCost(guestItem.getItemSupplierCost());
                    addonGuestCount.edit();
                }

                guestItem.add();

                for (InvoiceItem invoiceItem : invoiceItemObservableList) {
                    invoiceItem.setInvoiceId(selectedEvent.getInvoiceId());
                    Addon addon = Addon.findByName(invoiceItem.getItemDesc());
                    if(addon == null){
                        addon = new Addon();
                        addon.setName(invoiceItem.getItemDesc());
                        addon.setTypeCode("AD");
                        addon.setPrice(invoiceItem.getItemCost());
                        addon.setSupplierCost(invoiceItem.getItemSupplierCost());
                        addon.add();
                    } else {
                        addon.setPrice(invoiceItem.getItemCost());
                        addon.setSupplierCost(invoiceItem.getItemSupplierCost());
                        addon.edit();
                    }
                    invoiceItem.add();
                }

                LocalDate date = event_datePicker.getValue();
                LocalTime time = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                String guestCount = event_textfield2.getText();

                LocalDate picnicLocalDate = event_datePicker.getValue();
                int year = picnicLocalDate.getYear();
                int month = picnicLocalDate.getMonthValue();
                int day = picnicLocalDate.getDayOfMonth();
                String timeString = event_textfield1.getText();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = year + "-" + month + "-" + day + " " + timeString;
                Date picnicDate = new Date();

                try {
                    picnicDate  = formatter.parse(dateString);
                } catch(Exception e){
                    e.printStackTrace();
                }

                InvoiceItem shippingItem = new InvoiceItem();
                shippingItem.setInvoiceId(selectedEvent.getInvoiceId());
                shippingItem.setItemQuantity(1);
                shippingItem.setItemCost(shippingPrice);
                shippingItem.setItemDesc("Event:" + selectedEvent.getInvoiceId() + " Shipping");
                shippingItem.setItemSupplierCost(0);

                Addon addonShipping = Addon.findByName(shippingItem.getItemDesc());
                if(addonShipping == null){
                    addonShipping = new Addon();
                    addonShipping.setName(shippingItem.getItemDesc());
                    addonShipping.setTypeCode("SH");
                    addonShipping.setPrice(shippingItem.getItemCost());
                    addonShipping.setSupplierCost(shippingItem.getItemSupplierCost());
                    addonShipping.add();
                } else {
                    addonShipping.setPrice(guestItem.getItemCost());
                    addonShipping.setSupplierCost(guestItem.getItemSupplierCost());
                    addonShipping.edit();
                }

                shippingItem.add();


                selectedEvent.setPicnicDateTime(Instant.ofEpochMilli(picnicDate.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());

                selectedEvent.setGuestCount(event_textfield2.getText());
                selectedEvent.setEventLocation(event_textfield4.getText());

                if(event_textfield4.getText().toLowerCase().contains("home delivery")){
                    selectedEvent.setEventAddress(event_textfield5.getText());
                } else {
                    selectedEvent.setEventAddress(event_textfield4.getText().split("\\(")[0] + ", Houston TX");
                }

                selectedEvent.setEventType(event_textfield8.getText());
                selectedEvent.setStyle(event_textfield6.getText());
                selectedEvent.setCustomPalette(event_textfield7.getText());
                selectedEvent.edit();

                successAlert.setContentText("Success.");
                successAlert.showAndWait();
                eventController.refreshTable();
                closeChildWindow(event);
            });
        }
    }

    public void addButtonPushed(ActionEvent event) {
        if(!isNew && selectedEvent != null){
            if(selectedEvent.getInvoice().getIsPaid()){
                rescheduleEvent(event);
                return;
            } else {
                editEvent(event);
                return;
            }
        }
        if(validateFields()){
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                float subtotal = 0f;

                double guestPrice = Double.parseDouble(event_textfield3.getText());
                double shippingPrice = Double.parseDouble(event_textfield9.getText());
                Double discountPercentage = 0d;

                if(!textFieldDiscount.getText().trim().equals("")) {
                    discountPercentage = Double.parseDouble(textFieldDiscount.getText().replace("%", "").trim());
                }

                subtotal += guestPrice + shippingPrice;

                for (InvoiceItem invoiceItem :invoiceItemObservableList) {
                    subtotal += invoiceItem.getItemQuantity() * invoiceItem.getItemCost();
                }

                String name = customer_textField1.getText();
                String email = customer_textField2.getText();
                String phone = customer_textField3.getText();
                String source = customer_textField4.getText();

                Customer customer = Customer.findByEmail(email);
                if (customer == null) {
                    customer = new Customer();
                    customer.setName(name);
                    customer.setPhone(phone);
                    customer.setSource(source);
                    customer.setEmail(email);
                    customer.add();
                    customer.getIDFromDB();
                } else {
                    customer.setPhone(phone);
                    customer.setSource(source);
                    customer.edit();
                }

                if(discountPercentage != 0d){
                    subtotal = (float)(subtotal - (subtotal * (discountPercentage / 100)));
                }

                Invoice invoice = new Invoice();
                invoice.setSubtotal(subtotal);
                invoice.setTaxRate(TAX_RATE);
                invoice.setIsPaid(false);
                invoice.setTotal(Math.round((subtotal * (1 + (TAX_RATE / 100.0))) * 100.0) / 100.0f);
                invoice.setDiscountPercentage(discountPercentage);

                if(isNew){
                    if(!createSquareInvoice) {
                        invoice.setID(invoice.getNewIDFromDB());
                    } else {
                        SquareClient client = new SquareClient.Builder()
                                .environment(Environment.PRODUCTION)
                                .accessToken(ACCESS_TOKEN)
                                .build();

                        String[] customerFullName = customer.getName().split(" ");
                        String customerFirstName = customerFullName[0];
                        String customerLastName = customerFullName.length > 1 ? customerFullName[1] : "";
                        String customerEmail = customer.getEmail();
                        String customerPhoneNumber = customer.getPhone();

                        DateTimeFormatter formatterPicnicDate = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                        DateTimeFormatter formatterPicnicTime = DateTimeFormatter.ofPattern("hh:mm a");
                        LocalTime picnicTime = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                        String addressForSquare;

                        if(event_textfield4.getText().toLowerCase().contains("home delivery")){
                            addressForSquare = event_textfield5.getText();
                        } else {
                            addressForSquare = event_textfield4.getText().split("\\(")[0] + ", Houston TX";
                        }

                        String guestCountNote = "";
                        guestCountNote += (event_textfield7.getText().isEmpty() ? event_textfield6.getText() : event_textfield7.getText()) + "\n\n";
                        guestCountNote += formatterPicnicDate.format(event_datePicker.getValue()) + "\n";
                        guestCountNote += formatterPicnicTime.format(picnicTime) + "\n";

                        CustomersApi customersApi = client.getCustomersApi();
                        CreateCustomerRequest customerBody = new CreateCustomerRequest.Builder()
                                .givenName(customerFirstName)
                                .familyName(customerLastName)
                                .emailAddress(customerEmail)
                                .phoneNumber(customerPhoneNumber)
                                .build();

                        try {
                            CreateCustomerResponse customerResult = customersApi.createCustomer(customerBody);
                            String squareCustomerID = customerResult.getCustomer().getId();
                            OrdersApi ordersApi = client.getOrdersApi();
                            LinkedList<OrderLineItem> lineItems = new LinkedList<>();

                            Money basePriceMoneyGC = new Money.Builder()
                                    .amount(Math.round(Double.parseDouble(event_textfield3.getText())) * 100)
                                    .currency("USD")
                                    .build();

                            OrderLineItem orderLineItemGC = new OrderLineItem.Builder("1")
                                    .name(event_textfield2.getText() + " Guests")
                                    .basePriceMoney(basePriceMoneyGC)
                                    .note(guestCountNote)
                                    .build();
                            lineItems.add(orderLineItemGC);

                            for (InvoiceItem squareInvoiceItem : invoiceItemObservableList) {
                                String addonNote = squareInvoiceItem.getNote();
                                Money basePriceMoney = new Money.Builder()
                                        .amount(Math.round(squareInvoiceItem.getItemCost()) * 100)
                                        .currency("USD")
                                        .build();

                                OrderLineItem orderLineItem = new OrderLineItem.Builder(String.valueOf(squareInvoiceItem.getItemQuantity()))
                                        .name(squareInvoiceItem.getItemDesc())
                                        .basePriceMoney(basePriceMoney)
                                        .note(addonNote)
                                        .build();
                                lineItems.add(orderLineItem);
                            }

                            Money basePriceMoneySH = new Money.Builder()
                                    .amount(Math.round(Double.parseDouble(event_textfield9.getText())) * 100)
                                    .currency("USD")
                                    .build();

                            OrderLineItem orderLineItemSH = new OrderLineItem.Builder("1")
                                    .name("Delivery")
                                    .basePriceMoney(basePriceMoneySH)
                                    .note(addressForSquare)
                                    .build();
                            lineItems.add(orderLineItemSH);

                            OrderLineItemTax orderLineItemTax = new OrderLineItemTax.Builder()
                                    .name("Sales Tax")
                                    .percentage("6.25")
                                    .scope("ORDER")
                                    .build();

                            LinkedList<OrderLineItemTax> taxes = new LinkedList<>();
                            taxes.add(orderLineItemTax);

                            Order order;
                            if(!textFieldDiscount.getText().trim().equals(""))
                            {
                                Double discount = Double.parseDouble(textFieldDiscount.getText().replace("%", "").trim());

                                OrderLineItemDiscount orderLineItemDiscount = new OrderLineItemDiscount.Builder()
                                        .name("Discount: ")
                                        .percentage(String.valueOf(discount))
                                        .scope("ORDER")
                                        .build();

                                LinkedList<OrderLineItemDiscount> discounts = new LinkedList<>();
                                discounts.add(orderLineItemDiscount);

                                order = new Order.Builder(LOCATION_ID)
                                        .lineItems(lineItems)
                                        .taxes(taxes)
                                        .discounts(discounts)
                                        .customerId(squareCustomerID)
                                        .build();
                            } else {
                                order = new Order.Builder(LOCATION_ID)
                                        .lineItems(lineItems)
                                        .taxes(taxes)
                                        .customerId(squareCustomerID)
                                        .build();
                            }

                            CreateOrderRequest orderBody = new CreateOrderRequest.Builder()
                                    .order(order)
                                    .idempotencyKey(UUID.randomUUID().toString())
                                    .build();

                            CreateOrderResponse orderResult = ordersApi.createOrder(orderBody);
                            String squareOrderID = orderResult.getOrder().getId();
                            InvoicesApi invoicesApi = client.getInvoicesApi();
                            InvoiceRecipient primaryRecipient = new InvoiceRecipient.Builder()
                                    .customerId(squareCustomerID)
                                    .build();

                            InvoicePaymentReminder invoicePaymentReminder1 = new InvoicePaymentReminder.Builder()
                                    .relativeScheduledDays(0)
                                    .build();

                            InvoicePaymentReminder invoicePaymentReminder2 = new InvoicePaymentReminder.Builder()
                                    .relativeScheduledDays(1)
                                    .build();

                            InvoicePaymentReminder invoicePaymentReminder3 = new InvoicePaymentReminder.Builder()
                                    .relativeScheduledDays(4)
                                    .build();

                            LinkedList<InvoicePaymentReminder> reminders = new LinkedList<>();
                            reminders.add(invoicePaymentReminder1);
                            reminders.add(invoicePaymentReminder2);
                            reminders.add(invoicePaymentReminder3);

                            LocalDate eventDate = event_datePicker.getValue();
                            long daysBetween = DAYS.between(LocalDate.now(), eventDate);
                            LocalDate dueDate = daysBetween < 14 ? LocalDate.now() : eventDate.minusDays(14);
                            DateTimeFormatter formatterDueDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String dueDateString = formatterDueDate.format(dueDate);
                            InvoicePaymentRequest invoicePaymentRequest = new InvoicePaymentRequest.Builder()
                                    .requestMethod("EMAIL")
                                    .requestType("BALANCE")
                                    .dueDate(dueDateString)
                                    .tippingEnabled(false)
                                    .reminders(reminders)
                                    .build();

                            LinkedList<InvoicePaymentRequest> paymentRequests = new LinkedList<>();
                            paymentRequests.add(invoicePaymentRequest);

                            com.squareup.square.models.Invoice squareInvoice = new com.squareup.square.models.Invoice.Builder()
                                    .locationId(LOCATION_ID)
                                    .orderId(squareOrderID)
                                    .primaryRecipient(primaryRecipient)
                                    .paymentRequests(paymentRequests)
                                    .title("Fancy Picnics")
                                    .description("Thanks for choosing Fancy Picnics! We will arrive to the location between 3 and 1 hour prior your starting time!")
                                    .build();

                            CreateInvoiceRequest invoiceBody = new CreateInvoiceRequest.Builder(squareInvoice)
                                    .idempotencyKey(UUID.randomUUID().toString())
                                    .build();

                            CreateInvoiceResponse invoiceResult = invoicesApi.createInvoice(invoiceBody);
                            invoice.setID(Integer.parseInt(invoiceResult.getInvoice().getInvoiceNumber()));
                            invoice.setSquareInvoiceID(invoiceResult.getInvoice().getId());
                            PublishInvoiceRequest publishBody = new PublishInvoiceRequest.Builder(
                                    invoiceResult.getInvoice().getVersion())
                                    .idempotencyKey(UUID.randomUUID().toString())
                                    .build();

                            PublishInvoiceResponse publishInvoiceResponse = invoicesApi.publishInvoice(invoiceResult.getInvoice().getId(), publishBody);

                            GmailService gmailService = new GmailService();
                            gmailService.sendAutoEmail(customerFirstName, customerEmail);
                        } catch (ApiException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    invoice.add();

                    InvoiceItem guestItem = new InvoiceItem();
                    guestItem.setInvoiceId(invoice.getID());
                    guestItem.setItemQuantity(1);
                    guestItem.setItemCost(guestPrice);
                    guestItem.setItemDesc(event_textfield2.getText() + " Guests");
                    guestItem.setItemSupplierCost(0);

                    Addon addonGuestCount = Addon.findByName(guestItem.getItemDesc());
                    if(addonGuestCount == null){
                        addonGuestCount = new Addon();
                        addonGuestCount.setName(guestItem.getItemDesc());
                        addonGuestCount.setTypeCode("GC");
                        addonGuestCount.setPrice(guestItem.getItemCost());
                        addonGuestCount.setSupplierCost(guestItem.getItemSupplierCost());
                        addonGuestCount.add();
                    } else {
                        addonGuestCount.setPrice(guestItem.getItemCost());
                        addonGuestCount.setSupplierCost(guestItem.getItemSupplierCost());
                        addonGuestCount.edit();
                    }

                    guestItem.add();

                    for (InvoiceItem invoiceItem : invoiceItemObservableList) {
                        invoiceItem.setInvoiceId(invoice.getID());
                        Addon addon = Addon.findByName(invoiceItem.getItemDesc());
                        if(addon == null){
                            addon = new Addon();
                            addon.setName(invoiceItem.getItemDesc());
                            addon.setTypeCode("AD");
                            addon.setPrice(invoiceItem.getItemCost());
                            addon.setSupplierCost(invoiceItem.getItemSupplierCost());
                            addon.add();
                        } else {
                            addon.setPrice(invoiceItem.getItemCost());
                            addon.setSupplierCost(invoiceItem.getItemSupplierCost());
                            addon.edit();
                        }
                        invoiceItem.add();
                    }

                    LocalDate date = event_datePicker.getValue();
                    LocalTime time = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String guestCount = event_textfield2.getText();

                    LocalDate picnicLocalDate = event_datePicker.getValue();
                    int year = picnicLocalDate.getYear();
                    int month = picnicLocalDate.getMonthValue();
                    int day = picnicLocalDate.getDayOfMonth();
                    String timeString = event_textfield1.getText();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = year + "-" + month + "-" + day + " " + timeString;
                    Date picnicDate = new Date();

                    try {
                        picnicDate  = formatter.parse(dateString);
                    } catch(Exception e){
                        e.printStackTrace();
                    }

                    InvoiceItem shippingItem = new InvoiceItem();
                    shippingItem.setInvoiceId(invoice.getID());
                    shippingItem.setItemQuantity(1);
                    shippingItem.setItemCost(shippingPrice);
                    shippingItem.setItemDesc("Event:" + invoice.getID() + " Shipping");
                    shippingItem.setItemSupplierCost(0);

                    Addon addonShipping = Addon.findByName(shippingItem.getItemDesc());
                    if(addonShipping == null){
                        addonShipping = new Addon();
                        addonShipping.setName(shippingItem.getItemDesc());
                        addonShipping.setTypeCode("SH");
                        addonShipping.setPrice(shippingItem.getItemCost());
                        addonShipping.setSupplierCost(shippingItem.getItemSupplierCost());
                        addonShipping.add();
                    } else {
                        addonShipping.setPrice(guestItem.getItemCost());
                        addonShipping.setSupplierCost(guestItem.getItemSupplierCost());
                        addonShipping.edit();
                    }

                    shippingItem.add();

                    Event tmpEvent = new Event();
                    tmpEvent.setPicnicDateTime(Instant.ofEpochMilli(picnicDate.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                    tmpEvent.setInvoiceId(invoice.getID());
                    if(isImport){
                        selectedSquareEmail.add();
                        squareEmailObservableList.remove(selectedSquareEmail);

                        tmpEvent.setSquareEmailId(selectedSquareEmail.getId());
                    }
                    tmpEvent.setCustomerId(customer.getID());
                    tmpEvent.setGuestCount(event_textfield2.getText());
                    tmpEvent.setEventLocation(event_textfield4.getText());

                    if(event_textfield4.getText().toLowerCase().contains("home delivery")){
                        tmpEvent.setEventAddress(event_textfield5.getText());
                    } else {
                        tmpEvent.setEventAddress(event_textfield4.getText().split("\\(")[0] + ", Houston TX");
                    }

                    tmpEvent.setEventType(event_textfield8.getText());
                    tmpEvent.setStyle(event_textfield6.getText());
                    tmpEvent.setCustomPalette(event_textfield7.getText());
                    tmpEvent.add();
                }
                successAlert.setContentText("Success.");
                successAlert.showAndWait();

                closeChildWindow(event);
            });
        }
    }

    private boolean validateFields() {
        if(customer_textField1.getText().trim().equals(""))
        {
            failureAlert.setContentText("Please enter customer name.");
            failureAlert.showAndWait();
            return false;
        }

        if(customer_textField2.getText().trim().equals(""))
        {
            failureAlert.setContentText("Please enter customer email.");
            failureAlert.showAndWait();
            return false;
        }

        if(customer_textField3.getText().trim().equals(""))
        {
            failureAlert.setContentText("Please enter customer phone number.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_datePicker.getValue() == null){
            failureAlert.setContentText("Please select a date.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield1.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a time.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield2.getText().trim().equals("")){
            failureAlert.setContentText("Please enter guest count.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield3.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a guests count price.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield4.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a location.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield5.getText().trim().equals("") && event_textfield4.getText().trim().toLowerCase().equals("home delivery")){
            failureAlert.setContentText("Please enter an address for home delivery.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield6.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a style.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield8.getText().trim().equals("")){
            failureAlert.setContentText("Please enter an event type.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield9.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a delivery price.");
            failureAlert.showAndWait();
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate minValueDate = LocalDate.parse("01-01-1753", formatter);
        if(event_datePicker.getValue().isBefore(minValueDate)){
            failureAlert.setContentText("The minimum date allowed is 01-01-1753.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            LocalTime time = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        }catch (Exception ex){
            failureAlert.setContentText("Enter time in the following format: HH:mm:ss.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            double guestPrice = Double.parseDouble(event_textfield3.getText());
        }catch (Exception ex){
            failureAlert.setContentText("Please enter a valid guest price.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            double deliveryPrice = Double.parseDouble(event_textfield9.getText());
        }catch (Exception ex){
            failureAlert.setContentText("Please enter a valid shipping price.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            if(textFieldDiscount.getText() != null) {
                if (!textFieldDiscount.getText().trim().replaceAll("%", "").equals("")) {
                    double discount = Double.parseDouble(textFieldDiscount.getText().replaceAll("%", ""));
                    if(discount < 0 || discount >= 100) {
                        failureAlert.setContentText("Discount percentage has to be between 0 and 100.");
                        failureAlert.showAndWait();
                        return false;
                    }
                }
            }
        }catch (Exception ex){
            failureAlert.setContentText("Please enter a valid discount price.");
            failureAlert.showAndWait();
            return false;
        }


        for (InvoiceItem addon:invoiceItemObservableList) {
            if(addon.getItemDesc().equals("New Addon") || addon.getItemCost() == 0){
                failureAlert.setContentText("Please enter addon details.");
                failureAlert.showAndWait();
                return false;
            }
        }

        return true;
    }

    public void cancelButtonPushed(ActionEvent event){
        closeChildWindow(event);
    }

}

