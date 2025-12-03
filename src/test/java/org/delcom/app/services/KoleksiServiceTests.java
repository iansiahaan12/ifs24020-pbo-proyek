package org.delcom.app.services;

import org.delcom.app.configs.DatabaseConnection;
import org.delcom.app.entities.KoleksiItem;
import org.delcom.app.services.KoleksiService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

public class KoleksiServiceTests {

    private static final KoleksiService service = new KoleksiService();
    private static final UUID TEST_USER_ID = UUID.randomUUID();

    public static void main(String[] args) {
        System.out.println("=== TEST: KoleksiService ===");
        
        setupDummyUser(); // Wajib bikin user dulu biar ga error
        
        try {
            testSimpanItem();
            testAmbilSemuaItem();
            // Clean up data test akan dilakukan manual atau biarkan saja untuk dicek
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
             cleanupDummyUser();
        }
    }

    public static void testSimpanItem() {
        System.out.print("Testing saveItem (Insert)... ");
        
        KoleksiItem item = new KoleksiItem();
        item.setUserId(TEST_USER_ID);
        item.setNamaItem("Service Test Item");
        item.setKategori("Testing");
        item.setKondisi("Baru");
        item.setHargaTaksiran(12345.0);
        item.setDeskripsi("Dibuat oleh Service Test");

        // Simpan (null file gambar)
        service.saveItem(item, null, false);
        
        System.out.println("SUKSES [v]");
    }

    public static void testAmbilSemuaItem() {
        System.out.print("Testing getAllItems... ");
        
        List<KoleksiItem> list = service.getAllItems(TEST_USER_ID);
        
        if (list.size() > 0 && list.get(0).getNamaItem().equals("Service Test Item")) {
            System.out.println("SUKSES [v] (Data ditemukan: " + list.size() + ")");
        } else {
            System.out.println("GAGAL [x] (List kosong atau data salah)");
        }
    }

    // --- Helpers untuk Data Dummy ---
    private static void setupDummyUser() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (id, name, email, password, created_at, updated_at) VALUES (?, 'SvcTest', 'svc@test', '1', NOW(), NOW())")) {
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