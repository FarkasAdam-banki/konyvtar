package com.example.konyvtar;
import java.sql.*;

public class DatabaseConnection {
    private static Connection conn = null;
    private final static String URL = "jdbc:mysql://localhost:3306/konyvtar";
    private final static String USER = "root";
    private final static String PASSWORD = "";

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        }
        return conn;
    }
}
