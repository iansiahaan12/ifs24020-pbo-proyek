package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoverCashFlowFormTests {

    private CoverCashFlowForm coverCashFlowForm;
    private MultipartFile mockMultipartFile;

    @BeforeEach
    void setup() {
        coverCashFlowForm = new CoverCashFlowForm();
        mockMultipartFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Constructor default membuat objek kosong")
    void constructor_default_membuat_objek_kosong() {
        // Act
        CoverCashFlowForm form = new CoverCashFlowForm();

        // Assert
        assertNull(form.getId());
        assertNull(form.getCoverFile());
    }

    @Test
    @DisplayName("Setter dan Getter untuk ID bekerja dengan benar")
    void setter_dan_getter_untuk_id_bekerja_dengan_benar() {
        // Arrange
        UUID expectedId = UUID.randomUUID();

        // Act
        coverCashFlowForm.setId(expectedId);
        UUID actualId = coverCashFlowForm.getId();

        // Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Setter dan Getter untuk coverFile bekerja dengan benar")
    void setter_dan_getter_untuk_coverFile_bekerja_dengan_benar() {
        // Act
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        MultipartFile actualFile = coverCashFlowForm.getCoverFile();

        // Assert
        assertEquals(mockMultipartFile, actualFile);
    }

    @Test
    @DisplayName("isEmpty return true ketika coverFile null")
    void isEmpty_return_true_ketika_coverFile_null() {
        // Arrange
        coverCashFlowForm.setCoverFile(null);

        // Act
        boolean result = coverCashFlowForm.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isEmpty return true ketika coverFile empty")
    void isEmpty_return_true_ketika_coverFile_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isEmpty return false ketika coverFile tidak empty")
    void isEmpty_return_false_ketika_coverFile_tidak_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isEmpty();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("getOriginalFilename return null ketika coverFile null")
    void getOriginalFilename_return_null_ketika_coverFile_null() {
        // Arrange
        coverCashFlowForm.setCoverFile(null);

        // Act
        String result = coverCashFlowForm.getOriginalFilename();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getOriginalFilename return filename ketika coverFile ada")
    void getOriginalFilename_return_filename_ketika_coverFile_ada() {
        // Arrange
        String expectedFilename = "cashflow-receipt.jpg";
        when(mockMultipartFile.getOriginalFilename()).thenReturn(expectedFilename);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        String result = coverCashFlowForm.getOriginalFilename();

        // Assert
        assertEquals(expectedFilename, result);
    }

    @Test
    @DisplayName("isValidImage return false ketika coverFile null")
    void isValidImage_return_false_ketika_coverFile_null() {
        // Arrange
        coverCashFlowForm.setCoverFile(null);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return false ketika coverFile empty")
    void isValidImage_return_false_ketika_coverFile_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return false ketika contentType null")
    void isValidImage_return_false_ketika_contentType_null() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn(null);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/jpeg")
    void isValidImage_return_true_untuk_image_jpeg() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/png")
    void isValidImage_return_true_untuk_image_png() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/gif")
    void isValidImage_return_true_untuk_image_gif() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/gif");
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return true untuk image/webp")
    void isValidImage_return_true_untuk_image_webp() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/webp");
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isValidImage();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isValidImage return false untuk content type non-image")
    void isValidImage_return_false_untuk_content_type_non_image() {
        // Arrange
        String[] invalidContentTypes = {
                "text/plain",
                "application/pdf",
                "application/octet-stream",
                "video/mp4",
                "audio/mpeg",
                "image/svg+xml", // SVG tidak didukung
                "image/bmp" // BMP tidak didukung
        };

        for (String contentType : invalidContentTypes) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(contentType);
            coverCashFlowForm.setCoverFile(mockMultipartFile);

            // Act
            boolean result = coverCashFlowForm.isValidImage();

            // Assert
            assertFalse(result, "Should return false for content type: " + contentType);
        }
    }

    @Test
    @DisplayName("isSizeValid return false ketika coverFile null")
    void isSizeValid_return_false_ketika_coverFile_null() {
        // Arrange
        coverCashFlowForm.setCoverFile(null);
        long maxSize = 1024 * 1024; // 1MB

        // Act
        boolean result = coverCashFlowForm.isSizeValid(maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isSizeValid return true ketika file size sama dengan maxSize")
    void isSizeValid_return_true_ketika_file_size_sama_dengan_maxSize() {
        // Arrange
        long maxSize = 1024 * 1024; // 1MB
        when(mockMultipartFile.getSize()).thenReturn(maxSize);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isSizeValid(maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isSizeValid return true ketika file size kurang dari maxSize")
    void isSizeValid_return_true_ketika_file_size_kurang_dari_maxSize() {
        // Arrange
        long maxSize = 1024 * 1024; // 1MB
        long fileSize = 512 * 1024; // 0.5MB
        when(mockMultipartFile.getSize()).thenReturn(fileSize);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isSizeValid(maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isSizeValid return false ketika file size lebih dari maxSize")
    void isSizeValid_return_false_ketika_file_size_lebih_dari_maxSize() {
        // Arrange
        long maxSize = 1024 * 1024; // 1MB
        long fileSize = 2 * 1024 * 1024; // 2MB
        when(mockMultipartFile.getSize()).thenReturn(fileSize);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isSizeValid(maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isSizeValid return true untuk file size 0 dengan maxSize 0")
    void isSizeValid_return_true_untuk_file_size_0_dengan_maxSize_0() {
        // Arrange
        when(mockMultipartFile.getSize()).thenReturn(0L);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverCashFlowForm.isSizeValid(0L);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Integration test - form valid untuk image JPEG ukuran normal")
    void integration_test_form_valid_untuk_image_JPEG_ukuran_normal() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getSize()).thenReturn(500 * 1024L); // 500KB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("receipt.jpg");

        coverCashFlowForm.setId(id);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Assert semua kondisi
        assertFalse(coverCashFlowForm.isEmpty());
        assertEquals("receipt.jpg", coverCashFlowForm.getOriginalFilename());
        assertTrue(coverCashFlowForm.isValidImage());
        assertTrue(coverCashFlowForm.isSizeValid(1024 * 1024)); // 1MB max
        assertEquals(id, coverCashFlowForm.getId());
    }

    @Test
    @DisplayName("Integration test - form invalid untuk file besar")
    void integration_test_form_invalid_untuk_file_besar() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        when(mockMultipartFile.getSize()).thenReturn(5 * 1024 * 1024L); // 5MB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("large-receipt.png");

        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Assert
        assertFalse(coverCashFlowForm.isEmpty());
        assertTrue(coverCashFlowForm.isValidImage()); // Masih valid sebagai image
        assertFalse(coverCashFlowForm.isSizeValid(2 * 1024 * 1024)); // Tapi size melebihi 2MB
    }

    @Test
    @DisplayName("Edge case - contentType case insensitive")
    void edge_case_contentType_case_insensitive() {
        // Arrange
        String[] caseVariations = {
                "IMAGE/JPEG",
                "Image/Jpeg",
                "image/JPEG",
                "IMAGE/jpeg"
        };

        for (String contentType : caseVariations) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(contentType);
            coverCashFlowForm.setCoverFile(mockMultipartFile);

            // Act
            boolean result = coverCashFlowForm.isValidImage();

            // Assert
            assertFalse(result, "Should return false for case variation: " + contentType);
        }
    }

    @Test
    @DisplayName("Integration test - upload receipt untuk transaksi Inflow")
    void integration_test_upload_receipt_untuk_transaksi_inflow() {
        // Arrange
        UUID cashFlowId = UUID.randomUUID();
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getSize()).thenReturn(300 * 1024L); // 300KB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("salary-receipt.jpg");

        coverCashFlowForm.setId(cashFlowId);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Assert - cocok untuk upload bukti pemasukan
        assertNotNull(coverCashFlowForm.getId());
        assertFalse(coverCashFlowForm.isEmpty());
        assertEquals("salary-receipt.jpg", coverCashFlowForm.getOriginalFilename());
        assertTrue(coverCashFlowForm.isValidImage());
        assertTrue(coverCashFlowForm.isSizeValid(1024 * 1024)); // 1MB max
    }

    @Test
    @DisplayName("Integration test - upload receipt untuk transaksi Outflow")
    void integration_test_upload_receipt_untuk_transaksi_outflow() {
        // Arrange
        UUID cashFlowId = UUID.randomUUID();
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        when(mockMultipartFile.getSize()).thenReturn(450 * 1024L); // 450KB
        when(mockMultipartFile.getOriginalFilename()).thenReturn("shopping-receipt.png");

        coverCashFlowForm.setId(cashFlowId);
        coverCashFlowForm.setCoverFile(mockMultipartFile);

        // Assert - cocok untuk upload bukti pengeluaran
        assertNotNull(coverCashFlowForm.getId());
        assertFalse(coverCashFlowForm.isEmpty());
        assertEquals("shopping-receipt.png", coverCashFlowForm.getOriginalFilename());
        assertTrue(coverCashFlowForm.isValidImage());
        assertTrue(coverCashFlowForm.isSizeValid(1024 * 1024)); // 1MB max
    }

    @Test
    @DisplayName("Test validasi untuk berbagai format gambar receipt")
    void test_validasi_untuk_berbagai_format_gambar_receipt() {
        // Test JPEG
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        assertTrue(coverCashFlowForm.isValidImage());

        // Test PNG
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        assertTrue(coverCashFlowForm.isValidImage());

        // Test WebP (format modern)
        when(mockMultipartFile.getContentType()).thenReturn("image/webp");
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        assertTrue(coverCashFlowForm.isValidImage());
    }

    @Test
    @DisplayName("Test ukuran file untuk berbagai skenario bisnis")
    void test_ukuran_file_untuk_berbagai_skenario_bisnis() {
        // Scenario 1: Receipt foto dari HP (biasanya 200-500KB)
        when(mockMultipartFile.getSize()).thenReturn(350 * 1024L);
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        assertTrue(coverCashFlowForm.isSizeValid(1024 * 1024)); // 1MB limit
        assertTrue(coverCashFlowForm.isSizeValid(2 * 1024 * 1024)); // 2MB limit

        // Scenario 2: Scan dokumen (biasanya 500KB-2MB)
        when(mockMultipartFile.getSize()).thenReturn(1500 * 1024L);
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        assertTrue(coverCashFlowForm.isSizeValid(2 * 1024 * 1024)); // 2MB limit
        assertFalse(coverCashFlowForm.isSizeValid(1024 * 1024)); // 1MB limit (terlalu kecil)

        // Scenario 3: File terlalu besar (lebih dari 5MB)
        when(mockMultipartFile.getSize()).thenReturn(6 * 1024 * 1024L);
        coverCashFlowForm.setCoverFile(mockMultipartFile);
        assertFalse(coverCashFlowForm.isSizeValid(5 * 1024 * 1024)); // 5MB limit
    }
}