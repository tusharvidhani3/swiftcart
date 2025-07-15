package com.swiftcart.swiftcart.features.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    public Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.mobileNumber = :mobileNumber")
    public Optional<User> findByMobileNumberWithRole(@Param("mobileNumber") String mobileNumber);

    public Page<User> findAll(Pageable pageable);

    public Optional<User> findByUserId(Long userId);

}
