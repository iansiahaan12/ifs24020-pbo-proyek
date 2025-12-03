package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cashflows")
public class CashFlowController {
    
    private final CashFlowService cashFlowService;

    @Autowired
    protected AuthContext authContext;

    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    // Menambahkan cash flow baru
    // -------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createCashFlow(@RequestBody CashFlow reqCashFlow) {

        // Validasi input
        if (reqCashFlow.getType() == null || reqCashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data type tidak valid", null));
        } else if (reqCashFlow.getSource() == null || reqCashFlow.getSource().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data source tidak valid", null));
        } else if (reqCashFlow.getLabel() == null || reqCashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data label tidak valid", null));
        } else if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data amount tidak valid", null));
        } else if (reqCashFlow.getDescription() == null || reqCashFlow.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data description tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow newCashFlow = cashFlowService.createCashFlow(
            authUser.getId(), 
            reqCashFlow.getType(), 
            reqCashFlow.getSource(), 
            reqCashFlow.getLabel(), 
            reqCashFlow.getAmount(), 
            reqCashFlow.getDescription()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil menambahkan data",
                Map.of("id", newCashFlow.getId())));
    }

    // Mendapatkan semua cash flow dengan opsi pencarian
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<CashFlow>>>> getAllCashFlows(
            @RequestParam(required = false) String search) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("cash_flows", cashFlows)));
    }

    // Mendapatkan cash flow berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, CashFlow>>> getCashFlowById(@PathVariable UUID id) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow cashFlow = cashFlowService.getCashFlowById(authUser.getId(), id);
        if (cashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("cash_flow", cashFlow)));
    }

    // Mendapatkan semua label
    // -------------------------------
    @GetMapping("/labels")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getCashFlowLabels() {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<String> labels = cashFlowService.getAllLabels(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("labels", labels)));
    }

    // Memperbarui cash flow berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CashFlow>> updateCashFlow(@PathVariable UUID id, @RequestBody CashFlow reqCashFlow) {

        // Validasi input
        if (reqCashFlow.getType() == null || reqCashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data type tidak valid", null));
        } else if (!reqCashFlow.getType().equals("Inflow") && !reqCashFlow.getType().equals("Outflow")) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Type harus Inflow atau Outflow", null));
        } else if (reqCashFlow.getSource() == null || reqCashFlow.getSource().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data source tidak valid", null));
        } else if (reqCashFlow.getLabel() == null || reqCashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data label tidak valid", null));
        } else if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data amount tidak valid", null));
        } else if (reqCashFlow.getDescription() == null || reqCashFlow.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data description tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        CashFlow updatedCashFlow = cashFlowService.updateCashFlow(
            authUser.getId(), 
            id, 
            reqCashFlow.getType(), 
            reqCashFlow.getSource(), 
            reqCashFlow.getLabel(), 
            reqCashFlow.getAmount(), 
            reqCashFlow.getDescription()
        );

        if (updatedCashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil memperbarui data", null));
    }

    // Menghapus cash flow berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCashFlow(@PathVariable UUID id) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = cashFlowService.deleteCashFlow(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data cash flow tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data cash flow berhasil dihapus",
                null));
    }
}