package com.interview.lender.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.security")
@Data
public class AuthConfig {

    private List<User> users;

    @Data
    public static class User {
        private String username;
        private String password;
        private List<String> roles;
    }
}
