package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CashFlowService {

    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    // Membuat cash flow baru
    public CashFlow createCashFlow(UUID userId, String type, String source, String label, Long amount, String description) {
        CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);
        return cashFlowRepository.save(cashFlow);
    }

    // Mendapatkan semua cash flow berdasarkan user ID dengan opsi pencarian
    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search != null && !search.isEmpty()) {
            return cashFlowRepository.findByUserIdWithSearch(userId, search);
        }
        return cashFlowRepository.findByUserId(userId);
    }

    // Mendapatkan cash flow berdasarkan ID
    public CashFlow getCashFlowById(UUID userId, UUID id) {
        return cashFlowRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    // Mendapatkan semua label unik
    public List<String> getAllLabels(UUID userId) {
        return cashFlowRepository.findDistinctLabelsByUserId(userId);
    }

    // Memperbarui cash flow
    public CashFlow updateCashFlow(UUID userId, UUID id, String type, String source, String label, Long amount, String description) {
        CashFlow existingCashFlow = cashFlowRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existingCashFlow == null) {
            return null;
        }

        existingCashFlow.setType(type);
        existingCashFlow.setSource(source);
        existingCashFlow.setLabel(label);
        existingCashFlow.setAmount(amount);
        existingCashFlow.setDescription(description);

        return cashFlowRepository.save(existingCashFlow);
    }

    // ==========================
    // Method untuk update cover
    // ==========================
    public void updateCover(UUID id, String fileName) {
        CashFlow cashFlow = cashFlowRepository.findById(id).orElse(null);
        if (cashFlow != null) {
            cashFlow.setCover(fileName);
            cashFlowRepository.save(cashFlow);
        }
    }

    // Menghapus cash flow
    @Transactional
    public boolean deleteCashFlow(UUID userId, UUID id) {
        CashFlow existingCashFlow = cashFlowRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existingCashFlow == null) {
            return false;
        }

        cashFlowRepository.deleteByIdAndUserId(id, userId);
        return true;
    }
}
