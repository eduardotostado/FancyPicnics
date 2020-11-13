package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Invoice extends QueryObject {

    private int id;
    private Float subtotal;
    private Float taxRate;
    private Float total;
    private boolean isPaid;
    private String squareInvoiceID;
    private Double discountPercentage;

    public Invoice(int id, Float subtotal, Float taxRate, Float total, boolean isPaid, String squareInvoiceID, Double discountPercentage) {
        this.id = id;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.total = total;
        this.isPaid = isPaid;
        this.squareInvoiceID = squareInvoiceID;
        this.discountPercentage = discountPercentage;
    }

    public Invoice(){
    }

    public boolean edit(){

        statement = "UPDATE invoice " +
                "SET " +
                "subtotal = " + this.getSubtotal() +  ", " +
                "tax_rate = " + this.getTaxRate() +  ", " +
                "is_paid = " + this.getIsPaidBit() +  ", " +
                "total = " + this.getTotal() +  ", " +
                "square_invoice_id = " + (this.getSquareInvoiceID() == null ? this.getSquareInvoiceID() : "'" + this.getSquareInvoiceID().replaceAll("'", "''") + "'") + ", " +
                "discount_percentage = " + this.getDiscountPercentage() +  " " +
                "WHERE id = " + this.getID();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO invoice (id, subtotal, tax_rate, is_paid, total, square_invoice_id, discount_percentage) VALUES (" +
                this.getID() + ", " +
                this.getSubtotal() + ", " +
                this.getTaxRate() + ", " +
                this.getIsPaidBit() + ", " +
                this.getTotal() + ", " +
                (this.getSquareInvoiceID() == null ? this.getSquareInvoiceID() : "'" + this.getSquareInvoiceID().replaceAll("'", "''") + "'") + ", " +
                this.getDiscountPercentage() +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<Invoice> findAll(){
        List<Invoice> invoices = new ArrayList<>();
        try {
            statement = "SELECT * FROM invoice ORDER BY id DESC";
            executeQuery(statement);
            while(resultSet.next()) {
                Invoice invoice = new Invoice();
                setFromQuery(invoice);
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(invoices);
    }

    public static Invoice findByID(int id){
        Invoice invoice = new Invoice();
        try {
            statement = "SELECT * FROM invoice WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setFromQuery(invoice);
            } else {
                invoice = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return invoice;
    }

    public int getID() {
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getNewIDFromDB() {
        int result = -1;
        try {
            statement = "SELECT NEXT VALUE FOR invoice_id_sequence";
            executeQuery(statement);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }

        return result;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }

    public Float getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }

    public Float getTotal() {
        return this.total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public int getIsPaidBit(){
        return isPaid ? 1 : 0;
    }

    public String getSquareInvoiceID() {
        return squareInvoiceID;
    }

    public void setSquareInvoiceID(String squareInvoiceID) {
        this.squareInvoiceID = squareInvoiceID;
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB(){
        setID(getLastID("invoice"));
    }

    private static void setFromQuery(Invoice invoice) throws SQLException {
        invoice.setID(resultSet.getInt("id"));
        invoice.setSubtotal(resultSet.getFloat("subtotal"));
        invoice.setTaxRate(resultSet.getFloat("tax_rate"));
        invoice.setIsPaid(resultSet.getBoolean("is_paid"));
        invoice.setTotal(resultSet.getFloat("total"));
        invoice.setSquareInvoiceID(resultSet.getString("square_invoice_id"));
        invoice.setDiscountPercentage(resultSet.getDouble("discount_percentage"));
    }

    public static int getChecksum(){
        return getChecksum("invoice");
    }
}
