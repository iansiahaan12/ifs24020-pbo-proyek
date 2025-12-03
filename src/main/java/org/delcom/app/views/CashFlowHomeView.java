package org.delcom.app.views;

import java.util.List;
import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cashflows")
public class CashFlowHomeView {

    private final CashFlowService cashFlowService;

    public CashFlowHomeView(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String search) {
        // Autentikasi
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/login";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/login";
        }
        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        // Data cashflows
        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search != null ? search : "");
        model.addAttribute("cashFlows", cashFlows);

        // Hitung summary
        long totalInflow = cashFlows.stream()
                .filter(cf -> "Inflow".equals(cf.getType()))
                .mapToLong(CashFlow::getAmount)
                .sum();
        long totalOutflow = cashFlows.stream()
                .filter(cf -> "Outflow".equals(cf.getType()))
                .mapToLong(CashFlow::getAmount)
                .sum();

        model.addAttribute("totalInflow", totalInflow);
        model.addAttribute("totalOutflow", totalOutflow);
        model.addAttribute("balance", totalInflow - totalOutflow);
        model.addAttribute("cashFlowForm", new CashFlowForm());

        return ConstUtil.TEMPLATE_PAGES_CASHFLOWS_HOME;
    }
}