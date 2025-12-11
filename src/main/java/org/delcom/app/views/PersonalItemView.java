package org.delcom.app.views;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.ItemImageForm;
import org.delcom.app.dto.PersonalItemForm;
import org.delcom.app.entities.PersonalItem;
import org.delcom.app.entities.User;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.PersonalItemService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
public class PersonalItemView {

    private final PersonalItemService personalItemService;
    private final FileStorageService fileStorageService;
    private final AuthContext authContext;

    public PersonalItemView(PersonalItemService personalItemService, 
                            FileStorageService fileStorageService,
                            AuthContext authContext) {
        this.personalItemService = personalItemService;
        this.fileStorageService = fileStorageService;
        this.authContext = authContext;
    }

    // ===========================
    // 1. LIST PAGE (GET /items)
    // ===========================
    @GetMapping
    public String showListPage(
            @RequestParam(required = false) String search, 
            Model model) {

        if (!authContext.isAuthenticated()) { return "redirect:/auth/login"; }
        User authUser = authContext.getAuthUser();

        // Ambil Data
        List<PersonalItem> items = personalItemService.getAllItems(authUser.getId(), search);
        if (items == null) items = List.of(); // Cegah Null

        // --- LOGIC STATISTIK (ANTI-CRASH) ---
        
        // A. Total Nilai
        double totalValue = items.stream()
                .filter(item -> item.getPrice() != null)
                .mapToDouble(PersonalItem::getPrice)
                .sum();

        // B. Total Item
        int totalItems = items.size();

        // C. Barang Terbaru (PENTING: Filter tanggal NULL agar tidak Error 500)
        List<PersonalItem> recentItems = items.stream()
                .filter(item -> item.getUpdatedAt() != null) // <--- PENJAGA GAWANG
                .sorted(Comparator.comparing(PersonalItem::getUpdatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Masukkan ke Model
        model.addAttribute("auth", authUser);
        model.addAttribute("items", items);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("recentItems", recentItems);
        model.addAttribute("search", search);

        // Form Kosong untuk Modal Tambah
        if (!model.containsAttribute("itemForm")) {
            model.addAttribute("itemForm", new PersonalItemForm());
        }

        // RETURN KE FILE HTML (Pastikan nama file di folder template benar!)
        // Sesuai diskusi sebelumnya, nama file kamu adalah list.html
        return "pages/personalitem/list"; 
    }

    // ===========================
    // 2. ADD ITEM
    // ===========================
    @PostMapping("/add")
    public String postAddItem(@Valid @ModelAttribute("itemForm") PersonalItemForm itemForm,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) { return "redirect:/auth/login"; }
        User authUser = authContext.getAuthUser();

        personalItemService.createItem(
                authUser.getId(),
                itemForm.getItemName(),
                itemForm.getCategory(),
                itemForm.getPrice(),
                itemForm.getCondition(),
                itemForm.getDescription(),
                null, 
                itemForm.getPurchaseDate(),
                itemForm.getStorageLocation()              
        );

        redirectAttributes.addFlashAttribute("success", "Barang berhasil ditambahkan.");
        return "redirect:/items";
    }

    // ===========================
    // 3. EDIT ITEM
    // ===========================
    @PostMapping("/edit")
    public String postEditItem(@Valid @ModelAttribute("itemForm") PersonalItemForm itemForm,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) { return "redirect:/auth/login"; }
        User authUser = authContext.getAuthUser();

        personalItemService.updateItem(
                authUser.getId(),
                itemForm.getId(),
                itemForm.getItemName(),
                itemForm.getCategory(),
                itemForm.getPrice(),
                itemForm.getCondition(),
                itemForm.getDescription(),
                null, 
                itemForm.getPurchaseDate(),    
                itemForm.getStorageLocation()  
        );

        redirectAttributes.addFlashAttribute("success", "Data barang berhasil diperbarui.");
        return "redirect:/items";
    }

    // ===========================
    // 4. DELETE ITEM (YANG SUDAH DIPERBAIKI)
    // ===========================
    @PostMapping("/delete")
    public String postDeleteItem(
            @RequestParam("id") UUID id, // Ambil ID langsung
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) { return "redirect:/auth/login"; }
        User authUser = authContext.getAuthUser();

        boolean deleted = personalItemService.deleteItem(authUser.getId(), id);

        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Barang berhasil dihapus.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus barang.");
        }
        
        return "redirect:/items";
    }

    // ===========================
    // 5. DETAIL PAGE
    // ===========================
    @GetMapping("/{itemId}")
    public String getDetailItem(@PathVariable UUID itemId, Model model) {
        if (!authContext.isAuthenticated()) { return "redirect:/auth/login"; }
        User authUser = authContext.getAuthUser();

        PersonalItem item = personalItemService.getItemById(authUser.getId(), itemId);
        if (item == null) {
            return "redirect:/items";
        }

        model.addAttribute("auth", authUser);
        model.addAttribute("item", item);

        ItemImageForm itemImageForm = new ItemImageForm();
        itemImageForm.setId(itemId);
        model.addAttribute("itemImageForm", itemImageForm);

        return "pages/personalitem/detail"; 
    }

    // ===========================
    // 6. UPLOAD IMAGE
    // ===========================
    @PostMapping("/edit-image")
    public String postEditItemImage(
            @Valid @ModelAttribute("itemImageForm") ItemImageForm itemImageForm,
            RedirectAttributes redirectAttributes) {

        if (!authContext.isAuthenticated()) { return "redirect:/auth/login"; }
        
        if (itemImageForm.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File gambar tidak boleh kosong");
            return "redirect:/items/" + itemImageForm.getId();
        }

        try {
            String fileName = fileStorageService.storeFile(
                    itemImageForm.getImageFile(),
                    itemImageForm.getId()
            );

            personalItemService.updateImagePathOnly(itemImageForm.getId(), fileName);

            redirectAttributes.addFlashAttribute("success", "Gambar barang berhasil diupload");
            return "redirect:/items/" + itemImageForm.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload gambar");
            return "redirect:/items/" + itemImageForm.getId();
        }
    }

    // ===========================
    // 7. SERVE IMAGE
    // ===========================
    @GetMapping("/image/{filename:.+}")
    @ResponseBody
    public Resource getImageByFilename(@PathVariable String filename) {
        try {
            Path file = fileStorageService.loadFile(filename);
            return new UrlResource(file.toUri());
        } catch (Exception e) {
            return null;
        }
    }
}