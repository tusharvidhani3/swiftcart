package com.swiftcart.swiftcart.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long userId;

    @Pattern(regexp = "^[A-Za-z]+(?:[\\s.][A-Za-z]+)*$", message = "Enter valid characters in first name")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z]+(?:[\\s.][A-Za-z]+)*$", message = "Enter valid characters in last name")
    private String lastName;

    @NotBlank(message = "Please enter the mobile Number")
    @Pattern(regexp = "[6789]{1}[0-9]{9}", message = "Enter valid 10 digit mobile number")
    private String mobileNumber;

	@Email(message = "Please enter a valid email address")
    private String email;

    private String role;

}
