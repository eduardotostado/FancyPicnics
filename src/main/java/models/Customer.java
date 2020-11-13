package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Customer extends QueryObject {

    private int id;
    private String name;
    private String phone;
    private String source;
    private String email;



    public Customer(int id, String name, String phone, String source, String email) {
        setID(id);
        setName(name);
        setPhone(phone);
        setEmail(email);
        setSource(source);
    }

    public Customer(){
    }

    public boolean edit(){

        statement = "UPDATE customer " +
                "SET " +
                "name = " + (this.getName() == null ? this.getName() : "'" + this.getName().replaceAll("'","''") + "'") + ", " +
                "phone = " + (this.getPhone() == null ? this.getPhone() : "'" + this.getPhone().replaceAll("'","''") + "'") + ", " +
                "source = " + (this.getSource() == null ? this.getSource() : "'" + this.getSource().replaceAll("'","''") + "'") + ", " +
                "email = " + (this.getEmail() == null ? this.getEmail() : "'" + this.getEmail().replaceAll("'","''") + "'") + " " +
                "WHERE id = " + this.getID();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO customer (name, phone, source, email) VALUES (" +
                (this.getName() == null ? this.getName() : "'" + this.getName().replaceAll("'","''") + "'") + ", " +
                (this.getPhone() == null ? this.getPhone() : "'" + this.getPhone().replaceAll("'","''") + "'") + ", " +
                (this.getSource() == null ? this.getSource() : "'" + this.getSource().replaceAll("'","''") + "'") + ", " +
                (this.getEmail() == null ? this.getEmail() : "'" + this.getEmail().replaceAll("'","''") + "'") +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<Customer> findAll(){
        List<Customer> customers = new ArrayList<>();
        try {
            statement = "SELECT * FROM customer ORDER BY name ASC";
            executeQuery(statement);
            while(resultSet.next()) {
                Customer customer = new Customer();
                setEmployeeFromQuery(customer);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(customers);
    }

    public static Customer findByID(int id){
        Customer customer = new Customer();
        try {
            statement = "SELECT * FROM customer WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(customer);
            } else {
                customer = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return customer;
    }

    public static Customer findByEmail(String email){
        Customer customer = new Customer();
        try {
            statement = "SELECT * FROM customer WHERE email = " +
                    (email == null ? null : "'" + email.replaceAll("'","''") + "'") + ";";
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(customer);
            } else {
                customer = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return customer;
    }

    public int getID() {
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB(){
        setID(getLastID("customer"));
    }

    private static void setEmployeeFromQuery(Customer customer) throws SQLException {
        customer.setID(resultSet.getInt("id"));
        customer.setName(resultSet.getString("name"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setSource(resultSet.getString("source"));
        customer.setEmail(resultSet.getString("email"));
    }

    public static int getChecksum(){
        return getChecksum("customer");
    }
}
