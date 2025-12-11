package org.delcom.app.controllers;

import jakarta.validation.Valid;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    protected AuthContext authContext;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // 1. HALAMAN LOGIN
    // ==========================================
    @GetMapping("/login")
    public String login(Model model) {
        // Jika user sudah login, redirect ke dashboard
        if (authContext.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("loginForm", new LoginForm());
        return ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN; // "pages/auth/login"
    }

    // ==========================================
    // 2. HALAMAN REGISTER
    // ==========================================
    @GetMapping("/register")
    public String register(Model model) {
        if (authContext.isAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("registerForm", new RegisterForm());
        return ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER; // "pages/auth/register"
    }

    // ==========================================
    // 3. PROSES REGISTER (POST)
    // ==========================================
    @PostMapping("/register/post")
    public String registerPost(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        // 1. Validasi standar (NotBlank, Email format, dll)
        if (bindingResult.hasErrors()) {
            return ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER;
        }

        // 2. Cek apakah email sudah terdaftar
        User existingUser = userService.getUserByEmail(registerForm.getEmail());
        if (existingUser != null) {
            bindingResult.rejectValue("email", "error.email", "Email sudah terdaftar!");
            return ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER;
        }

        // 3. Simpan User Baru
        String encodedPassword = passwordEncoder.encode(registerForm.getPassword());
        userService.createUser(
                registerForm.getName(),
                registerForm.getEmail(),
                encodedPassword
        );

        // 4. Redirect ke Login dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "Registrasi berhasil! Silakan login.");
        return "redirect:/auth/login";
    }
}