package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public interface DevLibraryRepository extends JpaRepository<DevOwnedLibrary, Long> {
    Optional<DevOwnedLibrary> findByUserId(long Id);
}
