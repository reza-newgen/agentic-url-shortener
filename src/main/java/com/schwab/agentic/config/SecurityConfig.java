package com.schwab.agentic.config;


import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(@Value("${agentic.reviewer.username}") String name, @Value("${agentic.reviewer.password}") String pass, PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(User.withUsername(name).password(encoder.encode(pass)).roles("REVIEWER").build());
    }

    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(a -> a.requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**", "/r/**").permitAll().requestMatchers("/api/workflows/**").hasRole("REVIEWER").anyRequest().authenticated()).httpBasic(org.springframework.security.config.Customizer.withDefaults()).build();
    }
}
