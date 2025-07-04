package com.swiftcart.swiftcart.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    public Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.mobileNumber = :mobileNumber")
    public Optional<User> findByMobileNumberWithRole(@Param("mobileNumber") String mobileNumber);

    public Page<User> findAll(Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.mobileNumber = :mobileNumber, u.email = :email, u.firstName = :firstName, u.lastName = :lastName WHERE u.userId = :userId")
    public void updateUser(@Param("userId") Long userId, @Param("email") String email, @Param("mobileNumber") String mobileNumber, @Param("firstName") String firstName, @Param("lastName") String lastName);
}
