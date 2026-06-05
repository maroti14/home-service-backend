package com.homeservice.domain.auth.dto.request;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 60)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Min 8 characters")
    private String password;

    // secret key to prevent unauthorised admin creation
    @NotBlank(message = "Admin secret is required")
    private String adminSecret;
}
