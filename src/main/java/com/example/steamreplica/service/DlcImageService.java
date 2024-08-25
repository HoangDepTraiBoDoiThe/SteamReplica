package com.example.steamreplica.service;

import com.example.steamreplica.dtos.response.DlcImageResponse;
import com.example.steamreplica.dtos.response.game.ImageResponse;
import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.repository.DlcImageRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DlcImageService {
    private final DlcImageRepository dlcImageRepository;
    private final ServiceHelper serviceHelper;

    public List<EntityModel<ImageResponse>> getAllDlcImages(Authentication authentication) {
        return dlcImageRepository.findAll().stream().map(dlcImage -> serviceHelper.makeDlcImageResponse(ImageResponse.class, dlcImage, authentication)).toList();
    }

    public List<DLCImage> getDlcImagesByIds_entity(Set<Long> ids) {
        return dlcImageRepository.findAllById(ids);
    }

    public EntityModel<DlcImageResponse> getDlcImageById(Long id, Authentication authentication) {
        DLCImage image = dlcImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC Image with id %d not found", id)));
        return serviceHelper.makeDlcImageResponse(DlcImageResponse.class, image, authentication);
    }

    @Transactional
    public EntityModel<DlcImageResponse> saveDlcImage(DLCImage dlcImage, Authentication authentication) {
        return serviceHelper.makeDlcImageResponse(DlcImageResponse.class, dlcImageRepository.save(dlcImage), authentication);
    }

    @Transactional
    public EntityModel<DlcImageResponse> updateDlcImage(long id, DLCImage dlcImage, Authentication authentication) {
        DLCImage image = dlcImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC Image with id %d not found", id)));
        image.setImageName(dlcImage.getImageName());
        return serviceHelper.makeDlcImageResponse(DlcImageResponse.class, dlcImageRepository.save(dlcImage), authentication);
    }

    public void deleteDlcImageById(Long id) {
        dlcImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC Image with id %d not found", id)));
        dlcImageRepository.deleteById(id);
    }

    @Transactional
    public DLCImage findById_entityFull(long id) {
        return dlcImageRepository.findById_full(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC Image with id %d not found", id)));
    }
}
