package com.swiftcart.swiftcart.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellerRegisterRequest {

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Not a valid mobile number")
    @NotBlank
    private String mobileNumber;

    @Email(message = "Please enter a valid email address")
    @NotBlank(message = "Email is required for seller registration")
    private String email;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
        message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
        )
    private String password;
}
