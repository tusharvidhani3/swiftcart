package com.swiftcart.swiftcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swiftcart.swiftcart.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {

    public Role getRoleByName(String name);
}
