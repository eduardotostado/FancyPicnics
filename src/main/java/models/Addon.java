package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Addon extends QueryObject {
    private double price;
    private String name;
    private String typeCode;
    private double supplierCost;

    public Addon(double price, String name, String typeCode, double supplierCost) {
        this.price = price;
        this.name = name;
        this.typeCode = typeCode;
        this.supplierCost = supplierCost;
    }

    public Addon(){
    }

    public boolean edit(){

        statement = "UPDATE addon " +
                "SET " +
                "price = " + this.getPrice() +  ", " +
                "supplier_cost = " + this.getSupplierCost() +  " " +
                "WHERE name = " + (this.getName() == null ? this.getName() : "'" + this.getName().replaceAll("'","''") + "'") + " " +
                "AND type_code = " + (this.getTypeCode() == null ? this.getTypeCode() : "'" + this.getTypeCode().replaceAll("'","''") + "'");


        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO addon (price, name, type_code, supplier_cost) VALUES (" +
                this.getPrice() + ", " +
                (this.getName() == null ? this.getName() : "'" + this.getName().replaceAll("'","''") + "'") + ", " +
                (this.getTypeCode() == null ? this.getTypeCode() : "'" + this.getTypeCode().replaceAll("'","''") + "'") + ", " +
                this.getSupplierCost() +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<Addon> findAll(){
        List<Addon> addons = new ArrayList<>();
        try {
            statement = "SELECT * FROM addon ORDER BY name ASC";
            executeQuery(statement);
            while(resultSet.next()) {
                Addon addon = new Addon();
                setAddonFromQuery(addon);
                addons.add(addon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(addons);
    }

    public static Addon findByName(String name){
        Addon addon = new Addon();
        try {
            statement = "SELECT * FROM addon WHERE name = '" + name.replaceAll("'","''") + "'";
            executeQuery(statement);
            if (resultSet.next()) {
                setAddonFromQuery(addon);
            } else {
                addon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return addon;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public double getSupplierCost() {
        return supplierCost;
    }

    public void setSupplierCost(double supplierCost) {
        this.supplierCost = supplierCost;
    }

    public boolean exists() {
        return (findByName(this.name) != null);
    }

    private static void setAddonFromQuery(Addon addon) throws SQLException {
        addon.setPrice(resultSet.getDouble("price"));
        addon.setName(resultSet.getString("name"));
        addon.setTypeCode(resultSet.getString("type_code"));
        addon.setSupplierCost(resultSet.getDouble("supplier_cost"));
    }

    public static int getChecksum(){
        return getChecksum("addon");
    }
}
