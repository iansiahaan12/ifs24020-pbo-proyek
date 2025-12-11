package org.delcom.app.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personal_items") // Nama tabel diubah sesuai konteks
public class PersonalItem {

    // --- 1. Atribut Wajib (Sesuai Soal) ---
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- 2. Atribut Tambahan (Total Atribut > 8) ---
    
    @Column(name = "item_name", nullable = false)
    private String itemName; // Nama Barang

    @Column(name = "category", nullable = false)
    private String category; // Kategori (Penting untuk Fitur Chart)

    @Column(name = "price", nullable = false)
    private Double price; // Harga Beli/Estimasi (Bisa tipe Long jika Rupiah tanpa sen)

    @Column(name = "item_condition", nullable = false)
    private String condition; // Kondisi: "Baru", "Bekas", "Rusak"

    @Column(name = "description", nullable = true, length = 500)
    private String description; // Deskripsi detail barang

    @Column(name = "image_path", nullable = true)
    private String imagePath; // Path file gambar (Untuk Fitur Ubah Data Gambar)

    @Column(name = "purchase_date")
    private LocalDate purchaseDate; // Tanggal Beli

    @Column(name = "storage_location")
    private String storageLocation; // Lokasi (Contoh: "Lemari Kaca")

    // --- Constructor ---
    public PersonalItem() {
        // Constructor kosong diperlukan oleh JPA/Hibernate
    }

    public PersonalItem(UUID userId, String itemName, String category, Double price, String condition, String description, String imagePath, LocalDate purchaseDate, String storageLocation) {
        this.userId = userId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.condition = condition;
        this.description = description;
        this.imagePath = imagePath;
        this.purchaseDate = purchaseDate;
        this.storageLocation = storageLocation;
    }

    // --- Getter & Setter ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }


    // --- Lifecycle Callbacks ---
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.id == null) {
            this.id = UUID.randomUUID(); // Generate UUID jika belum ada
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}