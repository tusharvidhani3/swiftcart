package com.swiftcart.swiftcart.features.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "^[6789]{1}[0-9]{9}$", message = "Enter valid 10 digit mobile number")
    String mobileNumber,

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", message = "Invalid password")
    String password

) {}