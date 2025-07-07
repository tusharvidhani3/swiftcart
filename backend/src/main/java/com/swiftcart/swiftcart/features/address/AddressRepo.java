package com.swiftcart.swiftcart.features.address;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<Address,Long> {

    public List<Address> findAllByUser_UserId(Long userId);
    public Optional<Address> findByAddressId(Long shippingAddressId);
    
    @Query("SELECT a FROM Address a WHERE a.user.userId = :userId AND a.isDefaultShipping = true")
    public Optional<Address> findDefaultShippingAddress(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefaultShipping = false WHERE a.user.id = :userId")
    public void unsetDefaultShipping(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefaultShipping = true WHERE a.addressId = :addressId")
    public void setDefaultShipping(@Param("addressId") Long addressId);

    public long countByUser_UserId(Long userId);
}
