package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.ConstUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthContext authContext;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        // PENTING: Inject AuthContext manual karena di controller menggunakan @Autowired field injection
        ReflectionTestUtils.setField(authController, "authContext", authContext);
    }

    @Test
    @DisplayName("GET /auth/login returns correct view when not authenticated")
    void testGetLogin() {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(false);

        // Act
        String viewName = authController.login(model);

        // Assert: Pastikan view name sesuai ConstUtil ("pages/auth/login")
        // Ini akan memperbaiki error "expected:<auth/login> but was:<pages/auth/login>"
        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN, viewName);
        verify(model).addAttribute(eq("loginForm"), any());
    }

    @Test
    @DisplayName("GET /auth/login redirects to items if authenticated")
    void testGetLogin_Redirects_If_Authenticated() {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(true);

        // Act
        String viewName = authController.login(model);

        // Assert
        assertEquals("redirect:/dashboard", viewName);
    }

    @Test
    @DisplayName("GET /auth/register returns correct view when not authenticated")
    void testGetRegister() {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(false);

        // Act
        String viewName = authController.register(model);

        // Assert: Pastikan view name sesuai ConstUtil ("pages/auth/register")
        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, viewName);
        verify(model).addAttribute(eq("registerForm"), any());
    }

    @Test
    @DisplayName("POST /auth/register/post success")
    void testRegisterPost_Success() {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setName("New User");
        form.setEmail("new@example.com");
        form.setPassword("password");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(null); // Email belum ada
        when(passwordEncoder.encode(form.getPassword())).thenReturn("encodedPwd");

        // Act
        String viewName = authController.registerPost(form, bindingResult, redirectAttributes);

        // Assert
        assertEquals("redirect:/auth/login", viewName);
        verify(userService).createUser("New User", "new@example.com", "encodedPwd");
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    @DisplayName("POST /auth/register/post fails when email exists")
    void testRegisterPost_EmailExists() {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setEmail("exist@example.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        // Simulasi email sudah ada
        when(userService.getUserByEmail(form.getEmail())).thenReturn(new User()); 

        // Act
        String viewName = authController.registerPost(form, bindingResult, redirectAttributes);

        // Assert
        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, viewName);
        verify(bindingResult).rejectValue(eq("email"), anyString(), anyString());
        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    // ==========================================
    // PERBAIKAN 1: Cover Redirect jika User Sudah Login
    // (Memperbaiki Diamond Kuning di Baris 45)
    // ==========================================
    @Test
    @DisplayName("GET /auth/register redirects to dashboard if authenticated")
    void testRegister_Redirects_If_Authenticated() {
        // Arrange
        // Simulasi user sudah login
        when(authContext.isAuthenticated()).thenReturn(true);

        // Act
        String viewName = authController.register(model);

        // Assert
        // Harusnya diarahkan kembali ke dashboard
        assertEquals("redirect:/dashboard", viewName);
    }

    // ==========================================
    // PERBAIKAN 2: Cover Validasi Error (Input Salah)
    // (Memperbaiki Diamond Kuning di Baris 62)
    // ==========================================
    @Test
    @DisplayName("POST /auth/register/post returns register view if validation fails")
    void testRegisterPost_ValidationErrors() {
        // Arrange
        RegisterForm form = new RegisterForm(); // Data form (kosong gapapa, yg penting bindingResult)
        
        // Simulasi ada error validasi (misal: nama kosong, format email salah)
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = authController.registerPost(form, bindingResult, redirectAttributes);

        // Assert
        // 1. Harus kembali ke halaman register
        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, viewName);
        
        // 2. Pastikan service create user TIDAK dipanggil
        verify(userService, never()).createUser(any(), any(), any());
    }
}