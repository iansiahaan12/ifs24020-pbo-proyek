    package org.delcom.app.services;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.doNothing;
    import static org.mockito.Mockito.when;

    import java.util.Arrays;
    import java.util.List;
    import java.util.UUID;

    import org.delcom.app.entities.CashFlow;
    import org.delcom.app.repositories.CashFlowRepository;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.mockito.Mockito;

    public class CashFlowServiceTests {
        
        @Test
        @DisplayName("Pengujian untuk service CashFlow")
        void testCashFlowService() throws Exception {
            // Buat random UUID
            UUID userId = UUID.randomUUID();
            UUID cashFlowId = UUID.randomUUID();
            UUID nonexistentCashFlowId = UUID.randomUUID();

            // Membuat dummy data
            CashFlow cashFlow = new CashFlow(
                userId, 
                "Inflow", 
                "Gaji", 
                "Pendapatan", 
                5000000L, 
                "Gaji bulan Januari"
            );
            cashFlow.setId(cashFlowId);

            // Membuat mock CashFlowRepository
            CashFlowRepository cashFlowRepository = Mockito.mock(CashFlowRepository.class);

            // Atur perilaku mock
            when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);
            when(cashFlowRepository.findByUserId(userId)).thenReturn(List.of(cashFlow));
            when(cashFlowRepository.findByUserIdWithSearch(userId, "Gaji")).thenReturn(List.of(cashFlow));
            when(cashFlowRepository.findByIdAndUserId(cashFlowId, userId)).thenReturn(java.util.Optional.of(cashFlow));
            when(cashFlowRepository.findByIdAndUserId(nonexistentCashFlowId, userId)).thenReturn(java.util.Optional.empty());
            when(cashFlowRepository.findById(cashFlowId)).thenReturn(java.util.Optional.of(cashFlow));
            when(cashFlowRepository.findById(nonexistentCashFlowId)).thenReturn(java.util.Optional.empty());
            when(cashFlowRepository.findDistinctLabelsByUserId(userId)).thenReturn(Arrays.asList("Pendapatan", "Kebutuhan", "Hiburan"));
            doNothing().when(cashFlowRepository).deleteByIdAndUserId(any(UUID.class), any(UUID.class));

            // Membuat instance service
            CashFlowService cashFlowService = new CashFlowService(cashFlowRepository);
            assert (cashFlowService != null);

            // Menguji createCashFlow
            {
                CashFlow createdCashFlow = cashFlowService.createCashFlow(
                    userId, 
                    cashFlow.getType(), 
                    cashFlow.getSource(), 
                    cashFlow.getLabel(), 
                    cashFlow.getAmount(), 
                    cashFlow.getDescription()
                );
                assert (createdCashFlow != null);
                assert (createdCashFlow.getId().equals(cashFlowId));
                assert (createdCashFlow.getType().equals(cashFlow.getType()));
                assert (createdCashFlow.getSource().equals(cashFlow.getSource()));
                assert (createdCashFlow.getLabel().equals(cashFlow.getLabel()));
                assert (createdCashFlow.getAmount().equals(cashFlow.getAmount()));
                assert (createdCashFlow.getDescription().equals(cashFlow.getDescription()));
            }

            // Menguji getAllCashFlows tanpa pencarian
            {
                List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(userId, null);
                assert (cashFlows.size() == 1);
                assert (cashFlows.get(0).getId().equals(cashFlowId));
            }

            // Menguji getAllCashFlows dengan pencarian
            {
                List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(userId, "Gaji");
                assert (cashFlows.size() == 1);
                assert (cashFlows.get(0).getSource().equals("Gaji"));
            }

            // Menguji getAllCashFlows dengan pencarian kosong
            {
                List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(userId, "");
                assert (cashFlows.size() == 1);
            }

            // Menguji getCashFlowById
            {
                CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, cashFlowId);
                assert (fetchedCashFlow != null);
                assert (fetchedCashFlow.getId().equals(cashFlowId));
                assert (fetchedCashFlow.getType().equals(cashFlow.getType()));
                assert (fetchedCashFlow.getSource().equals(cashFlow.getSource()));
                assert (fetchedCashFlow.getLabel().equals(cashFlow.getLabel()));
                assert (fetchedCashFlow.getAmount().equals(cashFlow.getAmount()));
            }

            // Menguji getCashFlowById dengan ID yang tidak ada
            {
                CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, nonexistentCashFlowId);
                assert (fetchedCashFlow == null);
            }

            // Menguji getAllLabels
            {
                List<String> labels = cashFlowService.getAllLabels(userId);
                assert (labels.size() == 3);
                assert (labels.contains("Pendapatan"));
                assert (labels.contains("Kebutuhan"));
                assert (labels.contains("Hiburan"));
            }

            // Menguji updateCashFlow
            {
                String updatedType = "Outflow";
                String updatedSource = "Belanja";
                String updatedLabel = "Kebutuhan";
                Long updatedAmount = 1000000L;
                String updatedDescription = "Belanja bulanan";

                CashFlow updatedCashFlow = cashFlowService.updateCashFlow(
                    userId, 
                    cashFlowId, 
                    updatedType, 
                    updatedSource, 
                    updatedLabel, 
                    updatedAmount, 
                    updatedDescription
                );
                assert (updatedCashFlow != null);
                assert (updatedCashFlow.getType().equals(updatedType));
                assert (updatedCashFlow.getSource().equals(updatedSource));
                assert (updatedCashFlow.getLabel().equals(updatedLabel));
                assert (updatedCashFlow.getAmount().equals(updatedAmount));
                assert (updatedCashFlow.getDescription().equals(updatedDescription));
            }

            // Menguji updateCashFlow dengan ID yang tidak ada
            {
                String updatedType = "Outflow";
                String updatedSource = "Belanja";
                String updatedLabel = "Kebutuhan";
                Long updatedAmount = 1000000L;
                String updatedDescription = "Belanja bulanan";

                CashFlow updatedCashFlow = cashFlowService.updateCashFlow(
                    userId, 
                    nonexistentCashFlowId, 
                    updatedType, 
                    updatedSource, 
                    updatedLabel, 
                    updatedAmount, 
                    updatedDescription
                );
                assert (updatedCashFlow == null);
            }

            // Menguji method updateCover dengan cashFlow yang tidak ada
            {
                UUID newCashFlowId = UUID.randomUUID();
                when(cashFlowRepository.findById(newCashFlowId)).thenReturn(java.util.Optional.empty());
                
                cashFlowService.updateCover(newCashFlowId, "cover1.png");
                // Tidak ada assertion karena method void, hanya memastikan tidak error
            }

            // Menguji method updateCover dengan cashFlow yang ada
            {
                String newCoverFilename = "cashflow-cover.png";
                
                when(cashFlowRepository.findById(cashFlowId)).thenReturn(java.util.Optional.of(cashFlow));
                when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);

                cashFlow.setCover(null);
                cashFlowService.updateCover(cashFlowId, newCoverFilename);
                assert (cashFlow.getCover().equals(newCoverFilename));
            }

            // Menguji method updateCover dengan sebelumnya sudah ada cover
            {
                String newCoverFilename = "cashflow-cover2.png";
                
                when(cashFlowRepository.findById(cashFlowId)).thenReturn(java.util.Optional.of(cashFlow));
                when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);

                cashFlow.setCover("old-cover.png");
                cashFlowService.updateCover(cashFlowId, newCoverFilename);
                assert (cashFlow.getCover().equals(newCoverFilename));
            }

            // Menguji deleteCashFlow
            {
                boolean deleted = cashFlowService.deleteCashFlow(userId, cashFlowId);
                assert (deleted == true);
            }

            // Menguji deleteCashFlow dengan ID yang tidak ada
            {
                boolean deleted = cashFlowService.deleteCashFlow(userId, nonexistentCashFlowId);
                assert (deleted == false);
            }
        }
    }