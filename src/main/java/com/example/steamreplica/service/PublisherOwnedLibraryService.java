package com.example.steamreplica.service;

import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.purchasedLibrary.PublisherOwnedLibrary;
import com.example.steamreplica.repository.DevLibraryRepository;
import com.example.steamreplica.repository.PublisherLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherOwnedLibraryService {
    private final PublisherLibraryRepository publisherLibraryRepository;

    public PublisherOwnedLibrary findByUserId_entity(long id) {
        return publisherLibraryRepository.findByUserId(id).orElseThrow(() -> new RuntimeException(String.format("PublisherOwnedLibrary with user id %d not found", id)));
    }
}
