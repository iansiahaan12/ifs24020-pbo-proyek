package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CashFlowFormTest {

    private CashFlowForm cashFlowForm;

    @BeforeEach
    void setUp() {
        cashFlowForm = new CashFlowForm();
    }

    @Test
    @DisplayName("Default constructor membuat objek dengan nilai default")
    void defaultConstructor_CreatesObjectWithDefaultValues() {
        assertNull(cashFlowForm.getId());
        assertNull(cashFlowForm.getType());
        assertNull(cashFlowForm.getSource());
        assertNull(cashFlowForm.getLabel());
        assertNull(cashFlowForm.getAmount());
        assertNull(cashFlowForm.getDescription());
        assertNull(cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("Setter dan Getter untuk id bekerja dengan benar")
    void setterAndGetter_Id_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        cashFlowForm.setId(id);
        assertEquals(id, cashFlowForm.getId());
    }

    @Test
    @DisplayName("Setter dan Getter untuk type bekerja dengan benar - Inflow")
    void setterAndGetter_Type_WorksCorrectly_Inflow() {
        String type = "Inflow";
        cashFlowForm.setType(type);
        assertEquals(type, cashFlowForm.getType());
    }

    @Test
    @DisplayName("Setter dan Getter untuk type bekerja dengan benar - Outflow")
    void setterAndGetter_Type_WorksCorrectly_Outflow() {
        String type = "Outflow";
        cashFlowForm.setType(type);
        assertEquals(type, cashFlowForm.getType());
    }

    @Test
    @DisplayName("Setter dan Getter untuk source bekerja dengan benar")
    void setterAndGetter_Source_WorksCorrectly() {
        String source = "Gaji";
        cashFlowForm.setSource(source);
        assertEquals(source, cashFlowForm.getSource());
    }

    @Test
    @DisplayName("Setter dan Getter untuk label bekerja dengan benar")
    void setterAndGetter_Label_WorksCorrectly() {
        String label = "Pendapatan";
        cashFlowForm.setLabel(label);
        assertEquals(label, cashFlowForm.getLabel());
    }

    @Test
    @DisplayName("Setter dan Getter untuk amount bekerja dengan benar")
    void setterAndGetter_Amount_WorksCorrectly() {
        Long amount = 5000000L;
        cashFlowForm.setAmount(amount);
        assertEquals(amount, cashFlowForm.getAmount());
    }

    @Test
    @DisplayName("Setter dan Getter untuk description bekerja dengan benar")
    void setterAndGetter_Description_WorksCorrectly() {
        String description = "Gaji bulan Januari";
        cashFlowForm.setDescription(description);
        assertEquals(description, cashFlowForm.getDescription());
    }

    @Test
    @DisplayName("Setter dan Getter untuk confirmSource bekerja dengan benar")
    void setterAndGetter_ConfirmSource_WorksCorrectly() {
        String confirmSource = "Gaji";
        cashFlowForm.setConfirmSource(confirmSource);
        assertEquals(confirmSource, cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("Amount dapat diset dengan nilai 0")
    void amount_CanBeSet_WithZeroValue() {
        cashFlowForm.setAmount(0L);
        assertEquals(0L, cashFlowForm.getAmount());
    }

    @Test
    @DisplayName("Amount dapat diset dengan nilai negatif")
    void amount_CanBeSet_WithNegativeValue() {
        cashFlowForm.setAmount(-1000000L);
        assertEquals(-1000000L, cashFlowForm.getAmount());
    }

    @Test
    @DisplayName("Amount dapat diset dengan nilai besar")
    void amount_CanBeSet_WithLargeValue() {
        Long largeAmount = 999999999999L;
        cashFlowForm.setAmount(largeAmount);
        assertEquals(largeAmount, cashFlowForm.getAmount());
    }

    @Test
    @DisplayName("Semua field dapat diset dan diget dengan nilai berbagai tipe")
    void allFields_CanBeSetAndGet_WithVariousValues() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String type = "Inflow";
        String source = "Gaji";
        String label = "Pendapatan";
        Long amount = 5000000L;
        String description = "Gaji bulan Januari 2025";
        String confirmSource = "Gaji";

        // Act
        cashFlowForm.setId(id);
        cashFlowForm.setType(type);
        cashFlowForm.setSource(source);
        cashFlowForm.setLabel(label);
        cashFlowForm.setAmount(amount);
        cashFlowForm.setDescription(description);
        cashFlowForm.setConfirmSource(confirmSource);

        // Assert
        assertEquals(id, cashFlowForm.getId());
        assertEquals(type, cashFlowForm.getType());
        assertEquals(source, cashFlowForm.getSource());
        assertEquals(label, cashFlowForm.getLabel());
        assertEquals(amount, cashFlowForm.getAmount());
        assertEquals(description, cashFlowForm.getDescription());
        assertEquals(confirmSource, cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("Field dapat diset dengan null values")
    void fields_CanBeSet_WithNullValues() {
        // Act
        cashFlowForm.setId(null);
        cashFlowForm.setType(null);
        cashFlowForm.setSource(null);
        cashFlowForm.setLabel(null);
        cashFlowForm.setAmount(null);
        cashFlowForm.setDescription(null);
        cashFlowForm.setConfirmSource(null);

        // Assert
        assertNull(cashFlowForm.getId());
        assertNull(cashFlowForm.getType());
        assertNull(cashFlowForm.getSource());
        assertNull(cashFlowForm.getLabel());
        assertNull(cashFlowForm.getAmount());
        assertNull(cashFlowForm.getDescription());
        assertNull(cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("String field dapat diset dengan empty strings")
    void stringFields_CanBeSet_WithEmptyStrings() {
        // Act
        cashFlowForm.setType("");
        cashFlowForm.setSource("");
        cashFlowForm.setLabel("");
        cashFlowForm.setDescription("");
        cashFlowForm.setConfirmSource("");

        // Assert
        assertEquals("", cashFlowForm.getType());
        assertEquals("", cashFlowForm.getSource());
        assertEquals("", cashFlowForm.getLabel());
        assertEquals("", cashFlowForm.getDescription());
        assertEquals("", cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("String field dapat diset dengan blank strings")
    void stringFields_CanBeSet_WithBlankStrings() {
        // Act
        cashFlowForm.setType("   ");
        cashFlowForm.setSource("   ");
        cashFlowForm.setLabel("   ");
        cashFlowForm.setDescription("   ");
        cashFlowForm.setConfirmSource("   ");

        // Assert
        assertEquals("   ", cashFlowForm.getType());
        assertEquals("   ", cashFlowForm.getSource());
        assertEquals("   ", cashFlowForm.getLabel());
        assertEquals("   ", cashFlowForm.getDescription());
        assertEquals("   ", cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("Type dapat diubah dari Inflow ke Outflow")
    void type_CanBeChanged_FromInflowToOutflow() {
        // Arrange
        cashFlowForm.setType("Inflow");
        assertEquals("Inflow", cashFlowForm.getType());

        // Act
        cashFlowForm.setType("Outflow");

        // Assert
        assertEquals("Outflow", cashFlowForm.getType());
    }

    @Test
    @DisplayName("Type dapat diubah dari Outflow ke Inflow")
    void type_CanBeChanged_FromOutflowToInflow() {
        // Arrange
        cashFlowForm.setType("Outflow");
        assertEquals("Outflow", cashFlowForm.getType());

        // Act
        cashFlowForm.setType("Inflow");

        // Assert
        assertEquals("Inflow", cashFlowForm.getType());
    }

    @Test
    @DisplayName("Multiple operations pada object yang sama")
    void multipleOperations_OnSameObject() {
        // First set of values
        UUID id1 = UUID.randomUUID();
        cashFlowForm.setId(id1);
        cashFlowForm.setType("Inflow");
        cashFlowForm.setSource("Gaji");
        cashFlowForm.setAmount(5000000L);

        assertEquals(id1, cashFlowForm.getId());
        assertEquals("Inflow", cashFlowForm.getType());
        assertEquals("Gaji", cashFlowForm.getSource());
        assertEquals(5000000L, cashFlowForm.getAmount());

        // Second set of values
        UUID id2 = UUID.randomUUID();
        cashFlowForm.setId(id2);
        cashFlowForm.setType("Outflow");
        cashFlowForm.setSource("Belanja");
        cashFlowForm.setAmount(1000000L);

        assertEquals(id2, cashFlowForm.getId());
        assertEquals("Outflow", cashFlowForm.getType());
        assertEquals("Belanja", cashFlowForm.getSource());
        assertEquals(1000000L, cashFlowForm.getAmount());
    }

    @Test
    @DisplayName("Form untuk Inflow dengan data lengkap")
    void form_ForInflow_WithCompleteData() {
        // Arrange & Act
        cashFlowForm.setId(UUID.randomUUID());
        cashFlowForm.setType("Inflow");
        cashFlowForm.setSource("Freelance");
        cashFlowForm.setLabel("Pendapatan Sampingan");
        cashFlowForm.setAmount(3000000L);
        cashFlowForm.setDescription("Project web development");

        // Assert
        assertNotNull(cashFlowForm.getId());
        assertEquals("Inflow", cashFlowForm.getType());
        assertEquals("Freelance", cashFlowForm.getSource());
        assertEquals("Pendapatan Sampingan", cashFlowForm.getLabel());
        assertEquals(3000000L, cashFlowForm.getAmount());
        assertEquals("Project web development", cashFlowForm.getDescription());
    }

    @Test
    @DisplayName("Form untuk Outflow dengan data lengkap")
    void form_ForOutflow_WithCompleteData() {
        // Arrange & Act
        cashFlowForm.setId(UUID.randomUUID());
        cashFlowForm.setType("Outflow");
        cashFlowForm.setSource("Transportasi");
        cashFlowForm.setLabel("Kebutuhan");
        cashFlowForm.setAmount(500000L);
        cashFlowForm.setDescription("Bensin dan tol bulan ini");

        // Assert
        assertNotNull(cashFlowForm.getId());
        assertEquals("Outflow", cashFlowForm.getType());
        assertEquals("Transportasi", cashFlowForm.getSource());
        assertEquals("Kebutuhan", cashFlowForm.getLabel());
        assertEquals(500000L, cashFlowForm.getAmount());
        assertEquals("Bensin dan tol bulan ini", cashFlowForm.getDescription());
    }

    @Test
    @DisplayName("ConfirmSource untuk delete operation")
    void confirmSource_ForDeleteOperation() {
        // Arrange
        String source = "Gaji";
        cashFlowForm.setSource(source);

        // Act
        cashFlowForm.setConfirmSource(source);

        // Assert
        assertEquals(source, cashFlowForm.getSource());
        assertEquals(source, cashFlowForm.getConfirmSource());
        assertEquals(cashFlowForm.getSource(), cashFlowForm.getConfirmSource());
    }

    @Test
    @DisplayName("ConfirmSource berbeda dengan source")
    void confirmSource_DifferentFromSource() {
        // Arrange
        cashFlowForm.setSource("Gaji");
        cashFlowForm.setConfirmSource("Belanja");

        // Assert
        assertNotEquals(cashFlowForm.getSource(), cashFlowForm.getConfirmSource());
    }
}