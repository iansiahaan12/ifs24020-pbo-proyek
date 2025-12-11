package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.delcom.app.interceptors.SessionInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTests {

    @Mock
    private AuthInterceptor authInterceptor;

    @Mock
    private SessionInterceptor sessionInterceptor;

    @Mock
    private InterceptorRegistry registry;

    @Mock
    private InterceptorRegistration authRegistration;

    @Mock
    private InterceptorRegistration sessionRegistration;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

@Test
    @DisplayName("addInterceptors harus mendaftarkan AuthInterceptor dan SessionInterceptor dengan path yang benar")
    void testAddInterceptors() {
        // --- 1. ARRANGE ---
        when(registry.addInterceptor(authInterceptor)).thenReturn(authRegistration);
        when(authRegistration.addPathPatterns(anyString())).thenReturn(authRegistration);
        // Kita gunakan lenient() agar Mockito tidak strict jika dipanggil berkali-kali dengan argumen varargs
        lenient().when(authRegistration.excludePathPatterns(any(String[].class))).thenReturn(authRegistration);

        when(registry.addInterceptor(sessionInterceptor)).thenReturn(sessionRegistration);
        when(sessionRegistration.addPathPatterns(anyString())).thenReturn(sessionRegistration);
        when(sessionRegistration.excludePathPatterns(any(String[].class))).thenReturn(sessionRegistration);

        // --- 2. ACT ---
        webMvcConfig.addInterceptors(registry);

        // --- 3. ASSERT / VERIFY ---

        // A. Verifikasi AuthInterceptor (API JWT)
        InOrder inOrderAuth = inOrder(registry, authRegistration);
        inOrderAuth.verify(registry).addInterceptor(authInterceptor);
        inOrderAuth.verify(authRegistration).addPathPatterns("/api/**");
        
        // --- PERBAIKAN DI SINI ---
        // Verifikasi dilakukan terpisah sesuai implementasi di WebMvcConfig.java
        inOrderAuth.verify(authRegistration).excludePathPatterns("/api/auth/**");
        inOrderAuth.verify(authRegistration).excludePathPatterns("/api/public/**");

        // B. Verifikasi SessionInterceptor (Web/HTML)
        InOrder inOrderSession = inOrder(registry, sessionRegistration);
        inOrderSession.verify(registry).addInterceptor(sessionInterceptor);
        inOrderSession.verify(sessionRegistration).addPathPatterns("/**");
        
        // Sesuaikan juga ini jika di WebMvcConfig dipanggil terpisah, 
        // tapi jika dipanggil sekaligus (varargs), gunakan kode di bawah ini:
        inOrderSession.verify(sessionRegistration).excludePathPatterns("/api/**", "/css/**", "/js/**", "/images/**");
        
        verifyNoMoreInteractions(registry, authRegistration, sessionRegistration);
    }
}