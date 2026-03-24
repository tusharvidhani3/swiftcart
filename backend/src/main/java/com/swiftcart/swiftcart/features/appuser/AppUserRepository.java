package com.swiftcart.swiftcart.features.appuser;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Integer> {

    @EntityGraph(attributePaths = {"role"})
    Optional<AppUser> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"role"})
    Optional<AppUser> findByMobileNumber(@Param("mobileNumber") String mobileNumber);

    Page<AppUser> findAll(Pageable pageable);

    Optional<AppUser> findById(Long id);

}
