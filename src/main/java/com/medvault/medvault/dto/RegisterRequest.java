package com.medvault.medvault.dto;

import com.medvault.medvault.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Role role;
}
