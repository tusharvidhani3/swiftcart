package com.swiftcart.swiftcart.features.appuser;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Integer> {

    @Query("SELECT u FROM AppUser u JOIN FETCH u.role WHERE u.email = :email")
    Optional<AppUser> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM AppUser u JOIN FETCH u.role WHERE u.mobileNumber = :mobileNumber")
    Optional<AppUser> findByMobileNumberWithRole(@Param("mobileNumber") String mobileNumber);

    Page<AppUser> findAll(Pageable pageable);

    Optional<AppUser> findById(Long id);

}
