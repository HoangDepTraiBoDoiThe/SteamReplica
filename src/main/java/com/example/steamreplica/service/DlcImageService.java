package com.example.steamreplica.service;

import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.repository.DlcImageRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DlcImageService {
    private final DlcImageRepository dlcImageRepository;

    public List<DLCImage> getAllDlcImages() {
        return dlcImageRepository.findAll();
    }

    public List<DLCImage> getDlcImagesByIds_entity(Set<Long> ids) {
        return dlcImageRepository.findAllById(ids);
    }

    public DLCImage getDlcImageById(Long id) {
        return dlcImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC Image with id %d not found", id)));
    }

    public DLCImage saveDlcImage(DLCImage dlcImage) {
        return dlcImageRepository.save(dlcImage);
    }

    public void deleteDlcImageById(Long id) {
        dlcImageRepository.deleteById(id);
    }
}
