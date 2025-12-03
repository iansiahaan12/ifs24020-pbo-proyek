package org.delcom.app.configs;

import org.delcom.app.configs.DatabaseConnection;
import java.sql.Connection;

public class DatabaseConnectionTests {

    public static void main(String[] args) {
        System.out.println("=== TEST: DatabaseConnection ===");
        
        testConnection();
    }

    public static void testConnection() {
        System.out.print("Mencoba terhubung ke database... ");
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("SUKSES [v]");
            System.out.println("Info: Objek Connection berhasil dibuat.");
        } else {
            System.out.println("GAGAL [x]");
            System.err.println("Pastikan XAMPP nyala dan nama database di file Config benar.");
        }
    }
}