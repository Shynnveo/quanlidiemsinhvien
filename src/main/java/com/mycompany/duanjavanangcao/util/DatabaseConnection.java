package com.mycompany.duanjavanangcao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://127.0.0.1:1433;"
            + "databaseName=duanjavanangcao;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try {
            System.out.println("Đang kết nối đến SQL Server...");
            try (Connection connection = getConnection()) {
                System.out.println("Kết nối SQL Server thành công!");
                System.out.println("Database hiện tại: " + connection.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("Kết nối thất bại!");
            System.err.println("Mã lỗi (Error Code): " + e.getErrorCode());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Nguyên nhân chi tiết: " + e.getMessage());
        } catch (Exception ex) {
            System.err.println("Lỗi khởi tạo hoặc thiếu thư viện: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}