package com.marketplace.salesapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private Environment environment;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Habilitando CSRF e configurando exceções
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/auth/**")
                .ignoringRequestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                .ignoringRequestMatchers("/h2-console/**")
            )
            .cors() // Usa o CorsConfigurationSource definido em WebConfig
            .and()
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        
        // Configuração condicional baseada no perfil ativo
        if (isDevelopmentMode()) {
            // Configuração para desenvolvimento
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            // Configuração para produção
            http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            );
        }
        
        return http.build();
    }
    
    private boolean isDevelopmentMode() {
        return activeProfile.equals("dev") || 
               activeProfile.equals("test") || 
               (environment.getActiveProfiles().length > 0 && 
                (environment.getActiveProfiles()[0].equals("dev") || 
                 environment.getActiveProfiles()[0].equals("test")));
    }
} 