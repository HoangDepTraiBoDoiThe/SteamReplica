package com.example.steamreplica.service;

import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.repository.DevLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevOwnedLibraryService {
    private final DevLibraryRepository devLibraryRepository;

    public DevOwnedLibrary findByUserId_entity(long id) {
        return devLibraryRepository.findByUserId(id).orElseThrow(() -> new RuntimeException(String.format("DevOwnedLibrary with user id %d not found", id)));
    }
}
