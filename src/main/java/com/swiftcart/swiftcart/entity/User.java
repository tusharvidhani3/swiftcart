package com.swiftcart.swiftcart.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    @Setter
    private String firstName;

    @Column(name = "last_name")
    @Setter
    private String lastName;

    @Column(name = "mobile_no", unique = true)
    @Setter
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit mobile number")
    private String mobileNumber;

    @Email
    @Column(unique = true, nullable = false)
    @Setter
    private String email;

    @Setter
    @Getter
    private String password;

    @Setter
    @ManyToMany
    private Set<Role> roles;
}
