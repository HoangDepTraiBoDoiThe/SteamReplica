package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.PublisherOwnedLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface PublisherLibraryRepository extends JpaRepository<PublisherOwnedLibrary, Long> {
}
