package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface DevLibraryRepository extends JpaRepository<DevOwnedLibrary, Long> {
}
