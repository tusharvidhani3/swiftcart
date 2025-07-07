package com.swiftcart.swiftcart.features.address;

import com.swiftcart.swiftcart.features.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String name;

    private String addressLine1;

	private String addressLine2;

	private String area;

	private String pincode;

	private String city;

	private String state;

    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private Boolean isDefaultShipping = false;

	@ManyToOne
	private User user;
}
