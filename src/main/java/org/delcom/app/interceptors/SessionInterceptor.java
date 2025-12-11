package org.delcom.app.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthContext authContext;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Cek apakah AuthContext masih kosong?
        if (!authContext.isAuthenticated()) {
            
            // 2. Ambil Authentication dari Spring Security (Session)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 3. Jika user login via browser (Session valid)
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                
                // 4. Ambil email dari session
                String email = authentication.getName();
                
                // 5. Cari user di DB dan masukkan ke AuthContext
                User user = userService.getUserByEmail(email);
                if (user != null) {
                    authContext.setAuthUser(user);
                }
            }
        }
        return true; // Lanjut ke controller
    }
}