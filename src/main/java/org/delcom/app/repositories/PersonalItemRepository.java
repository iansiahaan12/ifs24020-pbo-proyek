package org.delcom.app.repositories;

import org.delcom.app.entities.PersonalItem; // Menggunakan entitas PersonalItem
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalItemRepository extends JpaRepository<PersonalItem, UUID> {

    // Mencari semua barang berdasarkan user ID
    // Berguna untuk menampilkan daftar barang di Dashboard
    List<PersonalItem> findByUserId(UUID userId);

    // Mencari barang spesifik berdasarkan ID barang dan ID pemilik (User)
    // Penting untuk keamanan agar user tidak bisa edit/hapus barang orang lain
    Optional<PersonalItem> findByIdAndUserId(UUID id, UUID userId);

    // Mencari barang berdasarkan user ID dengan fitur PENCARIAN (Search)
    // Field yang dicari: Nama Barang, Kategori, Kondisi, atau Deskripsi
    @Query("SELECT p FROM PersonalItem p WHERE p.userId = :userId AND " +
           "(LOWER(p.itemName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.condition) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<PersonalItem> findByUserIdWithSearch(@Param("userId") UUID userId, @Param("search") String search);

    // Mendapatkan semua Kategori unik berdasarkan user ID
    // Query ini sangat penting untuk fitur CHART (Statistik kategori) & Dropdown Filter
    @Query("SELECT DISTINCT p.category FROM PersonalItem p WHERE p.userId = :userId ORDER BY p.category")
    List<String> findDistinctCategoriesByUserId(@Param("userId") UUID userId);

    // Menghapus barang berdasarkan ID dan user ID
    void deleteByIdAndUserId(UUID id, UUID userId);
}