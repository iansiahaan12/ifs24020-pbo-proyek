package org.delcom.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class PersonalItemForm {

    private UUID id;

    @NotBlank(message = "Nama barang tidak boleh kosong")
    @Size(max = 100, message = "Nama barang maksimal 100 karakter")
    private String itemName;

    @NotBlank(message = "Kategori tidak boleh kosong")
    private String category;

    @NotNull(message = "Harga tidak boleh kosong")
    @Min(value = 0, message = "Harga tidak boleh kurang dari 0")
    private Double price;

    @NotBlank(message = "Kondisi barang harus dipilih")
    private String condition; // Contoh: "Baru", "Bekas", "Rusak"

    @Size(max = 500, message = "Deskripsi maksimal 500 karakter")
    private String description;

    private LocalDate purchaseDate;
    private String storageLocation;

    // Constructor Kosong
    public PersonalItemForm() {
    }

    // Constructor Lengkap
    public PersonalItemForm(UUID id, String itemName, String category, Double price, String condition, String description) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.condition = condition;
        this.description = description;
    }

    // --- Getter & Setter ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }    
}