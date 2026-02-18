package com.swiftcart.swiftcart.features.address;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

    List<Address> findByUserId(Long id);
    
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.defaultShipping = true")
    Optional<Address> findDefaultShippingAddress(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultShipping = false WHERE a.user.id = :userId")
    void unsetDefaultShipping(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultShipping = true WHERE a.id = :addressId")
    void setDefaultShipping(@Param("addressId") Long addressId);

    long countByUserId(Long userId);
}
