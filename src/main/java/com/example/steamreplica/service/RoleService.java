package com.example.steamreplica.service;

import com.example.steamreplica.model.userApplication.ApplicationRole;
import com.example.steamreplica.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<ApplicationRole> createRoles(Collection<ApplicationRole> roles) {
        return roleRepository.saveAll(roles);
    }

    public ApplicationRole getApplicationRoleByName(String name) {
        return roleRepository.findApplicationRoleByRoleName(name).orElseThrow(() -> new RuntimeException(String.format("Role [%s] not found", name)));
    }
    
    public List<ApplicationRole> getAllRoles() {
        return roleRepository.findAll();
    }
}
