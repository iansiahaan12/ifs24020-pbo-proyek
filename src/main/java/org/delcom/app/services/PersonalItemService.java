package org.delcom.app.services;

import org.delcom.app.entities.PersonalItem;
import org.delcom.app.repositories.PersonalItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PersonalItemService {

    // Menggunakan Repository khusus PersonalItem
    private final PersonalItemRepository personalItemRepository;

    public PersonalItemService(PersonalItemRepository personalItemRepository) {
        this.personalItemRepository = personalItemRepository;
    }

    // Membuat item koleksi baru
    public PersonalItem createItem(UUID userId, String itemName, String category, Double price, String condition, String description, String imagePath, LocalDate purchaseDate, String storageLocation) {
        // Membuat objek entity baru
        PersonalItem item = new PersonalItem(userId, itemName, category, price, condition, description, imagePath, purchaseDate, storageLocation);
        
        // Menyimpan ke database
        return personalItemRepository.save(item);
    }

    // Mendapatkan semua item berdasarkan user ID dengan opsi pencarian (Search)
    public List<PersonalItem> getAllItems(UUID userId, String search) {
        if (search != null && !search.isEmpty()) {
            // Asumsi repository memiliki method custom query untuk search nama/kategori
            return personalItemRepository.findByUserIdWithSearch(userId, search);
        }
        return personalItemRepository.findByUserId(userId);
    }

    // Mendapatkan item spesifik berdasarkan ID dan User ID (Security check)
    public PersonalItem getItemById(UUID userId, UUID id) {
        return personalItemRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    // Mendapatkan semua kategori unik (untuk keperluan Chart atau Filter)
    // Menggantikan getAllLabels
    public List<String> getAllCategories(UUID userId) {
        return personalItemRepository.findDistinctCategoriesByUserId(userId);
    }

    // Memperbarui data item (Termasuk Update Gambar/imagePath)
    public PersonalItem updateItem(UUID userId, UUID id, String itemName, String category, Double price, 
                               String condition, String description, String imagePath,
                               LocalDate purchaseDate, String storageLocation) {
        // Cari data lama
        PersonalItem existingItem = personalItemRepository.findByIdAndUserId(id, userId).orElse(null);
        
        if (existingItem == null) {
            return null; // Item tidak ditemukan atau bukan milik user ini
        }

        // Update value dengan data baru
        existingItem.setItemName(itemName);
        existingItem.setCategory(category);
        existingItem.setPrice(price);
        existingItem.setCondition(condition);
        existingItem.setDescription(description);
        existingItem.setPurchaseDate(purchaseDate);
        existingItem.setStorageLocation(storageLocation);
        
        // Update gambar jika ada path baru yang dikirim
        if (imagePath != null) {
            existingItem.setImagePath(imagePath);
        }

        return personalItemRepository.save(existingItem);
    }

    // Method khusus jika hanya ingin update path gambar secara terpisah (Opsional/Utility)
    public void updateImagePathOnly(UUID id, String imagePath) {
        // Menggunakan findById saja karena biasanya method ini dipanggil internal setelah upload file
        personalItemRepository.findById(id).ifPresent(item -> {
            item.setImagePath(imagePath);
            personalItemRepository.save(item);
        });
    }

    // Menghapus item koleksi
    @Transactional
    public boolean deleteItem(UUID userId, UUID id) {
        // Cek keberadaan data sebelum hapus
        PersonalItem existingItem = personalItemRepository.findByIdAndUserId(id, userId).orElse(null);
        
        if (existingItem == null) {
            return false;
        }

        personalItemRepository.deleteByIdAndUserId(id, userId);
        return true;
    }
}