package org.delcom.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF (Boleh dimatikan untuk development, tapi idealnya dinyalakan untuk Form)
            // Kita disable dulu agar tidak ribet dengan token
            .csrf(csrf -> csrf.disable())

            // 2. Atur izin URL
            .authorizeHttpRequests(auth -> auth
                // Izinkan akses ke folder public
                .requestMatchers(
                    "/auth/**", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/webjars/**",
                    "/assets/**"  // <--- TAMBAHKAN INI (JANGAN LUPA KOMA DI BARIS ATASNYA)
                ).permitAll()
                
                // Sisanya wajib login
                .anyRequest().authenticated()
            )

            // 3. Konfigurasi Form Login (SESUAIKAN DISINI)
            .formLogin(login -> login
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login/post")
            .usernameParameter("email")
            .passwordParameter("password")
            
            // UBAH DARI "/items" MENJADI "/dashboard"
            .defaultSuccessUrl("/dashboard", true) 
            
            .failureUrl("/auth/login?error=true")
            .permitAll()
        )

            // 4. Konfigurasi Logout
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}