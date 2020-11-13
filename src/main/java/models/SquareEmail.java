package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SquareEmail extends QueryObject {
    private String id;
    private Date emailDate;
    private boolean isIgnored;

    private String eventName;
    private String eventEmail;
    private String eventPhoneNumber;
    private String eventSource;
    private String eventDate;
    private String eventTime;
    private String eventGuestCount;
    private String eventLocation;
    private String eventAddress;
    private String eventType;
    private String eventStyle;
    private String[] eventAddonsArray;
    private String customPalette;
    private String marqueeLetters;

    public SquareEmail(String id, Date emailDate, boolean isIgnored) {
        setId(id);
        setEmailDate(emailDate);
        setIsIgnored(isIgnored);
    }

    public SquareEmail() {

    }

    public boolean add(){
        statement = "INSERT INTO square_email (id, email_date, is_ignored) VALUES (" +
                (this.getId() == null ? this.getId() : "'" + this.getId().replaceAll("'","''") + "'") + ", " +
                "'" +this.getEmailDateFormatted() + "', " +
                this.getIsIgnoredBit() +
                ")";
        return executeUpdate(statement);
    }

    public boolean update(){
        statement = "UPDATE square_email SET is_ignored = " + this.getIsIgnoredBit() +
                " WHERE id = " + (this.getId() == null ? this.getId() : "'" + this.getId().replaceAll("'","''") + "'") + ";";
        return executeUpdate(statement);
    }

    public boolean delete(){
        statement =
                "DELETE FROM square_email WHERE id = " +
                        (this.getId() == null ? this.getId() : "'" + this.getId().replaceAll("'","''") + "'") + ";";
        return executeUpdate(statement);
    }

    public static ObservableList<SquareEmail> findAll(){
        List<SquareEmail> squareEmails = new ArrayList<>();
        try {
            statement = "SELECT id, email_date, is_ignored FROM square_email";
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
            statement = "SELECT id, email_date, is_ignored FROM square_email WHERE id = " + (this.getId() == null ? this.getId() : "'" + this.getId().replaceAll("'","''") + "'") + ";";
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

    public static void resetIgnoredEmails(String begDate, String endDate){
            statement = "DELETE FROM square_email WHERE is_ignored = 1 AND email_date BETWEEN '" + begDate + "' AND '" + endDate +"'";
            executeUpdate(statement);
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

    public String getEmailDateFormatted(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(this.emailDate);
    }

    public boolean getIsIgnored(){
        return this.isIgnored;
    }

    public int getIsIgnoredBit(){
        return isIgnored ? 1 : 0;
    }

    public void setIsIgnored(boolean isIgnored){
        this.isIgnored = isIgnored;
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

    public String getEventStyle() {
        return eventStyle;
    }

    public void setEventStyle(String eventStyle) {
        this.eventStyle = eventStyle;
    }

    public String[] getEventAddonsArray() {
        return eventAddonsArray;
    }

    public void setEventAddonsArray(String[] eventAddonsArray) {
        this.eventAddonsArray = eventAddonsArray;
    }

    public String getCustomPalette() {
        return customPalette;
    }

    public void setCustomPalette(String customPalette) {
        this.customPalette = customPalette;
    }

    public String getMarqueeLetters() {
        return marqueeLetters;
    }

    public void setMarqueeLetters(String marqueeLetters) {
        this.marqueeLetters = marqueeLetters;
    }

    public boolean exists() {
        return (findByID(id) != null);
    }

    private static void setUserFromQuery(SquareEmail squareEmail) throws SQLException {
        squareEmail.setId(resultSet.getString("id"));
        squareEmail.setEmailDate(resultSet.getDate("email_date"));
        squareEmail.setIsIgnored(resultSet.getBoolean("is_ignored"));
    }

    public static int getChecksum(){
        return getChecksum("square_email");
    }
}
