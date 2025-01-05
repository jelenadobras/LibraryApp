package com.library.utilities;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DB_Util {


    // declaring at class level so all methods can access
    private static Connection con;
    private static Statement stm;
    private static ResultSet rs;
    private static ResultSetMetaData rsmd;


    /**
     * Create Connection by jdbc url and username , password provided
     *
     * @param url      jdbc url for any database
     * @param username username for database
     * @param password password for database
     */
    public static void createConnection(String url, String username, String password) {


        try {
            con = DriverManager.getConnection(url, username, password);
            System.out.println("CONNECTION SUCCESSFUL");
        } catch (Exception e) {
            System.out.println("CONNECTION HAS FAILED " + e.getMessage());
        }

    }

    /**
     * Create connection method , just checking one connection successful or not
     */
    public static void createConnection() {

        String url = ConfigurationReader.getProperty("library2.db.url");

        String username = ConfigurationReader.getProperty("library2.db.username");
        // String username = System.getenv("DB_USERNAME");

        String password = ConfigurationReader.getProperty("library2.db.password");
        //  String password = System.getenv("DB_PASSWORD");

        createConnection(url, username, password);

    }


    public static void destroy() {
        // WE HAVE TO CHECK IF WE HAVE THE VALID OBJECT FIRST BEFORE CLOSING THE RESOURCE
        // BECAUSE WE CAN NOT TAKE ACTION ON AN OBJECT THAT DOES NOT EXIST
        try {
            if (rs != null) rs.close();
            if (stm != null) stm.close();
            if (con != null) con.close();
        } catch (Exception e) {
            System.out.println("ERROR OCCURRED WHILE CLOSING RESOURCES " + e.getMessage());
        }

    }


    public static ResultSet runQuery(String sql) {

        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery(sql); // setting the value of ResultSet object
            rsmd = rs.getMetaData();  // setting the value of ResultSetMetaData for reuse
        } catch (Exception e) {
            System.out.println("ERROR OCCURRED WHILE RUNNING QUERY " + e.getMessage());
        }

        return rs;

    }


    public static Map<String, String> getRowMap(int rowNum) {

        Map<String, String> rowMap = new LinkedHashMap<>();
        int columnCount = getColumnCount();

        try {

            rs.absolute(rowNum);

            for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                String columnName = rsmd.getColumnName(colIndex);
                String cellValue = rs.getString(colIndex);
                rowMap.put(columnName, cellValue);
            }

        } catch (Exception e) {
            System.out.println("ERROR OCCURRED WHILE getRowMap " + e.getMessage());
        } finally {
            resetCursor();
        }
        return rowMap;
    }

    public static int getColumnCount() {

        int columnCount = 0;

        try {
            columnCount = rsmd.getColumnCount();

        } catch (Exception e) {
            System.out.println("ERROR OCCURRED WHILE GETTING COLUMN COUNT " + e.getMessage());
        }

        return columnCount;

    }

    private static void resetCursor() {

        try {
            rs.beforeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

