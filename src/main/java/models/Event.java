package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Event extends QueryObject {

    private int id;
    private String squareEmailId;
    private String picnicDateTime;
    private int invoiceId;
    private int customerId;
    private int guestCount;
    private String eventAddress;
    private int eventTypeId;
    private int addonCount;
    private int styleCount;

    public Event(int id, String squareEmailId, String picnicDateTime, int invoiceId, int customerId, int guestCount, String eventAddress, int eventTypeId, int addonCount, int styleCount) {
        setId(id);
        setSquareEmailId(squareEmailId);
        setPicnicDateTime(picnicDateTime);
        setInvoiceId(invoiceId);
        setCustomerId(customerId);
        setGuestCount(guestCount);
        setEventAddress(eventAddress);
        setEventTypeId(eventTypeId);
        setAddonCount(addonCount);
        setStyleCount(styleCount);
    }

    public Event(){
    }

    public boolean edit(){

        statement = "UPDATE event " +
                "SET " +
                "square_email_id = '" + this.getSquareEmailId() +  "' " +
                "picnic_date_time = '" + this.getPicnicDateTime() +  "' " +
                "invoice_id = " + this.getInvoiceId() +  " " +
                "customer_id = " + this.getCustomerId() +  " " +
                "guest_count = " + this.getGuestCount() +  " " +
                "event_address = '" + this.getEventAddress() +  "' " +
                "event_type_id = " + this.getEventTypeId() +  " " +
                "addon_count = " + this.getAddonCount() +  " " +
                "style_count = " + this.getStyleCount() +  " " +
                "WHERE id = " + this.getId();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO event (square_email_id, picnic_date_time, invoice_id, customer_id, guest_count, event_address, event_type_id, addon_count, style_count) VALUES ('" +
                this.getSquareEmailId() + ", '" + this.getPicnicDateTime() + "', " + this.getInvoiceId() + ", " + this.getCustomerId() + ", " + this.getGuestCount() + ", '" + this.getEventAddress() + "', " + this.getEventTypeId() + ", " + this.getAddonCount() + ", " + this.getStyleCount() +
                "')";

        return executeUpdate(statement);
    }

    /* Disabled because we don't want to delete customers given that they might be referenced in other tables.
     private boolean delete(){
        statement =
                "DELETE FROM employee WHERE id = " +
                        this.getID();
        return executeUpdate(statement);
    }
    */

    public static ObservableList<Event> findAll(){
        List<Event> events = new ArrayList<>();
        try {
            statement = "SELECT * FROM event";
            executeQuery(statement);
            while(resultSet.next()) {
                Event event = new Event();
                setEmployeeFromQuery(event);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(events);
    }

    public static Event findByID(int id){
        Event event = new Event();
        try {
            statement = "SELECT * FROM event WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(event);
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

    public String getPicnicDateTime() {
        return picnicDateTime;
    }

    public void setPicnicDateTime(String picnicDateTime) {
        this.picnicDateTime = picnicDateTime;
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

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public int getAddonCount() {
        return addonCount;
    }

    public void setAddonCount(int addonCount) {
        this.addonCount = addonCount;
    }

    public int getStyleCount() {
        return styleCount;
    }

    public void setStyleCount(int styleCount) {
        this.styleCount = styleCount;
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB(){
        setId(getLastID("event"));
    }

    private static void setEmployeeFromQuery(Event event) throws SQLException {
        event.setId(resultSet.getInt("id"));
        event.setSquareEmailId(resultSet.getString("square_email_id"));
        event.setPicnicDateTime(resultSet.getString("picnic_date_time"));
        event.setInvoiceId(resultSet.getInt("invoice_id"));
        event.setCustomerId(resultSet.getInt("customer_id"));
        event.setGuestCount(resultSet.getInt("guest_count"));
        event.setEventAddress(resultSet.getString("event_address"));
        event.setEventTypeId(resultSet.getInt("event_type_id"));
        event.setAddonCount(resultSet.getInt("addon_count"));
        event.setStyleCount(resultSet.getInt("style_count"));
    }

    public static int getChecksum(){
        return getChecksum("event");
    }
}