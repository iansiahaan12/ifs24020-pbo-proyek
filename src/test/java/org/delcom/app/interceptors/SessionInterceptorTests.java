package org.delcom.app.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionInterceptorTests {

    @Mock
    private AuthContext authContext;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private SessionInterceptor sessionInterceptor;

    // ==========================================
    // 1. Coverage Baris 26 (Kuning -> Hijau)
    // Skenario: AuthContext SUDAH terautentikasi (Skip semua logika di bawahnya)
    // ==========================================
    @Test
    @DisplayName("PreHandle: Sudah login (AuthContext True) -> Skip Logic")
    void testPreHandle_AlreadyAuthenticated() throws Exception {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(true);

        // Act
        boolean result = sessionInterceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
        // Pastikan tidak menyentuh SecurityContextHolder sama sekali
        // Ini menutup cabang 'False' dari if(!isAuthenticated)
    }

    // ==========================================
    // 2. Coverage Baris 32 Bagian 1 (Kuning -> Hijau)
    // Skenario: Authentication object NULL
    // ==========================================
    @Test
    @DisplayName("PreHandle: Authentication Null -> Skip Logic")
    void testPreHandle_AuthenticationNull() throws Exception {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null); // Authentication NULL

            // Act
            boolean result = sessionInterceptor.preHandle(request, response, handler);

            // Assert
            assertTrue(result);
            verifyNoInteractions(userService);
        }
    }

    // ==========================================
    // 3. Coverage Baris 32 Bagian 2 (Kuning -> Hijau)
    // Skenario: Authentication Ada, tapi isAuthenticated() FALSE
    // ==========================================
    @Test
    @DisplayName("PreHandle: Authentication Not Authenticated -> Skip Logic")
    void testPreHandle_AuthenticationFalse() throws Exception {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            // Authentication Object ada, TAPI statusnya False
            when(authentication.isAuthenticated()).thenReturn(false);

            // Act
            boolean result = sessionInterceptor.preHandle(request, response, handler);

            // Assert
            assertTrue(result);
            verifyNoInteractions(userService);
        }
    }

    // ==========================================
    // 4. Coverage Baris 33 (Kuning -> Hijau)
    // Skenario: User adalah "anonymousUser"
    // ==========================================
    @Test
    @DisplayName("PreHandle: User Anonymous -> Skip Logic")
    void testPreHandle_AnonymousUser() throws Exception {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            // Kondisi lolos Baris 32, tapi gagal di Baris 33
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("anonymousUser"); // Principal Anonymous

            // Act
            boolean result = sessionInterceptor.preHandle(request, response, handler);

            // Assert
            assertTrue(result);
            verifyNoInteractions(userService);
        }
    }

    // ==========================================
    // 5. Coverage Baris 40 - FALSE (Kuning -> Hijau)
    // Skenario: User tidak ditemukan di DB
    // ==========================================
    @Test
    @DisplayName("PreHandle: User Tidak Ditemukan di DB -> Jangan Set AuthContext")
    void testPreHandle_UserNotFoundInDB() throws Exception {
        // Arrange
        String email = "deleted@example.com";
        when(authContext.isAuthenticated()).thenReturn(false);
        when(userService.getUserByEmail(email)).thenReturn(null); // User NULL

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Lolos semua cek validasi awal
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("SomePrincipal");
            when(authentication.getName()).thenReturn(email);

            // Act
            boolean result = sessionInterceptor.preHandle(request, response, handler);

            // Assert
            assertTrue(result);
            verify(userService).getUserByEmail(email);
            verify(authContext, never()).setAuthUser(any()); // Pastikan setAuthUser TIDAK dipanggil
        }
    }

    // ==========================================
    // 6. Coverage Baris 40 - TRUE (Happy Path)
    // Skenario: Semua kondisi valid
    // ==========================================
    @Test
    @DisplayName("PreHandle: Valid Session -> Set AuthContext")
    void testPreHandle_ValidSession_SetsAuthContext() throws Exception {
        // Arrange
        String email = "test@example.com";
        User user = new User("Test User", email, "password");

        when(authContext.isAuthenticated()).thenReturn(false);
        when(userService.getUserByEmail(email)).thenReturn(user);

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Lolos semua cek
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("UserObject");
            when(authentication.getName()).thenReturn(email);

            // Act
            boolean result = sessionInterceptor.preHandle(request, response, handler);

            // Assert
            assertTrue(result);
            verify(userService).getUserByEmail(email);
            verify(authContext).setAuthUser(user); // Pastikan ini dipanggil
        }
    }
}