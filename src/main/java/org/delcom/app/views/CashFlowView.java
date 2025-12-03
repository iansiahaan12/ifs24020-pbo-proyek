package org.delcom.app.views;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.dto.CoverCashFlowForm;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.services.FileStorageService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/cashflows")
public class CashFlowView {

    private final CashFlowService cashFlowService;
    private final FileStorageService fileStorageService;

    // Constructor lengkap
    public CashFlowView(CashFlowService cashFlowService, FileStorageService fileStorageService) {
        this.cashFlowService = cashFlowService;
        this.fileStorageService = fileStorageService;
    }

    // ===========================
    // ADD CASHFLOW
    // ===========================
    @PostMapping("/add")
    public String postAddCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm cashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        // Validasi
        if (cashFlowForm.getType() == null || cashFlowForm.getType().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Tipe tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        if (!cashFlowForm.getType().equals("Inflow") && !cashFlowForm.getType().equals("Outflow")) {
            redirectAttributes.addFlashAttribute("error", "Tipe harus Inflow atau Outflow");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        if (cashFlowForm.getSource() == null || cashFlowForm.getSource().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Sumber tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        if (cashFlowForm.getLabel() == null || cashFlowForm.getLabel().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Label tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        if (cashFlowForm.getAmount() == null || cashFlowForm.getAmount() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Jumlah harus lebih dari 0");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        if (cashFlowForm.getDescription() == null || cashFlowForm.getDescription().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Deskripsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        var entity = cashFlowService.createCashFlow(
                authUser.getId(),
                cashFlowForm.getType(),
                cashFlowForm.getSource(),
                cashFlowForm.getLabel(),
                cashFlowForm.getAmount(),
                cashFlowForm.getDescription());

        if (entity == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan cash flow");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        redirectAttributes.addFlashAttribute("success", "Cash flow berhasil ditambahkan.");
        return "redirect:/cashflows";
    }

    // ===========================
    // EDIT CASHFLOW
    // ===========================
    @PostMapping("/edit")
    public String postEditCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm cashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (cashFlowForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID cash flow tidak valid");
            redirectAttributes.addFlashAttribute("editCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        var updated = cashFlowService.updateCashFlow(
                authUser.getId(),
                cashFlowForm.getId(),
                cashFlowForm.getType(),
                cashFlowForm.getSource(),
                cashFlowForm.getLabel(),
                cashFlowForm.getAmount(),
                cashFlowForm.getDescription());

        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui cash flow");
            redirectAttributes.addFlashAttribute("editCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        redirectAttributes.addFlashAttribute("success", "Cash flow berhasil diperbarui.");
        return "redirect:/cashflows";
    }

    // ===========================
    // DELETE CASHFLOW
    // ===========================
    @PostMapping("/delete")
    public String postDeleteCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm cashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (cashFlowForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID cash flow tidak valid");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        CashFlow existingCashFlow = cashFlowService.getCashFlowById(authUser.getId(), cashFlowForm.getId());
        if (existingCashFlow == null) {
            redirectAttributes.addFlashAttribute("error", "Cash flow tidak ditemukan");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        boolean deleted = cashFlowService.deleteCashFlow(
                authUser.getId(),
                cashFlowForm.getId());

        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus cash flow");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            return "redirect:/cashflows";
        }

        redirectAttributes.addFlashAttribute("success", "Cash flow berhasil dihapus.");
        return "redirect:/cashflows";
    }

    // ===========================
    // DETAIL (FIXED!)
    // ===========================
    @GetMapping("/{cashFlowId}")
    public String getDetailCashFlow(@PathVariable UUID cashFlowId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        model.addAttribute("auth", authUser);

        CashFlow cashFlow = cashFlowService.getCashFlowById(authUser.getId(), cashFlowId);
        if (cashFlow == null) {
            return "redirect:/cashflows";
        }

        model.addAttribute("cashFlow", cashFlow);


        CoverCashFlowForm coverCashFlowForm = new CoverCashFlowForm();
        coverCashFlowForm.setId(cashFlowId);
        model.addAttribute("coverCashFlowForm", coverCashFlowForm);

        return "pages/cashflows/detail";
    }

    // =====================================================
    // UPLOAD COVER
    // =====================================================
    @PostMapping("/edit-cover")
    public String postEditCoverCashFlow(
            @Valid @ModelAttribute("coverCashFlowForm") CoverCashFlowForm coverCashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (coverCashFlowForm.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File foto tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editCoverCashFlowModalOpen", true);
            return "redirect:/cashflows/" + coverCashFlowForm.getId();
        }

        CashFlow cashFlow = cashFlowService.getCashFlowById(authUser.getId(), coverCashFlowForm.getId());
        if (cashFlow == null) {
            redirectAttributes.addFlashAttribute("error", "Cash flow tidak ditemukan");
            return "redirect:/cashflows";
        }

        if (!coverCashFlowForm.isValidImage()) {
            redirectAttributes.addFlashAttribute("error", "Format file tidak didukung. Gunakan JPG, PNG, GIF, atau WEBP");
            redirectAttributes.addFlashAttribute("editCoverCashFlowModalOpen", true);
            return "redirect:/cashflows/" + coverCashFlowForm.getId();
        }

        if (!coverCashFlowForm.isSizeValid(5 * 1024 * 1024)) {
            redirectAttributes.addFlashAttribute("error", "Ukuran file terlalu besar. Maksimal 5MB");
            redirectAttributes.addFlashAttribute("editCoverCashFlowModalOpen", true);
            return "redirect:/cashflows/" + coverCashFlowForm.getId();
        }

        try {
            String fileName = fileStorageService.storeFile(
                    coverCashFlowForm.getCoverFile(),
                    coverCashFlowForm.getId()
            );

            cashFlowService.updateCover(coverCashFlowForm.getId(), fileName);

            redirectAttributes.addFlashAttribute("success", "Foto bukti berhasil diupload");
            return "redirect:/cashflows/" + coverCashFlowForm.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload foto");
            redirectAttributes.addFlashAttribute("editCoverCashFlowModalOpen", true);
            return "redirect:/cashflows/" + coverCashFlowForm.getId();
        }
    }

    // =====================================================
    // SERVE COVER FILE
    // =====================================================
    @GetMapping("/cover/{filename:.+}")
    @ResponseBody
    public Resource getCoverByFilename(@PathVariable String filename) {
        try {
            Path file = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (Exception ignored) {}

        return null;
    }
}