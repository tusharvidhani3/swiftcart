package com.swiftcart.swiftcart.features.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "^[6789]{1}[0-9]{9}$", message = "Enter valid 10 digit mobile number")
    private String mobileNumber;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", message = "Invalid password")
    private String password;
}
