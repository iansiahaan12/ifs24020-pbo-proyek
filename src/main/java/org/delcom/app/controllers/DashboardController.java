package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.PersonalItem;
import org.delcom.app.entities.User;
import org.delcom.app.services.PersonalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private AuthContext authContext;

    @Autowired
    private PersonalItemService personalItemService;

    @GetMapping
    public String dashboard(Model model) {
        // 1. Cek Login
        if (!authContext.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        User authUser = authContext.getAuthUser();

        // 2. Ambil Data untuk Statistik
        List<PersonalItem> items = personalItemService.getAllItems(authUser.getId(), null);
        if (items == null) items = List.of();

        // Hitung Total Aset
        double totalValue = items.stream()
                .filter(item -> item.getPrice() != null)
                .mapToDouble(PersonalItem::getPrice)
                .sum();

        // Hitung Jumlah Barang
        int totalItems = items.size();

        // Ambil 3 Barang Terbaru (Untuk widget "Baru Ditambahkan")
        List<PersonalItem> recentItems = items.stream()
                .filter(item -> item.getUpdatedAt() != null)
                .sorted(Comparator.comparing(PersonalItem::getUpdatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());

        // 3. Masukkan ke Model
        model.addAttribute("auth", authUser);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("recentItems", recentItems);

        // 4. Return View Dashboard
        // Pastikan kamu punya file HTML untuk dashboard, misal: home.html
        return "pages/home"; 
    }
}