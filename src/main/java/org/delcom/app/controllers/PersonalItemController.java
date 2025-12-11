package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.PersonalItem;
import org.delcom.app.entities.User;
import org.delcom.app.services.PersonalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class PersonalItemController {
    
    private final PersonalItemService personalItemService;

    @Autowired
    protected AuthContext authContext;

    public PersonalItemController(PersonalItemService personalItemService) {
        this.personalItemService = personalItemService;
    }

    // ==========================================
    // 1. UPDATE CREATE ITEM (Tambah Parameter)
    // ==========================================
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createItem(@RequestBody PersonalItem reqItem) {

        // Validasi input
        if (reqItem.getItemName() == null || reqItem.getItemName().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Nama barang tidak boleh kosong", null));
        } else if (reqItem.getCategory() == null || reqItem.getCategory().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Kategori tidak boleh kosong", null));
        } else if (reqItem.getPrice() == null || reqItem.getPrice() < 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Harga tidak valid", null));
        } else if (reqItem.getCondition() == null || reqItem.getCondition().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Kondisi barang harus diisi", null));
        }

        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        // --- PERBAIKAN DI SINI ---
        // Menambahkan reqItem.getPurchaseDate() dan reqItem.getStorageLocation()
        PersonalItem newItem = personalItemService.createItem(
            authUser.getId(), 
            reqItem.getItemName(), 
            reqItem.getCategory(), 
            reqItem.getPrice(), 
            reqItem.getCondition(), 
            reqItem.getDescription(),
            reqItem.getImagePath(),
            reqItem.getPurchaseDate(),    // <--- Parameter Baru 1
            reqItem.getStorageLocation()  // <--- Parameter Baru 2
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil menambahkan barang ke koleksi",
                Map.of("id", newItem.getId())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<PersonalItem>>>> getAllItems(
            @RequestParam(required = false) String search) {
        
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<PersonalItem> items = personalItemService.getAllItems(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data koleksi",
                Map.of("items", items)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, PersonalItem>>> getItemById(@PathVariable UUID id) {
        
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        PersonalItem item = personalItemService.getItemById(authUser.getId(), id);
        if (item == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data barang tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil detail barang",
                Map.of("item", item)));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getItemCategories() {
        
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<String> categories = personalItemService.getAllCategories(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data kategori",
                Map.of("categories", categories)));
    }

    // ==========================================
    // 2. UPDATE UPDATE ITEM (Tambah Parameter)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonalItem>> updateItem(@PathVariable UUID id, @RequestBody PersonalItem reqItem) {

        if (reqItem.getItemName() == null || reqItem.getItemName().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Nama barang tidak boleh kosong", null));
        } else if (reqItem.getCategory() == null || reqItem.getCategory().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Kategori tidak boleh kosong", null));
        } else if (reqItem.getPrice() == null || reqItem.getPrice() < 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Harga tidak valid", null));
        } else if (reqItem.getCondition() == null || reqItem.getCondition().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Kondisi barang harus diisi", null));
        }

        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        // --- PERBAIKAN DI SINI ---
        // Menambahkan reqItem.getPurchaseDate() dan reqItem.getStorageLocation()
        PersonalItem updatedItem = personalItemService.updateItem(
            authUser.getId(), 
            id, 
            reqItem.getItemName(), 
            reqItem.getCategory(), 
            reqItem.getPrice(), 
            reqItem.getCondition(), 
            reqItem.getDescription(),
            reqItem.getImagePath(),
            reqItem.getPurchaseDate(),   // <--- Parameter Baru 1
            reqItem.getStorageLocation() // <--- Parameter Baru 2
        );

        if (updatedItem == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data barang tidak ditemukan atau akses ditolak", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil memperbarui data barang", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable UUID id) {
        
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = personalItemService.deleteItem(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data barang tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data barang berhasil dihapus dari koleksi",
                null));
    }
}