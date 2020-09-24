package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventType extends QueryObject {

    private int id;
    private String description;

    public EventType(int id, String description) {
        setID(id);
        setDescription(description);
    }

    public EventType(){
    }

    public boolean edit(){

        statement = "UPDATE event_type " +
                "SET " +
                "description = '" + this.getDescription() +  "' " +
                "WHERE id = " + this.getID();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO event_type (description) VALUES ('" +
                this.getDescription() +
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

    public static ObservableList<EventType> findAll(){
        List<EventType> eventTypes = new ArrayList<>();
        try {
            statement = "SELECT * FROM event_type ORDER BY name ASC";
            executeQuery(statement);
            while(resultSet.next()) {
                EventType eventType = new EventType();
                setEmployeeFromQuery(eventType);
                eventTypes.add(eventType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(eventTypes);
    }

    public static EventType findByID(int id){
        EventType eventType = new EventType();
        try {
            statement = "SELECT * FROM event_type WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(eventType);
            } else {
                eventType = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return eventType;
    }

    public static EventType findByDescription(String description){
        EventType eventType = new EventType();
        try {
            statement = "SELECT * FROM event_type WHERE description = " + description;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(eventType);
            } else {
                eventType = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return eventType;
    }

    public int getID() {
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB(){
        setID(getLastID("event_type"));
    }

    private static void setEmployeeFromQuery(EventType eventType) throws SQLException {
        eventType.setID(resultSet.getInt("id"));
        eventType.setDescription(resultSet.getString("description"));
    }

    public static int getChecksum(){
        return getChecksum("event_type");
    }
}
