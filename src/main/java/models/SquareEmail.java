package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SquareEmail extends QueryObject {
    private String id;
    private Date emailDate;
    private int invoiceId;

    private String eventName;
    private String eventEmail;
    private String eventPhoneNumber;
    private String eventSource;
    private String eventDate;
    private String eventTime;
    private String eventGuestCount;
    private String[] eventLocationArray;
    private String eventAddress;
    private String[] eventTypeArray;
    private String[] eventStyleArray;
    private String[] eventAddonsArray;


    public SquareEmail(String id, Date emailDate, int invoiceId) {
        setId(id);
        setEmailDate(emailDate);
        setInvoiceId(invoiceId);
    }

    public SquareEmail() {

    }

    public boolean add(){
        statement = "INSERT INTO square_email (id, email_date) VALUES ('" +
                this.getId() + "', '" + this.getEmailDate() + "', " +
                ")";
        return executeUpdate(statement);
    }

    public boolean update(){
        statement = "UPDATE square_email SET invoice_id = " + this.getInvoiceId() +
                " WHERE id = " + this.getId();
        return executeUpdate(statement);
    }

    public boolean delete(){
        statement =
                "DELETE FROM square_email WHERE id = '" +
                        this.getId() + "'";
        return executeUpdate(statement);
    }

    public static ObservableList<SquareEmail> findAll(){
        List<SquareEmail> squareEmails = new ArrayList<>();
        try {
            statement = "SELECT id, email_date, invoice_id FROM square_email";
            executeQuery(statement);
            while(resultSet.next()) {
                SquareEmail squareEmail = new SquareEmail();
                setUserFromQuery(squareEmail);
                squareEmails.add(squareEmail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(squareEmails);
    }

    public SquareEmail findByID(String id){
        SquareEmail squareEmail = new SquareEmail();
        try {
            statement = "SELECT id, email_date, invoice_id FROM square_email WHERE id = '" + id + "'";
            executeQuery(statement);
            if (resultSet.next()) {
                setUserFromQuery(squareEmail);
            } else {
                squareEmail = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return squareEmail;
    }

    public static List<String> getAllIdWithinDate(String begDate, String endDate){
        List<String> idList = new ArrayList<>();
        try {
            statement = "SELECT id FROM square_email WHERE email_date BETWEEN '" + begDate + "' AND '" + endDate +"'";
            executeQuery(statement);
            while (resultSet.next()) {
                idList.add(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return idList;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public Date getEmailDate(){
        return this.emailDate;
    }

    public void setEmailDate(Date emailDate){
        this.emailDate = emailDate;
    }

    public int getInvoiceId(){
        return this.invoiceId;
    }

    public void setInvoiceId(int invoiceId){
        this.invoiceId = invoiceId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventEmail() {
        return eventEmail;
    }

    public void setEventEmail(String eventEmail) {
        this.eventEmail = eventEmail;
    }

    public String getEventPhoneNumber() {
        return eventPhoneNumber;
    }

    public void setEventPhoneNumber(String eventPhoneNumber) {
        this.eventPhoneNumber = eventPhoneNumber;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventGuestCount() {
        return eventGuestCount;
    }

    public void setEventGuestCount(String eventGuestCount) {
        this.eventGuestCount = eventGuestCount;
    }

    public String[] getEventLocationArray() {
        return eventLocationArray;
    }

    public void setEventLocationArray(String[] eventLocationArray) {
        this.eventLocationArray = eventLocationArray;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String[] getEventTypeArray() {
        return eventTypeArray;
    }

    public void setEventTypeArray(String[] eventTypeArray) {
        this.eventTypeArray = eventTypeArray;
    }

    public String[] getEventStyleArray() {
        return eventStyleArray;
    }

    public void setEventStyleArray(String[] eventStyleArray) {
        this.eventStyleArray = eventStyleArray;
    }

    public String[] getEventAddonsArray() {
        return eventAddonsArray;
    }

    public void setEventAddonsArray(String[] eventAddonsArray) {
        this.eventAddonsArray = eventAddonsArray;
    }

    public boolean exists() {
        return (findByID(id) != null);
    }

    private static void setUserFromQuery(SquareEmail squareEmail) throws SQLException {
        squareEmail.setId(resultSet.getString("id"));
        squareEmail.setEmailDate(resultSet.getDate("email_date"));
        squareEmail.setInvoiceId(resultSet.getInt("invoice_id"));
    }

    public static int getChecksum(){
        return getChecksum("square_email");
    }
}
