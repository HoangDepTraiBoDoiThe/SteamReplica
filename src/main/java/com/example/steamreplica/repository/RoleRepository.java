package com.example.steamreplica.repository;

import com.example.steamreplica.model.userApplication.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<ApplicationRole, Long> {
    Optional<ApplicationRole> findApplicationRoleByRoleName(String name);
}
