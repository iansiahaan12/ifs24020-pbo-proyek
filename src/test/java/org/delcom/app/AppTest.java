package org.delcom.app;

import org.delcom.app.configs.DatabaseConnection;
import org.delcom.app.controllers.KoleksiController;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class AppTest {

    // Generate User ID Dummy untuk keperluan tes
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final KoleksiController controller = new KoleksiController();

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    MULAI TESTING MANUAL BACKEND PBO      ");
        System.out.println("==========================================\n");

        try {
            // 1. TEST KONEKSI DATABASE
            testDatabaseConnection();

            // 2. PERSIAPAN DATA (Buat User Dummy agar tidak error Foreign Key)
            buatUserDummy();

            // 3. TEST TAMBAH DATA (CREATE)
            testTambahData();

            // 4. TEST BACA DATA (READ)
            String idItem = testBacaData();

            if (idItem != null) {
                // 5. TEST UPDATE DATA (UPDATE)
                testUpdateData(idItem);

                // 6. TEST STATISTIK CHART (READ STATS)
                testStatistik();

                // 7. TEST HAPUS DATA (DELETE)
                testHapusData(idItem);
            } else {
                System.out.println("⚠️ Skip tes Update/Hapus karena data gagal dibuat.");
            }

        } catch (Exception e) {
            System.err.println("\n[FATAL ERROR] Terjadi kesalahan fatal:");
            e.printStackTrace();
        } finally {
            // Bersihkan data sampah (User Dummy)
            hapusUserDummy();
            System.out.println("\n==========================================");
            System.out.println("          TESTING SELESAI                 ");
            System.out.println("==========================================");
        }
    }

    // --- STEP 1: TEST KONEKSI ---
    private static void testDatabaseConnection() {
        System.out.print("[TEST 1] Cek Koneksi Database... ");
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("BERHASIL ✅");
        } else {
            System.out.println("GAGAL ❌ (Cek DatabaseConnection.java / XAMPP)");
            System.exit(1);
        }
    }

    // --- STEP 2: SETUP DUMMY USER ---
    private static void buatUserDummy() {
        System.out.print("[SETUP] Membuat User Dummy... ");
        String sql = "INSERT INTO users (id, name, email, password, created_at, updated_at) VALUES (?, 'Tester', 'test@local', '123', NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, TEST_USER_ID.toString());
            stmt.executeUpdate();
            System.out.println("OK ✅");
        } catch (SQLException e) {
            System.out.println("User sudah ada / Gagal (Lanjut) ⚠️");
        }
    }

    // --- STEP 3: TEST CREATE ---
    private static void testTambahData() {
        System.out.println("\n[TEST 2] Menambah Data (SimpanData)...");
        try {
            // Data Dummy
            String nama = "Gundam Barbatos";
            String kategori = "Action Figure";
            String kondisi = "Baru (Mint)";
            String harga = "1500000";
            String deskripsi = "Tes input data dengan deskripsi lengkap.";
            
            // Parameter idStr NULL menandakan ini data BARU
            controller.simpanData(null, TEST_USER_ID, nama, kategori, kondisi, harga, deskripsi, null);
            
            System.out.println("   -> Eksekusi Controller: SUKSES ✅");
        } catch (Exception e) {
            System.out.println("   -> GAGAL ❌: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- STEP 4: TEST READ ---
    private static String testBacaData() {
        System.out.println("\n[TEST 3] Membaca Data dari Tabel...");
        DefaultTableModel model = controller.getTableData(TEST_USER_ID);
        
        int rowCount = model.getRowCount();
        System.out.println("   -> Jumlah data ditemukan: " + rowCount);

        if (rowCount > 0) {
            // Mengambil ID (Kolom 0) dan Nama (Kolom 1) dari baris pertama
            // ID di tabel tersimpan sebagai UUID Object
            Object idObj = model.getValueAt(0, 0); 
            String nama = (String) model.getValueAt(0, 1);
            String harga = model.getValueAt(0, 4).toString();

            System.out.println("   -> Data Terakhir: " + nama + " | Rp " + harga);
            System.out.println("   -> Read Data BERHASIL ✅");
            
            return idObj.toString(); // Kembalikan ID sebagai String untuk tes selanjutnya
        } else {
            System.out.println("   -> GAGAL: Data kosong ❌");
            return null;
        }
    }

    // --- STEP 5: TEST UPDATE ---
    private static void testUpdateData(String idItemStr) {
        System.out.println("\n[TEST 4] Mengupdate Data (Edit Harga & Nama)...");
        try {
            String namaBaru = "Gundam Barbatos Lupus Rex"; // Ganti nama
            String hargaBaru = "2000000"; // Ganti harga
            String descBaru = "Sudah diupdate";

            // Parameter idStr DIISI menandakan ini UPDATE
            controller.simpanData(idItemStr, TEST_USER_ID, namaBaru, "Action Figure", "Bekas", hargaBaru, descBaru, null);
            
            // Verifikasi Perubahan
            DefaultTableModel model = controller.getTableData(TEST_USER_ID);
            String namaDiTabel = (String) model.getValueAt(0, 1);
            
            if (namaDiTabel.equals(namaBaru)) {
                System.out.println("   -> Nama berubah jadi: '" + namaDiTabel + "' ✅");
            } else {
                System.out.println("   -> Nama TIDAK berubah ❌");
            }
        } catch (Exception e) {
            System.out.println("   -> Error Update: " + e.getMessage());
        }
    }

    // --- STEP 6: TEST CHART ---
    private static void testStatistik() {
        System.out.println("\n[TEST 5] Mengambil Data Statistik (Chart)...");
        Map<String, Double> stats = controller.getStats(TEST_USER_ID);
        
        if (stats.containsKey("Action Figure")) {
            System.out.println("   -> Kategori 'Action Figure' ditemukan.");
            System.out.println("   -> Total Aset: Rp " + stats.get("Action Figure"));
            System.out.println("   -> Logic Statistik BERHASIL ✅");
        } else {
            System.out.println("   -> Gagal / Data kategori tidak ditemukan ❌");
        }
    }

    // --- STEP 7: TEST DELETE ---
    private static void testHapusData(String idItemStr) {
        System.out.println("\n[TEST 6] Menghapus Data...");
        controller.hapusData(idItemStr);
        
        // Cek apakah data benar-benar hilang
        DefaultTableModel model = controller.getTableData(TEST_USER_ID);
        if (model.getRowCount() == 0) {
            System.out.println("   -> Tabel kosong kembali. Hapus BERHASIL ✅");
        } else {
            System.out.println("   -> Data masih ada (" + model.getRowCount() + " rows). Hapus GAGAL ❌");
        }
    }

    // --- CLEANUP ---
    private static void hapusUserDummy() {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, TEST_USER_ID.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Ignore
        }
    }
}