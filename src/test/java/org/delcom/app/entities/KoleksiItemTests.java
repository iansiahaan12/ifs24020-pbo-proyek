package org.delcom.app.entities;

import org.delcom.app.entities.KoleksiItem;
import java.time.LocalDateTime;
import java.util.UUID;

public class KoleksiItemTests {

    public static void main(String[] args) {
        System.out.println("=== TEST: KoleksiItem (Entity) ===");
        
        testGetterSetter();
        testConstructor();
    }

    public static void testGetterSetter() {
        System.out.print("Testing Getter & Setter... ");
        
        KoleksiItem item = new KoleksiItem();
        UUID id = UUID.randomUUID();
        String nama = "Hotwheels Rare";
        
        item.setId(id);
        item.setNamaItem(nama);
        item.setHargaTaksiran(50000.0);

        if (item.getId().equals(id) && item.getNamaItem().equals(nama) && item.getHargaTaksiran() == 50000.0) {
            System.out.println("SUKSES [v]");
        } else {
            System.out.println("GAGAL [x]");
        }
    }

    public static void testConstructor() {
        System.out.print("Testing Constructor Lengkap... ");
        
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        KoleksiItem item = new KoleksiItem(id, userId, "Barang A", "Kat A", "Baru", 100.0, "path/img", "deskripsi");

        if (item.getUserId().equals(userId) && item.getKondisi().equals("Baru")) {
            System.out.println("SUKSES [v]");
        } else {
            System.out.println("GAGAL [x]");
        }
    }
}