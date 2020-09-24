package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Employee extends QueryObject {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private boolean active;

    public Employee(int id, String firstName, String lastName, String email, String address, String phoneNumber) {
        setID(id);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setAddress(address);
        setPhoneNumber(phoneNumber);
        setActive(true);
    }

    public Employee(){
    }

    public boolean edit(){

        statement = "UPDATE employee " +
                "SET " +
                "first_name = '" + this.getFirstName() +  "', " +
                "last_name = '" + this.getLastName() +  "', " +
                "email = '" + this.getEmail() +  "', " +
                "address = '" + this.getAddress() +  "', " +
                "phone_number = '" + this.getPhoneNumber() +  "', " +
                "active = " + this.getActiveBit() + " " +
                "WHERE id = " + this.getID();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO employee (first_name, last_name, email, address, phone_number) VALUES ('" +
                this.getFirstName() + "', '" + this.getLastName() +  "', '" + this.getEmail() + "', '" + this.getAddress() + "', '" + this.getPhoneNumber() +
                "')";

        return executeUpdate(statement);
    }

    /* Disabled because we don't want to delete employees given that they might be referenced in other tables.
     private boolean delete(){
        statement =
                "DELETE FROM employee WHERE id = " +
                        this.getID();
        return executeUpdate(statement);
    }
    */

    public static ObservableList<Employee> findAll(){
        List<Employee> employees = new ArrayList<>();
        try {
            statement = "SELECT * FROM employee ORDER BY active DESC, first_name, last_name";
            executeQuery(statement);
            while(resultSet.next()) {
                Employee employee = new Employee();
                setEmployeeFromQuery(employee);
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(employees);
    }

    public static Employee findByID(int id){
        Employee employee = new Employee();
        try {
            statement = "SELECT * FROM employee WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(employee);
            } else {
                employee = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return employee;
    }

    public int getID() {
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getActive(){ return active; }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public int getActiveBit(){
        return active ? 1 : 0;
    }

    public void setActive(boolean active){ this.active = active; }

    public String toString(){
        return this.getFirstName() + " " + this.getLastName();
    }

    public void getIDFromDB(){
        setID(getLastID("employee"));
    }

    private static void setEmployeeFromQuery(Employee employee) throws SQLException {
        employee.setID(resultSet.getInt("id"));
        employee.setFirstName(resultSet.getString("first_name"));
        employee.setLastName(resultSet.getString("last_name"));
        employee.setEmail(resultSet.getString("email"));
        employee.setAddress(resultSet.getString("address"));
        employee.setPhoneNumber(resultSet.getString("phone_number"));
        employee.setActive(resultSet.getBoolean("active"));
    }

    public static int getChecksum(){
        return getChecksum("employee");
    }
}
