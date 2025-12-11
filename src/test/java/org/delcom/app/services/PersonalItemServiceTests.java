package org.delcom.app.services;

import org.delcom.app.entities.PersonalItem;
import org.delcom.app.repositories.PersonalItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonalItemServiceTests {

    @Mock
    private PersonalItemRepository personalItemRepository;

    @InjectMocks
    private PersonalItemService personalItemService;

    private PersonalItem mockItem;
    private UUID userId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        // Setup Mock Data
        mockItem = new PersonalItem();
        mockItem.setId(itemId);
        mockItem.setUserId(userId);
        mockItem.setItemName("Macbook M1");
        mockItem.setCategory("Laptop");
        mockItem.setPrice(12000000.0);
        mockItem.setCondition("Bekas");
        mockItem.setPurchaseDate(LocalDate.now());
        mockItem.setStorageLocation("Meja");
    }

    // ==========================================
    // 1. TEST CREATE ITEM
    // ==========================================
    @Test
    void testCreateItem() {
        // Arrange
        when(personalItemRepository.save(any(PersonalItem.class))).thenReturn(mockItem);

        // Act
        PersonalItem result = personalItemService.createItem(
                userId, "Macbook M1", "Laptop", 12000000.0, "Bekas", "Deskripsi", null, LocalDate.now(), "Meja"
        );

        // Assert
        assertNotNull(result);
        assertEquals("Macbook M1", result.getItemName());
        verify(personalItemRepository, times(1)).save(any(PersonalItem.class));
    }

    // ==========================================
    // 2. TEST GET ALL ITEMS (Search vs No Search vs Empty String)
    // ==========================================
    @Test
    void testGetAllItems_WithoutSearch() {
        // Arrange
        when(personalItemRepository.findByUserId(userId)).thenReturn(Arrays.asList(mockItem));

        // Act
        List<PersonalItem> results = personalItemService.getAllItems(userId, null);

        // Assert
        assertEquals(1, results.size());
        verify(personalItemRepository, times(1)).findByUserId(userId);
        verify(personalItemRepository, never()).findByUserIdWithSearch(any(), any());
    }

    @Test
    void testGetAllItems_WithSearch() {
        // Arrange
        String keyword = "Macbook";
        when(personalItemRepository.findByUserIdWithSearch(userId, keyword)).thenReturn(Arrays.asList(mockItem));

        // Act
        List<PersonalItem> results = personalItemService.getAllItems(userId, keyword);

        // Assert
        assertEquals(1, results.size());
        verify(personalItemRepository, never()).findByUserId(any());
        verify(personalItemRepository, times(1)).findByUserIdWithSearch(userId, keyword);
    }

    @Test
    @DisplayName("GetAllItems: Jika search string kosong, kembalikan semua data")
    void testGetAllItems_WithEmptyString() {
        // Arrange
        String search = ""; // Empty String
        when(personalItemRepository.findByUserId(userId)).thenReturn(Arrays.asList(mockItem));

        // Act
        List<PersonalItem> results = personalItemService.getAllItems(userId, search);

        // Assert
        assertEquals(1, results.size());
        // Verifikasi bahwa yang dipanggil adalah findByUserId (bukan withSearch) karena string kosong dianggap tidak search
        verify(personalItemRepository, times(1)).findByUserId(userId);
        verify(personalItemRepository, never()).findByUserIdWithSearch(any(), any());
    }

    // ==========================================
    // 3. TEST GET ITEM BY ID
    // ==========================================
    @Test
    void testGetItemById_Found() {
        // Arrange
        when(personalItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.of(mockItem));

        // Act
        PersonalItem result = personalItemService.getItemById(userId, itemId);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    void testGetItemById_NotFound() {
        // Arrange
        when(personalItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.empty());

        // Act
        PersonalItem result = personalItemService.getItemById(userId, itemId);

        // Assert
        assertNull(result);
    }

    // ==========================================
    // 4. TEST GET CATEGORIES
    // ==========================================
    @Test
    @DisplayName("GetAllCategories: Berhasil mengambil daftar kategori")
    void testGetAllCategories() {
        // Arrange
        List<String> mockCategories = Arrays.asList("Elektronik", "Fashion");
        when(personalItemRepository.findDistinctCategoriesByUserId(userId)).thenReturn(mockCategories);

        // Act
        List<String> result = personalItemService.getAllCategories(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Elektronik", result.get(0));
        verify(personalItemRepository, times(1)).findDistinctCategoriesByUserId(userId);
    }

    // ==========================================
    // 5. TEST UPDATE ITEM
    // ==========================================
    @Test
    void testUpdateItem_Success() {
        // Arrange
        // Mocking item lama yang ada di DB
        when(personalItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.of(mockItem));
        // Mocking proses save
        when(personalItemRepository.save(any(PersonalItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Data baru
        String newName = "Macbook M2";
        Double newPrice = 15000000.0;
        LocalDate newDate = LocalDate.of(2025, 1, 1);
        String newLoc = "Gudang";

        // Act
        PersonalItem updated = personalItemService.updateItem(
                userId, itemId, newName, "Laptop", newPrice, "Baru", "Desc", "img.jpg", newDate, newLoc
        );

        // Assert
        assertNotNull(updated);
        assertEquals(newName, updated.getItemName());
        assertEquals(newPrice, updated.getPrice());
        assertEquals(newDate, updated.getPurchaseDate());     // Cek Tanggal Update
        assertEquals(newLoc, updated.getStorageLocation());   // Cek Lokasi Update
        assertEquals("img.jpg", updated.getImagePath());      // Cek Gambar Update
    }

    @Test
    @DisplayName("UpdateItem: Jika imagePath null, jangan ubah gambar lama")
    void testUpdateItem_WithNullImagePath_ShouldNotUpdateImage() {
        // Arrange
        // 1. Set gambar awal pada item yang ada di DB
        mockItem.setImagePath("gambar-lama.jpg");
        
        when(personalItemRepository.findByIdAndUserId(itemId, userId))
            .thenReturn(Optional.of(mockItem));
        
        // Mock save agar mengembalikan objek yang disimpan
        when(personalItemRepository.save(any(PersonalItem.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        // 2. Panggil update dengan imagePath = NULL
        PersonalItem updatedResult = personalItemService.updateItem(
                userId, 
                itemId, 
                "Nama Baru", 
                "Kategori", 
                100.0, 
                "Baru", 
                "Desc", 
                null, // <--- Param Image Path NULL
                LocalDate.now(), 
                "Loc"
        );

        // Assert
        // 3. Pastikan imagePath masih tetap "gambar-lama.jpg"
        assertEquals("gambar-lama.jpg", updatedResult.getImagePath());
        
        // Verifikasi save dipanggil
        verify(personalItemRepository).save(any(PersonalItem.class));
    }

    @Test
    void testUpdateItem_NotFound() {
        // Arrange
        when(personalItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.empty());

        // Act
        PersonalItem updated = personalItemService.updateItem(
                userId, itemId, "Name", "Cat", 100.0, "Cond", "Desc", null, null, null
        );

        // Assert
        assertNull(updated);
        verify(personalItemRepository, never()).save(any());
    }

    // ==========================================
    // 6. TEST UPDATE IMAGE ONLY
    // ==========================================
    @Test
    void testUpdateImagePathOnly() {
        // Arrange
        when(personalItemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));

        // Act
        personalItemService.updateImagePathOnly(itemId, "new-cover.jpg");

        // Assert
        assertEquals("new-cover.jpg", mockItem.getImagePath());
        verify(personalItemRepository, times(1)).save(mockItem);
    }

    // ==========================================
    // 7. TEST DELETE ITEM
    // ==========================================
    @Test
    void testDeleteItem_Success() {
        // Arrange
        when(personalItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.of(mockItem));

        // Act
        boolean result = personalItemService.deleteItem(userId, itemId);

        // Assert
        assertTrue(result);
        verify(personalItemRepository, times(1)).deleteByIdAndUserId(itemId, userId);
    }

    @Test
    void testDeleteItem_NotFound() {
        // Arrange
        when(personalItemRepository.findByIdAndUserId(itemId, userId)).thenReturn(Optional.empty());

        // Act
        boolean result = personalItemService.deleteItem(userId, itemId);

        // Assert
        assertFalse(result);
        verify(personalItemRepository, never()).deleteByIdAndUserId(any(), any());
    }
}