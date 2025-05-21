package com.swiftcart.swiftcart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    public Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.mobileNumber = :mobileNumber")
    public Optional<User> findByMobileNumberWithRoles(@Param("mobileNumber") String mobileNumber);
}
