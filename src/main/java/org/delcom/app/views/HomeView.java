package org.delcom.app.views;

import org.delcom.app.entities.PersonalItem;
import org.delcom.app.entities.User;
import org.delcom.app.services.PersonalItemService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeView {

    // 1. Inject Service
    private final PersonalItemService personalItemService;

    public HomeView(PersonalItemService personalItemService) {
        this.personalItemService = personalItemService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;
        
        // 2. Kirim Data User
        model.addAttribute("auth", authUser);
        model.addAttribute("hideNavbar", true); // Sembunyikan Navbar menu

        // 3. HITUNG STATISTIK (Agar Dashboard tidak kosong)
        List<PersonalItem> items = personalItemService.getAllItems(authUser.getId(), null);
        
        int totalItems = items.size();
        double totalValue = items.stream().mapToDouble(PersonalItem::getPrice).sum();
        
        // Ambil 3 barang terakhir untuk ditampilkan (Recent Items)
        // Kita reverse listnya (cara simpel) atau pakai query database (cara advanced). 
        // Ini cara simpel Java:
        List<PersonalItem> recentItems = items.size() > 3 ? items.subList(items.size() - 3, items.size()) : items;

        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("recentItems", recentItems);

        return ConstUtil.TEMPLATE_PAGES_HOME;
    }
}