package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface BoughtLibraryRepository extends JpaRepository<BoughtLibrary, Long> {
}
