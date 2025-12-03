package org.delcom.app.controllers;

import org.delcom.app.configs.DatabaseConnection;
import org.delcom.app.controllers.KoleksiController;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.UUID;

public class KoleksiControllerTests {

    private static final KoleksiController controller = new KoleksiController();
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    public static void main(String[] args) {
        System.out.println("=== TEST: KoleksiController ===");
        
        setupDummyUser();

        try {
            testSimpanDataViaController();
            testFormatTabel();
            testStatistikChart();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanupDummyUser();
        }
    }

    public static void testSimpanDataViaController() {
        System.out.print("Testing simpanData (Parsing Input)... ");
        
        // Simulasi input form (semua String)
        controller.simpanData(null, TEST_USER_ID, "Controller Item", "Elektronik", "Rusak", "500000", "Desc via Ctrl", null);
        
        System.out.println("SUKSES [v]");
    }

    public static void testFormatTabel() {
        System.out.print("Testing getTableData (TableModel)... ");
        
        DefaultTableModel model = controller.getTableData(TEST_USER_ID);
        
        // Cek Kolom
        if (model.getColumnCount() == 7) { // ID, Nama, Kat, Kondisi, Harga, Path, Desc
            System.out.print("Kolom OK... ");
        } else {
            System.out.print("Kolom GAGAL ("+model.getColumnCount()+")... ");
        }

        // Cek Row
        if (model.getRowCount() > 0) {
            String nama = (String) model.getValueAt(0, 1); // Kolom 1 = Nama
            if (nama.equals("Controller Item")) {
                System.out.println("Data Row OK [v]");
            } else {
                System.out.println("Data Row Salah [x]");
            }
        } else {
            System.out.println("Tidak ada baris [x]");
        }
    }

    public static void testStatistikChart() {
        System.out.print("Testing getStats (Chart Data)... ");
        
        Map<String, Double> stats = controller.getStats(TEST_USER_ID);
        
        if (stats.containsKey("Elektronik") && stats.get("Elektronik") == 500000.0) {
            System.out.println("SUKSES [v]");
        } else {
            System.out.println("GAGAL [x] - " + stats);
        }
    }

    // --- Helpers ---
    private static void setupDummyUser() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (id, name, email, password, created_at, updated_at) VALUES (?, 'CtrlTest', 'ctrl@test', '1', NOW(), NOW())")) {
            stmt.setString(1, TEST_USER_ID.toString());
            stmt.executeUpdate();
        } catch (Exception e) {}
    }

    private static void cleanupDummyUser() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.prepareStatement("DELETE FROM koleksi_items WHERE user_id = '" + TEST_USER_ID + "'").executeUpdate();
            conn.prepareStatement("DELETE FROM users WHERE id = '" + TEST_USER_ID + "'").executeUpdate();
        } catch (Exception e) {}
    }
}