package org.delcom.app.configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Ganti 'nama_database_kamu' dengan nama database yang kamu buat di phpMyAdmin
    private static final String URL = "jdbc:mysql://localhost:3306/pbo_proyek_db"; 
    private static final String USER = "root"; // Default user XAMPP
    private static final String PASSWORD = ""; // Default password XAMPP (biasanya kosong)

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Register Driver MySQL (Penting untuk memastikan driver terload)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Membuat koneksi
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL tidak ditemukan! Pastikan library mysql-connector sudah ditambahkan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Gagal terhubung ke Database!");
            e.printStackTrace();
        }
        return connection;
    }
}