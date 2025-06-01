package com.interview.lender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegistrationRequest {
    private String url;
    private String name;
    private String username;
    private String password;
}
