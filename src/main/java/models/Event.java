package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Event extends QueryObject {

    private int id;
    private String squareEmailId;
    private LocalDateTime picnicDateTime;
    private int invoiceId;
    private int customerId;
    private String guestCount;
    private String eventLocation;
    private String eventAddress;
    private String eventType;
    private int employeeId;
    private String googleCalendarId;
    private String style;
    private String customPalette;

    private Customer customer;
    private List<InvoiceItem> invoiceItemList;
    private Invoice invoice;

    public Event(int id, String squareEmailId, LocalDateTime picnicDateTime, int invoiceId, int customerId, String guestCount, String eventLocation, String eventAddress, String eventType, int employeeId, String googleCalendarId, String style, String customPalette) {
        this.id = id;
        this.squareEmailId = squareEmailId;
        this.picnicDateTime = picnicDateTime;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.guestCount = guestCount;
        this.eventLocation = eventLocation;
        this.eventAddress = eventAddress;
        this.eventType = eventType;
        this.employeeId = employeeId;
        this.googleCalendarId = googleCalendarId;
        this.style = style;
        this.customPalette = customPalette;
    }

    public Event() {
    }

    public boolean edit() {

        statement = "UPDATE event " +
                "SET " +
                "square_email_id = " + (this.getSquareEmailId() == null ? this.getSquareEmailId() : "'" + this.getSquareEmailId().replaceAll("'", "''") + "'") + ", " +
                "picnic_date_time = '" + this.getPicnicDateTimeString() + "', " +
                "invoice_id = " + this.getInvoiceId() + ", " +
                "customer_id = " + this.getCustomerId() + ", " +
                "guest_count = " + (this.getGuestCount() == null ? this.getGuestCount() : "'" + this.getGuestCount().replaceAll("'", "''") + "'") + ", " +
                "event_location = " + (this.getEventLocation() == null ? this.getEventLocation() : "'" + this.getEventLocation().replaceAll("'", "''") + "'") + ", " +
                "event_address = " + (this.getEventAddress() == null ? this.getEventAddress() : "'" + this.getEventAddress().replaceAll("'", "''") + "'") + ", " +
                "event_type = " + (this.getEventType() == null ? this.getEventType() : "'" + this.getEventType().replaceAll("'", "''") + "'") + ", " +
                "employee_id = " + (this.getEmployeeId() == 0 ? "null" : this.getEmployeeId()) + ", " +
                "google_calendar_id = " + (this.getGoogleCalendarId() == null ? this.getGoogleCalendarId() : "'" + this.getGoogleCalendarId().replaceAll("'", "''") + "'") + ", " +
                "style = " + (this.getStyle() == null ? this.getStyle() : "'" + this.getStyle().replaceAll("'", "''") + "'") + ", " +
                "custom_palette = " + (this.getCustomPalette() == null ? this.getCustomPalette() : "'" + this.getCustomPalette().replaceAll("'", "''") + "'") + " " +
                "WHERE id = " + this.getId();

        return executeUpdate(statement);
    }

    public boolean add() {
        statement = "Insert INTO event (square_email_id, picnic_date_time, invoice_id, customer_id, guest_count, event_location, event_address, event_type, employee_id, google_calendar_id, custom_palette, style)  VALUES (" +
                (this.getSquareEmailId() == null ? this.getSquareEmailId() : "'" + this.getSquareEmailId().replaceAll("'", "''") + "'") + ", " +
                "'" + this.getPicnicDateTimeString() + "', " +
                this.getInvoiceId() + ", " +
                this.getCustomerId() + ", " +
                (this.getGuestCount() == null ? this.getGuestCount() : "'" + this.getGuestCount().replaceAll("'", "''") + "'") + ", " +
                (this.getEventLocation() == null ? this.getEventLocation() : "'" + this.getEventLocation().replaceAll("'", "''") + "'") + ", " +
                (this.getEventAddress() == null ? this.getEventAddress() : "'" + this.getEventAddress().replaceAll("'", "''") + "'") + ", " +
                (this.getEventType() == null ? this.getEventType() : "'" + this.getEventType().replaceAll("'", "''") + "'") + ", " +
                (this.getEmployeeId() == 0 ? "null" : this.getEmployeeId()) + ", " +
                (this.getGoogleCalendarId() == null ? this.getGoogleCalendarId() : "'" + this.getGoogleCalendarId().replaceAll("'", "''") + "'") + ", " +
                (this.getCustomPalette() == null ? this.getCustomPalette() : "'" + this.getCustomPalette().replaceAll("'", "''") + "'") + ", " +
                (this.getStyle() == null ? this.getStyle() : "'" + this.getStyle().replaceAll("'", "''") + "'") +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<Event> findAllForTable(LocalDate fromDate, LocalDate toDate) {
        List<Event> events = new ArrayList<>();
        try {
            statement = "SELECT e.id as event_id, square_email_id, picnic_date_time, invoice_id, customer_id, guest_count, event_address, event_type, employee_id, google_calendar_id, custom_palette, style, event_location, c.id as customer_table_id, name, phone, source, email, i.id as invoice_table_id, subtotal, tax_rate, is_paid, total, square_invoice_id, discount_percentage\n" +
                    "FROM event e\n" +
                    "JOIN customer c on e.customer_id = c.id\n" +
                    "JOIN invoice i on i.id = e.invoice_id\n" +
                    "WHERE picnic_date_time BETWEEN '" + fromDate.toString() + "' AND '" + toDate.toString() + "';";
            executeQuery(statement);
            while (resultSet.next()) {
                Event event = new Event();
                setEventFromQuery(event);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(events);
    }


    public static Event findByID(int id) {
        Event event = new Event();
        try {
            statement = "SELECT * FROM event WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEventFromQuery(event);
            } else {
                event = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return event;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSquareEmailId() {
        return squareEmailId;
    }

    public void setSquareEmailId(String squareEmailId) {
        this.squareEmailId = squareEmailId;
    }

    public LocalDateTime getPicnicDateTime() {
        return picnicDateTime;
    }

    public void setPicnicDateTime(LocalDateTime picnicDateTime) {
        this.picnicDateTime = picnicDateTime;
    }

    public String getPicnicDateTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
        return picnicDateTime.format(formatter);
    }

    public String getPicnicTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return picnicDateTime.format(formatter);
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(String guestCount) {
        this.guestCount = guestCount;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCustomPalette() {
        return customPalette;
    }

    public void setCustomPalette(String customPalette) {
        this.customPalette = customPalette;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceItem> getInvoiceItemList() {
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getCustomerName(){
        return getCustomer().getName();
    }

    public String getCustomerEmail(){
        return getCustomer().getEmail();
    }

    public String getCustomerPhone(){
        return getCustomer().getPhone();
    }

    public Float getInvoiceTotal(){
        return getInvoice().getTotal();
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB() {
        setId(getLastID("event"));
    }

    private static void setEventFromQuery(Event event) throws SQLException {
        event.setId(resultSet.getInt("event_id"));
        event.setSquareEmailId(resultSet.getString("square_email_id"));
        event.setPicnicDateTime(resultSet.getTimestamp("picnic_date_time").toLocalDateTime());
        event.setInvoiceId(resultSet.getInt("invoice_id"));
        event.setCustomerId(resultSet.getInt("customer_id"));
        event.setGuestCount(resultSet.getString("guest_count"));
        event.setEventLocation(resultSet.getString("event_location"));
        event.setEventAddress(resultSet.getString("event_address"));
        event.setEventType(resultSet.getString("event_type"));
        event.setEmployeeId(resultSet.getInt("employee_id"));
        event.setGoogleCalendarId(resultSet.getString("google_calendar_id"));
        event.setStyle(resultSet.getString("style"));
        event.setCustomPalette(resultSet.getString("custom_palette"));

        Customer customer = new Customer();
        customer.setID(resultSet.getInt("customer_table_id"));
        customer.setName(resultSet.getString("name"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setSource(resultSet.getString("source"));
        customer.setEmail(resultSet.getString("email"));
        event.setCustomer(customer);

        Invoice invoice = new Invoice();
        invoice.setID(resultSet.getInt("invoice_table_id"));
        invoice.setSubtotal(resultSet.getFloat("subtotal"));
        invoice.setTaxRate(resultSet.getFloat("tax_rate"));
        invoice.setIsPaid(resultSet.getBoolean("is_paid"));
        invoice.setTotal(resultSet.getFloat("total"));
        invoice.setSquareInvoiceID(resultSet.getString("square_invoice_id"));
        invoice.setDiscountPercentage(resultSet.getDouble("discount_percentage"));

        event.setInvoice(invoice);
    }

    public static int getChecksum() {
        return getChecksum("event");
    }
}