package com.swiftcart.swiftcart.payload;

import com.swiftcart.swiftcart.entity.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDTO {

    private Long addressId;

    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[A-Za-z\\s]{3,200}$", message = "Please enter a valid name")
    private String name;

    @NotBlank(message = "House no./building/apartment/company name cannot be blank")
	@Pattern(regexp = "^[A-Za-z0-9\\s-]{3,}$", message = "Not a valid house no./building/apartment/company")
    private String addressLine1;

    @NotBlank(message = "Area/Street cannot be blank")
	@Pattern(regexp = "^[A-Za-z\\s-]{2,}$", message = "Not a valid area/street")
    private String addressLine2;

    @NotBlank(message = "Pincode cannot be blank")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must contain exactly 6 characters")
    private String pincode;

    @NotBlank(message = "City cannot be blank")
	@Pattern(regexp = "^[A-Za-z\\s-]{2,}$", message = "Not a valid city name")
    private String city;

    @NotBlank(message = "State cannot be blank")
	@Pattern(regexp = "^[A-Za-z\\s]{3,}$", message = "Not a valid state name")
    private String state;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Not a valid mobile number")
    @NotBlank(message = "Mobile number cannot be blank")
    private String mobileNumber;

    private AddressType addressType;

    private Boolean isDefaultShipping;
}
