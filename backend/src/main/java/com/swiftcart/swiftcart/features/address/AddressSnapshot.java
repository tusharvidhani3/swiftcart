package com.swiftcart.swiftcart.features.address;

public record AddressSnapshot(

    String name,
    String addressLine1,
    String addressLine2,
    String pincode,
    String city,
    String state,
    String mobileNumber

) {}