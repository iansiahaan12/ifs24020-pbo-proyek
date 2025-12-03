package org.delcom.app.entities;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CashFlowTests {
    
    @Test
    @DisplayName("Membuat instance dari kelas CashFlow")
    void testMembuatInstanceCashFlow() throws Exception {
        UUID userId = UUID.randomUUID();

        // CashFlow tipe Inflow
        {
            CashFlow cashFlow = new CashFlow(
                userId, 
                "Inflow", 
                "Gaji", 
                "Pendapatan", 
                5000000L, 
                "Gaji bulan Januari"
            );

            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("Inflow"));
            assert (cashFlow.getSource().equals("Gaji"));
            assert (cashFlow.getLabel().equals("Pendapatan"));
            assert (cashFlow.getAmount().equals(5000000L));
            assert (cashFlow.getDescription().equals("Gaji bulan Januari"));
        }

        // CashFlow tipe Outflow
        {
            CashFlow cashFlow = new CashFlow(
                userId, 
                "Outflow", 
                "Belanja", 
                "Kebutuhan", 
                500000L, 
                "Belanja bulanan"
            );

            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("Outflow"));
            assert (cashFlow.getSource().equals("Belanja"));
            assert (cashFlow.getLabel().equals("Kebutuhan"));
            assert (cashFlow.getAmount().equals(500000L));
            assert (cashFlow.getDescription().equals("Belanja bulanan"));
        }

        // CashFlow dengan nilai default
        {
            CashFlow cashFlow = new CashFlow();

            assert (cashFlow.getId() == null);
            assert (cashFlow.getUserId() == null);
            assert (cashFlow.getType() == null);
            assert (cashFlow.getSource() == null);
            assert (cashFlow.getLabel() == null);
            assert (cashFlow.getAmount() == null);
            assert (cashFlow.getDescription() == null);
            assert (cashFlow.getCover() == null);
        }

        // CashFlow dengan setNilai
        {
            CashFlow cashFlow = new CashFlow();
            UUID generatedId = UUID.randomUUID();
            
            cashFlow.setId(generatedId);
            cashFlow.setUserId(userId);
            cashFlow.setType("Inflow");
            cashFlow.setSource("Investasi");
            cashFlow.setLabel("Passive Income");
            cashFlow.setAmount(1000000L);
            cashFlow.setDescription("Dividen saham");
            cashFlow.setCover("/cashflow-cover.png");
            cashFlow.onCreate();
            cashFlow.onUpdate();

            assert (cashFlow.getId().equals(generatedId));
            assert (cashFlow.getUserId().equals(userId));
            assert (cashFlow.getType().equals("Inflow"));
            assert (cashFlow.getSource().equals("Investasi"));
            assert (cashFlow.getLabel().equals("Passive Income"));
            assert (cashFlow.getAmount().equals(1000000L));
            assert (cashFlow.getDescription().equals("Dividen saham"));
            assert (cashFlow.getCover().equals("/cashflow-cover.png"));
            assert (cashFlow.getCreatedAt() != null);
            assert (cashFlow.getUpdatedAt() != null);
        }
    }
}