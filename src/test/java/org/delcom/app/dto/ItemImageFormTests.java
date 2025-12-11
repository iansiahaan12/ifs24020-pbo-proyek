package org.delcom.app.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ItemImageFormTests {

    @Test
    @DisplayName("Test ItemImageForm logic validasi file")
    void testImageValidationLogic() {
        // Mocking MultipartFile (Pura-pura ada file yang diupload)
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);

        ItemImageForm form = new ItemImageForm();
        form.setId(UUID.randomUUID());

        // --- Skenario 1: File Kosong ---
        when(mockFile.isEmpty()).thenReturn(true);
        form.setImageFile(mockFile);
        
        assertTrue(form.isEmpty(), "Form harus terdeteksi kosong jika file empty");
        assertFalse(form.isValidImage(), "Image tidak valid jika file kosong");

        // --- Skenario 2: Format Valid (PNG) ---
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getSize()).thenReturn(1024L); // 1KB

        form.setImageFile(mockFile);
        assertTrue(form.isValidImage(), "Format image/png harusnya valid");
        
        // --- Skenario 3: Format Valid (JPEG) ---
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        assertTrue(form.isValidImage(), "Format image/jpeg harusnya valid");

        // --- Skenario 4: Format TIDAK Valid (PDF) ---
        when(mockFile.getContentType()).thenReturn("application/pdf");
        assertFalse(form.isValidImage(), "Format PDF harusnya tidak valid");

        // --- Skenario 5: Ukuran File ---
        long maxLimit = 5000L;
        
        // Case A: Ukuran kecil (Valid)
        when(mockFile.getSize()).thenReturn(4000L);
        assertTrue(form.isSizeValid(maxLimit), "Ukuran di bawah limit harus valid");

        // Case B: Ukuran besar (Invalid)
        when(mockFile.getSize()).thenReturn(6000L);
        assertFalse(form.isSizeValid(maxLimit), "Ukuran di atas limit harus invalid");
    }
    
    @Test
    @DisplayName("Test Getter Setter ItemImageForm")
    void testGetterSetter() {
        ItemImageForm form = new ItemImageForm();
        UUID uuid = UUID.randomUUID();
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);

        form.setId(uuid);
        form.setImageFile(mockFile);

        assert(form.getId().equals(uuid));
        assert(form.getImageFile().equals(mockFile));
    }
}