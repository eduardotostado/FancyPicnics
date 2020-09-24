package entities;

import java.sql.*;

public abstract class QueryObject {

    protected final static String connectionURL = "jdbc:sqlserver://*******.compute-1.amazonaws.com:1433;databaseName=******;user=*****;password=******";

    protected static String statement;
    protected static Connection conn = null;
    protected static PreparedStatement stmt = null;
    protected static ResultSet resultSet = null;

    protected static boolean executeUpdate(String statement) {
        try {
            conn = DriverManager.getConnection(connectionURL);
            stmt = conn.prepareStatement(statement);
            int result = stmt.executeUpdate();
            if (result > 0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return false;
    }

    protected static void executeQuery(String statement) throws SQLException {
        conn = DriverManager.getConnection(connectionURL);
        stmt = conn.prepareStatement(statement);
        resultSet = stmt.executeQuery();
    }

    protected static void terminateQuery(){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) { /* ignored */}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) { /* ignored */}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) { /* ignored */}
        }
    }

    protected static int getLastID(String tableName){
        int id = -1;
        try {
            statement = "SELECT ident_current('" + tableName + "') as id";
            executeQuery(statement);
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return id;
    }

    protected static int getChecksum(String tableName){
        statement = "SELECT CHECKSUM_AGG(BINARY_CHECKSUM(*)) AS checksum FROM " + tableName + " WITH (NOLOCK)";
        int checksum = 0;
        try {
            executeQuery(statement);
            if (resultSet.next()) {
                checksum = resultSet.getInt("checksum");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            terminateQuery();
        }
        return checksum;
    }
}
