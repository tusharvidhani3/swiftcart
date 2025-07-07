package com.swiftcart.swiftcart.features.address;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressSnapshot {

    private String name;
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private String city;
    private String state;
    private String mobileNumber;

}
