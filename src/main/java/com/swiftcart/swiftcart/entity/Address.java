package com.swiftcart.swiftcart.entity;

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
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Setter
    private String name;

    @Setter
    private String addressLine1;

    @Setter
	private String addressLine2;

    @Setter
	private String area;

    @Setter
	private String pincode;

    @Setter
	private String city;

    @Setter
	private String state;

    @Setter
    private String mobileNumber;

    @Setter
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @Setter
    private Boolean isDefaultShipping = false;

	@ManyToOne
    @Setter
	private User user;
}
