package com.swiftcart.swiftcart.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "[6789]{1}[0-9]{9}", message = "Enter valid 10 digit mobile number")
    private String mobileNumber;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
