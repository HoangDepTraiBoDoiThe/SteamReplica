package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.DiscountAssembler;
import com.example.steamreplica.controller.assembler.DlcAssembler;
import com.example.steamreplica.controller.assembler.GameAssembler;
import com.example.steamreplica.dtos.request.DlcRequest;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Full;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.DlcRepository;
import com.example.steamreplica.service.exception.ResourceExitedException;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DlcService {
    private final DlcRepository dlcRepository;
    private final ServiceHelper serviceHelper;
    private final GameService gameService;
    private final DlcImageService dlcImageService;
    
    public EntityModel<DlcResponse_Full> getDlcById(long id, Authentication authentication) {
        DLC dlc = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
        return serviceHelper.makeDlcResponse(DlcResponse_Full.class, dlc, authentication);
    }
    
    public DLC getDlcById_entity(long id) {
        return dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
    }

    public List<EntityModel<DlcResponse_Basic>> getAllDlcOfGame(long gameId, Authentication authentication) {
        return dlcRepository.getAllByGame_Id(gameId).stream().map(dlc -> serviceHelper.makeDlcResponse(DlcResponse_Basic.class, dlc, authentication)).toList();
    }
    
    public List<EntityModel<DlcResponse_Basic>> getAllDlc(Authentication authentication) {
        return dlcRepository.findAll().stream().map(dlc -> serviceHelper.makeDlcResponse(DlcResponse_Basic.class, dlc, authentication)).toList();
    }

    @Transactional
    public EntityModel<DlcResponse_Full> addDlc(DlcRequest dlcRequest, Authentication authentication) {
        if (dlcRepository.findDLCByDlcName(dlcRequest.getDlcName()).isPresent()) throw new ResourceExitedException(String.format("DLC with name [%s] already exists", dlcRequest.getDlcName()));
        DLC newDlc = new DLC(dlcRequest.getDlcName(), dlcRequest.getDlcDescription(), dlcRequest.getDlcBasePrice(), StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()), dlcRequest.getReleaseDate());
        Game game = gameService.getGameById_entity(dlcRequest.getOwningGameId());
        List<DLCImage> dlcImages = dlcImageService.getDlcImagesByIds_entity(dlcRequest.getDlcImages());
        newDlc.setGame(game);
        newDlc.setDlcImages(new HashSet<>(dlcImages));
       
        DLC newCreatedDlc = dlcRepository.save(newDlc);

        return serviceHelper.makeDlcResponse(DlcResponse_Full.class, newCreatedDlc, authentication);
    }

    @Transactional
    public EntityModel<DlcResponse_Full> updateDlc(long id, DlcRequest dlcRequest, Authentication authentication) {
        DLC dlcToUpdate = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcToUpdate.setDlcName(dlcRequest.getDlcName());
        dlcToUpdate.setDlcDescription(dlcRequest.getDlcDescription());
        dlcToUpdate.setDlcBasePrice(dlcRequest.getDlcBasePrice());
        dlcToUpdate.setDlcThumbnail(StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()));
        dlcToUpdate.setReleaseDate(dlcRequest.getReleaseDate());
        DLC updatedDlc = dlcRepository.save(dlcToUpdate);
        
        return serviceHelper.makeDlcResponse(DlcResponse_Full.class, updatedDlc, authentication);
    }

    public void deleteDlc(long id) {
        dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcRepository.deleteById(id);
    }

}
