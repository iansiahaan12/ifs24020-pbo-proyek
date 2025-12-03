package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class CashFlowControllerTests {
    
    @Test
    @DisplayName("Pengujian untuk controller CashFlow")
    void testCashFlowController() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();

        // Membuat dummy data
        CashFlow cashFlow = new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 5000000L, "Gaji bulan November");
        cashFlow.setId(cashFlowId);

        // Membuat mock Service
        CashFlowService cashFlowService = Mockito.mock(CashFlowService.class);

        // Atur perilaku mock
        when(cashFlowService.createCashFlow(
            any(UUID.class), 
            any(String.class), 
            any(String.class), 
            any(String.class), 
            any(Long.class), 
            any(String.class)
        )).thenReturn(cashFlow);

        // Membuat instance controller
        CashFlowController cashFlowController = new CashFlowController(cashFlowService);
        assert (cashFlowController != null);

        cashFlowController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Menguji method createCashFlow
        {
            // Data tidak valid
            {
                List<CashFlow> invalidCashFlows = List.of(
                    // Type Null
                    new CashFlow(userId, null, "Gaji", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Type Kosong
                    new CashFlow(userId, "", "Gaji", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Type Invalid
                    new CashFlow(userId, "InvalidType", "Gaji", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Source Null
                    new CashFlow(userId, "Inflow", null, "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Source Kosong
                    new CashFlow(userId, "Inflow", "", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Label Null
                    new CashFlow(userId, "Inflow", "Gaji", null, 5000000L, "Deskripsi valid"),
                    // Label Kosong
                    new CashFlow(userId, "Inflow", "Gaji", "", 5000000L, "Deskripsi valid"),
                    // Amount Null
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", null, "Deskripsi valid"),
                    // Amount Zero
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 0L, "Deskripsi valid"),
                    // Amount Negative
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", -1000L, "Deskripsi valid"),
                    // Description Null
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 5000000L, null),
                    // Description Kosong
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 5000000L, "")
                );

                ResponseEntity<ApiResponse<Map<String, UUID>>> result;
                for (CashFlow itemCashFlow : invalidCashFlows) {
                    result = cashFlowController.createCashFlow(itemCashFlow);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk menambahkan cashflow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.createCashFlow(cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil menambahkan cashflow
            {
                cashFlowController.authContext.setAuthUser(authUser);
                var result = cashFlowController.createCashFlow(cashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getAllCashFlows
        {
            // Tidak terautentikasi untuk getAllCashFlows
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji getAllCashFlows dengan search null
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<CashFlow> dummyResponse = List.of(cashFlow);
                when(cashFlowService.getAllCashFlows(any(UUID.class), any(String.class)))
                    .thenReturn(dummyResponse);
                
                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // Menguji getAllCashFlows dengan search parameter
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<CashFlow> dummyResponse = List.of(cashFlow);
                when(cashFlowService.getAllCashFlows(any(UUID.class), any(String.class)))
                    .thenReturn(dummyResponse);
                
                var result = cashFlowController.getAllCashFlows("gaji");
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getCashFlowById
        {
            // Tidak terautentikasi untuk getCashFlowById
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji getCashFlowById dengan ID yang ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class)))
                    .thenReturn(cashFlow);
                
                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("cash_flow").getId().equals(cashFlowId));
            }

            // Menguji getCashFlowById dengan ID yang tidak ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class)))
                    .thenReturn(null);
                
                var result = cashFlowController.getCashFlowById(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Menguji method getCashFlowLabels
        {
            // Tidak terautentikasi untuk getCashFlowLabels
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getCashFlowLabels();
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil mendapatkan labels
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<String> dummyLabels = List.of("gaji-bulanan", "belanja-harian");
                when(cashFlowService.getAllLabels(any(UUID.class)))
                    .thenReturn(dummyLabels);
                
                var result = cashFlowController.getCashFlowLabels();
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("labels").size() == 2);
            }
        }

        // Menguji method updateCashFlow
        {
            // Data tidak valid
            {
                List<CashFlow> invalidCashFlows = List.of(
                    // Type Null
                    new CashFlow(userId, null, "Gaji", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Type Kosong
                    new CashFlow(userId, "", "Gaji", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Type Invalid
                    new CashFlow(userId, "InvalidType", "Gaji", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Source Null
                    new CashFlow(userId, "Inflow", null, "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Source Kosong
                    new CashFlow(userId, "Inflow", "", "gaji-bulanan", 5000000L, "Deskripsi valid"),
                    // Label Null
                    new CashFlow(userId, "Inflow", "Gaji", null, 5000000L, "Deskripsi valid"),
                    // Label Kosong
                    new CashFlow(userId, "Inflow", "Gaji", "", 5000000L, "Deskripsi valid"),
                    // Amount Null
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", null, "Deskripsi valid"),
                    // Amount Zero
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 0L, "Deskripsi valid"),
                    // Description Null
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 5000000L, null),
                    // Description Kosong
                    new CashFlow(userId, "Inflow", "Gaji", "gaji-bulanan", 5000000L, "")
                );

                for (CashFlow itemCashFlow : invalidCashFlows) {
                    var result = cashFlowController.updateCashFlow(cashFlowId, itemCashFlow);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk updateCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.updateCashFlow(cashFlowId, cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Memperbarui cashflow dengan ID tidak ada
            {
                when(cashFlowService.updateCashFlow(
                    any(UUID.class), 
                    any(UUID.class), 
                    any(String.class), 
                    any(String.class), 
                    any(String.class), 
                    any(Long.class), 
                    any(String.class)
                )).thenReturn(null);
                
                CashFlow updatedCashFlow = new CashFlow(
                    userId, 
                    "Outflow", 
                    "Cash", 
                    "belanja-bulanan", 
                    1000000L, 
                    "Belanja kebutuhan"
                );
                updatedCashFlow.setId(nonexistentCashFlowId);

                var result = cashFlowController.updateCashFlow(nonexistentCashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Memperbarui cashflow dengan ID ada
            {
                CashFlow updatedCashFlow = new CashFlow(
                    userId, 
                    "Outflow", 
                    "Cash", 
                    "belanja-bulanan", 
                    1000000L, 
                    "Belanja kebutuhan updated"
                );
                updatedCashFlow.setId(cashFlowId);
                
                when(cashFlowService.updateCashFlow(
                    any(UUID.class), 
                    any(UUID.class), 
                    any(String.class), 
                    any(String.class), 
                    any(String.class), 
                    any(Long.class), 
                    any(String.class)
                )).thenReturn(updatedCashFlow);

                var result = cashFlowController.updateCashFlow(cashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method deleteCashFlow
        {
            // Tidak terautentikasi untuk deleteCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji deleteCashFlow dengan ID yang tidak ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class)))
                    .thenReturn(false);
                
                var result = cashFlowController.deleteCashFlow(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji deleteCashFlow dengan ID yang ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class)))
                    .thenReturn(true);
                
                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }
    }
}