package com.swiftcart.swiftcart.features.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDto(

    Long id,

    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[A-Za-z]{2,50}(?:[ .'-][A-Za-z]{2,50})*$", message = "Please enter a valid name")
    String name,

    @NotBlank(message = "Building/Apartment/Company name cannot be blank")
	@Pattern(regexp = "^[A-Za-z0-9\\s,./'-]{3,100}$", message = "Not a valid house no./building/apartment/company")
    String addressLine1,

    @NotBlank(message = "Area/Street cannot be blank")
	@Pattern(regexp = "^[A-Za-z0-9\\s,./'-]{3,100}$", message = "Not a valid area/street")
    String addressLine2,

    @NotBlank(message = "Pincode cannot be blank")
	@Pattern(regexp = "^[1-9]\\d{5}$", message = "Not a valid pincode")
    String pincode,

    @NotBlank(message = "City cannot be blank")
	@Pattern(regexp = "^[A-Za-z\\s'-]{3,100}$", message = "Not a valid city name")
    String city,

    @NotBlank(message = "State cannot be blank")
	@Pattern(regexp = "^[A-Za-z\\s-]{3,50}$", message = "Not a valid state name")
    String state,

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Not a valid mobile number")
    @NotBlank(message = "Mobile number cannot be blank")
    String mobileNumber,

    AddressType addressType,

    Boolean defaultShipping
) {}