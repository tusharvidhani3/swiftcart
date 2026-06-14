package com.swiftcart.swiftcart.features.appuser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.features.auth.AuthProvider;

@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
    @Query("SELECT up FROM UserProvider up JOIN FETCH up.user WHERE up.provider = :provider AND up.providerId = :providerId")
    Optional<UserProvider> findByProviderAndProviderId(@Param("provider") AuthProvider provider, @Param("providerId") String providerId);
}
