package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 *
 * @author jabar
 */
public class SQLConnector {

    private Connection con;
    /*private final String dbName = "csci5520";
    private final String userName = "jabarney7";
    private final String password = "password";*/
    private final String hostname = "127.0.0.1";
    private final String dbName = "barney";
    private final String userName = "barney";
    private final String password = "tiger";
    //private final String hostname = "35.185.94.191";*/
    private final String port = "3306";
    private String error;

    public SQLConnector() {
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbcUrl = "jdbc:mysql://" + hostname  + "/" + dbName;
            con = DriverManager.getConnection(jdbcUrl,userName,password);
            error = "";
        } catch (ClassNotFoundException | SQLException e) {
            error = e.getLocalizedMessage();
            System.out.println("Connection Error: " + error);
        }
        return con;
    }

    public void createDatabase() {
        error = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // SQL command to create a database in MySQL.
            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "?user="
                    + userName + "&password=" + password;

            con = DriverManager.getConnection(jdbcUrl);
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.execute();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            error = e.getLocalizedMessage();
        }
    }

    public Boolean createTable(HashMap<String, String> info, String table, String key) {
        error = "";
        con = this.getConnection();
        if (!error.isEmpty()) {
            System.out.println("failed");
            System.out.println("createTable after Connection: " + error);
        } else {
            try {
                String sqlCreate = "CREATE TABLE IF NOT EXISTS " + table + "  (";
                String cols = "";
                for (Map.Entry<String, String> entry : info.entrySet()) {
                    if (!cols.equals("")) {
                        cols += ",";
                    }
                    cols += entry.getKey() + " " + entry.getValue();
                }
                if (cols.equals("")) {
                    error = "No column data provided";
                    return false;
                }
                sqlCreate += cols;
                if (!key.equals("")) {
                    sqlCreate += ", PRIMARY KEY(" + key + ")";
                }
                sqlCreate += ")";
                System.out.println("create Table SQL: " + sqlCreate);
                PreparedStatement stmt = con.prepareStatement(sqlCreate);
                stmt.execute();
                stmt.close();
                con.close();
                return true;
            } catch (SQLException e) {
                error = e.getLocalizedMessage();
                System.out.println();
            }
        }
        return false;
    }

    public String getError() {
        return error;
    }

    public Boolean exists(String id, String table) {
        Statement statement;
        con = this.getConnection();

        try {
            statement = con.createStatement();
            PreparedStatement stmt = con.prepareStatement("select id from " + table + " where id = '" + id + "'");

            ResultSet resultSet = stmt.executeQuery();
            return (resultSet.next());
        } catch (SQLException ex) {
        }
        return false;
    }

    public Boolean tableExists(String table) {
        error = "";
        con = this.getConnection();
        if (!error.isEmpty()) {
            System.out.println(error);
        } else {
            try {
                PreparedStatement stmt = con.prepareStatement("SELECT 1 FROM " + table + " LIMIT 1");
                ResultSet resultSet = stmt.executeQuery();
                return (resultSet.next());
            } catch (SQLException ex) {
                System.out.println("Table Exists Error: " + ex.getLocalizedMessage());
            }
        }
        return false;
    }

    public Map<String, String> loadItem(String table, HashMap<String, String> criteria) {
        con = this.getConnection();
        String where = "";
        List<String> sets = new LinkedList<>();
        sets.add(0, "");
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (!where.equals("")) {
                where += " and ";
            }
            where += entry.getKey() + " = ?";
            sets.add(entry.getValue());
        }
        try (PreparedStatement stmt = con.prepareStatement("select * from " + table + " where " + where)) {
            for (int i = 1; i < sets.size(); i++) {
                stmt.setString(i, sets.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            HashMap form = new HashMap(columns);
            while (rs.next()) {
                for (int i = 1; i <= columns; ++i) {
                    form.put(md.getColumnName(i), rs.getObject(i).toString());
                }
            }
            con.close();
            return form;
        } catch (SQLException e) {
        }
        return new HashMap<>();
    }

    public Map<String, String> getRandomItem(String table) {
        con = this.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("select * from " + table + " ORDER BY RAND() LIMIT 0,1")) {
            System.out.println(stmt.toString());
            ResultSet rs = stmt.executeQuery();
            System.out.println(rs.toString());
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            HashMap form = new HashMap(columns);
            while (rs.next()) {
                for (int i = 1; i <= columns; ++i) {
                    form.put(md.getColumnName(i), rs.getObject(i).toString());
                }
            }
            con.close();
            return form;
        } catch (SQLException e) {
            
        }
        return new HashMap<>();
    }

    public TreeMap<Integer, String> getQuestionsList() {
        con = this.getConnection();
        String SQL_STMT = "SELECT chapterNo as chapters, "
                + "group_concat(questionNo) as questions "
                + "FROM intro11equiz "
                + "group by chapterNo";
        try (PreparedStatement stmt = con.prepareStatement(SQL_STMT)) {
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            TreeMap<Integer, String> form = new TreeMap<>();
            while (rs.next()) {
                form.put((Integer) rs.getObject(1), rs.getObject(2).toString());
            }
            con.close();
            System.out.println(form.toString());
            return form;
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return new TreeMap<>();
    }

}
