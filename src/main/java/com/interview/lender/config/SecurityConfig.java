package com.interview.lender.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthConfig authConfig;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        .requestMatchers("/loans/subscribe").hasRole("USER")
                        .requestMatchers("/loans/request").hasRole("USER")
                        .requestMatchers("/loans/status/**").hasRole("USER")
                        .requestMatchers("/transaction-data/**").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }



    @Bean
    public UserDetailsService userDetailsService() {
        List<UserDetails> users = new ArrayList<>();

        for (AuthConfig.User userConfig : authConfig.getUsers()) {
            UserDetails user = User.builder()
                    .username(userConfig.getUsername())
                    .password(passwordEncoder().encode(userConfig.getPassword()))
                    .roles(userConfig.getRoles().toArray(new String[0]))
                    .build();
            users.add(user);
        }

        return new InMemoryUserDetailsManager(users);
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}