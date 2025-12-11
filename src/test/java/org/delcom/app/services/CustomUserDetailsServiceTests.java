package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("loadUserByUsername berhasil mengembalikan UserDetails jika user ditemukan")
    void testLoadUserByUsername_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "hashedPassword123";
        User mockUser = new User("Test User", email, password);

        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(password, result.getPassword());
        
        // Memastikan authorities kosong sesuai implementasi: new ArrayList<>()
        assertTrue(result.getAuthorities().isEmpty()); 

        verify(userRepository).findFirstByEmail(email);
    }

    @Test
    @DisplayName("loadUserByUsername melempar Exception jika user tidak ditemukan")
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        // Verifikasi pesan error
        assertEquals("User tidak ditemukan dengan email: " + email, exception.getMessage());
        
        verify(userRepository).findFirstByEmail(email);
    }
}