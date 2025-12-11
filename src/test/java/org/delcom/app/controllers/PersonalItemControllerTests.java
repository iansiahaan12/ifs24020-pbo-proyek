package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.PersonalItem;
import org.delcom.app.entities.User;
import org.delcom.app.services.PersonalItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonalItemControllerTests {

    @Mock
    private PersonalItemService personalItemService;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private PersonalItemController personalItemController;

    private User mockUser;
    private PersonalItem mockItem;
    private UUID userId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        // Setup Mock User
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");

        // Setup Mock Item (Default Valid)
        mockItem = new PersonalItem();
        mockItem.setId(itemId);
        mockItem.setUserId(userId);
        mockItem.setItemName("iPad Pro");
        mockItem.setCategory("Elektronik");
        mockItem.setPrice(15000000.0);
        mockItem.setCondition("Baru");
        mockItem.setDescription("Mulus");
        mockItem.setPurchaseDate(LocalDate.now());
        mockItem.setStorageLocation("Lemari");
        ReflectionTestUtils.setField(personalItemController, "authContext", authContext);
    }

    // ==========================================
    // 1. TEST CREATE ITEM (SUCCESS & AUTH)
    // ==========================================

    @Test
    @DisplayName("Create Item: Berhasil membuat item baru")
    void testCreateItem_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.createItem(any(), anyString(), anyString(), anyDouble(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(mockItem);

        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals(itemId, response.getBody().getData().get("id"));
    }

    @Test
    @DisplayName("Create Item: Gagal jika belum login")
    void testCreateItem_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ==========================================
    // 2. TEST CREATE ITEM (VALIDATION)
    // Mencakup Null & Empty untuk menutup Diamond Kuning
    // ==========================================

    @Test
    @DisplayName("Create Validation: Name Null")
    void testCreate_NameNull() {
        mockItem.setItemName(null);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Nama"));
    }

    @Test
    @DisplayName("Create Validation: Name Empty")
    void testCreate_NameEmpty() {
        mockItem.setItemName("");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Nama"));
    }

    @Test
    @DisplayName("Create Validation: Category Null")
    void testCreate_CategoryNull() {
        mockItem.setCategory(null);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kategori"));
    }

    @Test
    @DisplayName("Create Validation: Category Empty")
    void testCreate_CategoryEmpty() {
        mockItem.setCategory("");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kategori"));
    }

    @Test
    @DisplayName("Create Validation: Price Null")
    void testCreate_PriceNull() {
        mockItem.setPrice(null);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Harga"));
    }

    @Test
    @DisplayName("Create Validation: Price Negative")
    void testCreate_PriceNegative() {
        mockItem.setPrice(-100.0);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Harga"));
    }

    @Test
    @DisplayName("Create Validation: Condition Null")
    void testCreate_ConditionNull() {
        mockItem.setCondition(null);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kondisi"));
    }

    @Test
    @DisplayName("Create Validation: Condition Empty")
    void testCreate_ConditionEmpty() {
        mockItem.setCondition("");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = personalItemController.createItem(mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kondisi"));
    }

    // ==========================================
    // 3. TEST UPDATE ITEM (VALIDATION)
    // Ini yang banyak merah/kuning di screenshot
    // ==========================================

    @Test
    @DisplayName("Update Validation: Name Null")
    void testUpdate_NameNull() {
        mockItem.setItemName(null);
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Nama"));
    }

    @Test
    @DisplayName("Update Validation: Name Empty")
    void testUpdate_NameEmpty() {
        mockItem.setItemName("");
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Nama"));
    }

    @Test
    @DisplayName("Update Validation: Category Null")
    void testUpdate_CategoryNull() {
        mockItem.setCategory(null);
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kategori"));
    }

    @Test
    @DisplayName("Update Validation: Category Empty")
    void testUpdate_CategoryEmpty() {
        mockItem.setCategory("");
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kategori"));
    }

    @Test
    @DisplayName("Update Validation: Price Null")
    void testUpdate_PriceNull() {
        mockItem.setPrice(null);
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Harga"));
    }

    @Test
    @DisplayName("Update Validation: Price Negative")
    void testUpdate_PriceNegative() {
        mockItem.setPrice(-1.0);
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Harga"));
    }

    @Test
    @DisplayName("Update Validation: Condition Null")
    void testUpdate_ConditionNull() {
        mockItem.setCondition(null);
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kondisi"));
    }

    @Test
    @DisplayName("Update Validation: Condition Empty")
    void testUpdate_ConditionEmpty() {
        mockItem.setCondition("");
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Kondisi"));
    }

    // ==========================================
    // 4. TEST UPDATE ITEM (LOGIC)
    // ==========================================

    @Test
    @DisplayName("Update Item: Berhasil update data")
    void testUpdateItem_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.updateItem(any(), any(), anyString(), anyString(), anyDouble(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(mockItem);

        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
    }

    @Test
    @DisplayName("Update Item: Gagal jika item tidak ditemukan")
    void testUpdateItem_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.updateItem(any(), any(), anyString(), anyString(), anyDouble(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(null);

        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Item: Return 403 jika belum login")
    void testUpdateItem_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        ResponseEntity<ApiResponse<PersonalItem>> response = personalItemController.updateItem(itemId, mockItem);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ==========================================
    // 5. TEST GET ALL ITEMS
    // ==========================================

    @Test
    @DisplayName("GetAllItems: Berhasil mengambil data")
    void testGetAllItems_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getAllItems(eq(userId), any())).thenReturn(Arrays.asList(mockItem));

        ResponseEntity<ApiResponse<Map<String, List<PersonalItem>>>> response = personalItemController.getAllItems(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData().get("items"));
    }

    @Test
    @DisplayName("GetAllItems: Return 403 jika belum login")
    void testGetAllItems_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        ResponseEntity<ApiResponse<Map<String, List<PersonalItem>>>> response = personalItemController.getAllItems(null);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ==========================================
    // 6. TEST GET ITEM BY ID
    // ==========================================

    @Test
    @DisplayName("GetItemById: Berhasil mengambil detail")
    void testGetItemById_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getItemById(userId, itemId)).thenReturn(mockItem);

        ResponseEntity<ApiResponse<Map<String, PersonalItem>>> response = personalItemController.getItemById(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GetItemById: Gagal jika item tidak ditemukan")
    void testGetItemById_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getItemById(userId, itemId)).thenReturn(null);

        ResponseEntity<ApiResponse<Map<String, PersonalItem>>> response = personalItemController.getItemById(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GetItemById: Return 403 jika belum login")
    void testGetItemById_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        ResponseEntity<ApiResponse<Map<String, PersonalItem>>> response = personalItemController.getItemById(itemId);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ==========================================
    // 7. TEST DELETE ITEM
    // ==========================================

    @Test
    @DisplayName("Delete Item: Berhasil hapus data")
    void testDeleteItem_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.deleteItem(userId, itemId)).thenReturn(true);

        ResponseEntity<ApiResponse<String>> response = personalItemController.deleteItem(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete Item: Gagal jika item tidak ditemukan")
    void testDeleteItem_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.deleteItem(userId, itemId)).thenReturn(false);

        ResponseEntity<ApiResponse<String>> response = personalItemController.deleteItem(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete Item: Return 403 jika belum login")
    void testDeleteItem_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        ResponseEntity<ApiResponse<String>> response = personalItemController.deleteItem(itemId);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ==========================================
    // 8. TEST GET CATEGORIES
    // ==========================================
    
    @Test
    @DisplayName("GetCategories: Berhasil mengambil kategori")
    void testGetCategories_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getAllCategories(userId)).thenReturn(Arrays.asList("Elektronik", "Buku"));

        ResponseEntity<ApiResponse<Map<String, List<String>>>> response = personalItemController.getItemCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GetCategories: Return 403 jika belum login")
    void testGetCategories_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        ResponseEntity<ApiResponse<Map<String, List<String>>>> response = personalItemController.getItemCategories();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}