package org.delcom.app.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersonalItemFormTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Menyiapkan validator manual untuk test unit
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test PersonalItemForm dengan data valid")
    void testValidPersonalItemForm() {
        PersonalItemForm form = new PersonalItemForm(
                UUID.randomUUID(),
                "Laptop Lenovo",
                "Elektronik",
                5000000.0,
                "Bekas",
                "Deskripsi barang yang valid"
        );

        // Validasi form
        Set<ConstraintViolation<PersonalItemForm>> violations = validator.validate(form);

        // Harusnya tidak ada error (violations kosong)
        assertTrue(violations.isEmpty(), "Form valid seharusnya tidak memiliki violation");
    }

    @Test
    @DisplayName("Test PersonalItemForm dengan data TIDAK valid")
    void testInvalidPersonalItemForm() {
        PersonalItemForm form = new PersonalItemForm();
        
        // Skenario 1: Semua field null/kosong
        Set<ConstraintViolation<PersonalItemForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty(), "Form kosong harusnya memiliki error");

        // Skenario 2: Harga negatif
        form.setItemName("Mouse");
        form.setCategory("Aksesoris");
        form.setCondition("Baru");
        form.setPrice(-1000.0); // Invalid

        violations = validator.validate(form);
        
        // Cek apakah ada error pada field 'price'
        boolean hasPriceError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("price"));
        
        assertTrue(hasPriceError, "Harga negatif harus dianggap error");

        // Skenario 3: Nama barang terlalu panjang (> 100 karakter)
        String longName = "A".repeat(101);
        form.setPrice(100.0); // Fix price
        form.setItemName(longName); // Invalid name

        violations = validator.validate(form);
        boolean hasNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("itemName"));

        assertTrue(hasNameError, "Nama barang > 100 karakter harus error");
    }
}