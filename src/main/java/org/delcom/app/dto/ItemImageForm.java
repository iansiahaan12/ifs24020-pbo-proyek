package org.delcom.app.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemImageForm {

    private UUID id; // ID barang yang akan diganti gambarnya
    private MultipartFile imageFile; // File gambar yang diupload

    // Constructor
    public ItemImageForm() {
    }

    // --- Getter & Setter ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    // --- Helper Validation Methods (Dipakai di Controller) ---

    // Cek apakah user tidak mengupload file
    public boolean isEmpty() {
        return imageFile == null || imageFile.isEmpty();
    }

    // Cek apakah file yang diupload adalah gambar (JPG, PNG, GIF, WEBP)
    public boolean isValidImage() {
        if (isEmpty()) return false;
        
        String contentType = imageFile.getContentType();
        List<String> validTypes = Arrays.asList(
                "image/jpeg", 
                "image/png", 
                "image/gif", 
                "image/webp"
        );
        
        return validTypes.contains(contentType);
    }

    // Cek ukuran file (byte)
    public boolean isSizeValid(long maxSize) {
        if (isEmpty()) return false;
        return imageFile.getSize() <= maxSize;
    }
}