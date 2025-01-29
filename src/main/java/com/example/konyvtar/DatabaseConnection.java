package com.example.konyvtar;
import java.sql.*;

public class DatabaseConnection {
    private static Connection conn = null;
    private final static String URL = "jdbc:mysql://localhost:3306/konyvtar";
    private final static String USER = "root";
    private final static String PASSWORD = "";

    private DatabaseConnection() {}

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

    public static PreparedStatement getPreparedStatement(String sql) {
        getConnection();
        try {
            return conn.prepareStatement(sql);
        }catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public static ResultSet executeQuery(PreparedStatement statement) {
        getConnection();
        try {
            return statement.executeQuery();
        }catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public static int executeUpdate(PreparedStatement statement) {
        getConnection();
        try {
            return statement.executeUpdate();
        }catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public static ResultSet executeQuery(String sql) {
        getConnection();
        try {
            Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        }catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public static int executeUpdate(String sql) {
        getConnection();
        try {
            Statement statement = conn.createStatement();
            int i = statement.executeUpdate(sql);
            statement.close();
            return i;
        }catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }
}
