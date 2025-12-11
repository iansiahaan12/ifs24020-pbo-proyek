package org.delcom.app.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PersonalItemTests {

    private UUID userId;
    private LocalDate purchaseDate;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        purchaseDate = LocalDate.now();
    }

    // ==========================================
    // 1. TEST CONSTRUCTOR & GETTER
    // ==========================================
    @Test
    void testConstructorAndGetters() {
        // Arrange
        String itemName = "Macbook Pro";
        String category = "Elektronik";
        Double price = 25000000.0;
        String condition = "Baru";
        String description = "Chip M3";
        String imagePath = "img123.jpg";
        String location = "Meja Kerja";

        // Act
        PersonalItem item = new PersonalItem(
            userId, itemName, category, price, condition, description, imagePath, purchaseDate, location
        );

        // Assert
        assertEquals(userId, item.getUserId());
        assertEquals(itemName, item.getItemName());
        assertEquals(category, item.getCategory());
        assertEquals(price, item.getPrice());
        assertEquals(condition, item.getCondition());
        assertEquals(description, item.getDescription());
        assertEquals(imagePath, item.getImagePath());
        assertEquals(purchaseDate, item.getPurchaseDate());
        assertEquals(location, item.getStorageLocation());
    }

    // ==========================================
    // 2. TEST SETTER
    // ==========================================
    @Test
    void testSetters() {
        // Arrange
        PersonalItem item = new PersonalItem();
        UUID newItemId = UUID.randomUUID();
        
        // Act
        item.setId(newItemId);
        item.setItemName("Kamera Sony");
        item.setPrice(1000000.0);
        item.setStorageLocation("Lemari Kaca");
        item.setPurchaseDate(LocalDate.of(2023, 1, 1));

        // Assert
        assertEquals(newItemId, item.getId());
        assertEquals("Kamera Sony", item.getItemName());
        assertEquals(1000000.0, item.getPrice());
        assertEquals("Lemari Kaca", item.getStorageLocation());
        assertEquals(LocalDate.of(2023, 1, 1), item.getPurchaseDate());
    }

    // ==========================================
    // 3. TEST LIFECYCLE (@PrePersist)
    // ==========================================
    @Test
    void testOnCreate() throws Exception {
        /* 
           Karena method onCreate() bersifat protected, kita menggunakan Reflection 
           untuk memanggilnya dalam Unit Test tanpa perlu menjalankan Database sungguhan.
        */
        
        // Arrange
        PersonalItem item = new PersonalItem();
        assertNull(item.getId());        // Awalnya ID null
        assertNull(item.getCreatedAt()); // Awalnya CreatedAt null

        // Act: Panggil method 'onCreate' via Reflection
        Method onCreateMethod = PersonalItem.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true); // Buka akses protected
        onCreateMethod.invoke(item);

        // Assert
        assertNotNull(item.getId(), "ID harus digenerate otomatis saat onCreate");
        assertNotNull(item.getCreatedAt(), "CreatedAt harus terisi saat onCreate");
        assertNotNull(item.getUpdatedAt(), "UpdatedAt harus terisi saat onCreate");
    }

    // ==========================================
    // 4. TEST LIFECYCLE (@PreUpdate)
    // ==========================================
    @Test
    void testOnUpdate() throws Exception {
        // Arrange
        PersonalItem item = new PersonalItem();
        
        // Simulasi data lama (waktu kemarin)
        Method onCreateMethod = PersonalItem.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(item);
        
        LocalDateTime oldTime = item.getUpdatedAt();
        
        // Beri jeda sedikit agar waktu berbeda (karena tes berjalan sangat cepat)
        Thread.sleep(10); 

        // Act: Panggil method 'onUpdate'
        Method onUpdateMethod = PersonalItem.class.getDeclaredMethod("onUpdate");
        onUpdateMethod.setAccessible(true);
        onUpdateMethod.invoke(item);

        // Assert
        assertTrue(item.getUpdatedAt().isAfter(oldTime), "UpdatedAt harus diperbarui menjadi waktu yang lebih baru");
    }
}