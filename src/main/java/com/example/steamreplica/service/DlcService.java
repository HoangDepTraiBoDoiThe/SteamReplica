package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.DlcAssembler;
import com.example.steamreplica.dtos.request.DlcRequest;
import com.example.steamreplica.dtos.response.DlcResponse;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.DlcRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DlcService {
    private final DlcRepository dlcRepository;
    private final DlcAssembler dlcAssembler;
    private final GameService gameService;
    
    public EntityModel<DlcResponse> getDlcById(long id) {
        DLC dlc = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
        return dlcAssembler.toModel(new DlcResponse(dlc));
    }

    public CollectionModel<EntityModel<DlcResponse>> getAllDlcOfGame(long gameId) {
        List<DlcResponse> dlcResponses = dlcRepository.getAllByGame(gameId).stream().map(DlcResponse::new).toList();
        return dlcAssembler.toCollectionModel(dlcResponses);
    }
    
    public CollectionModel<EntityModel<DlcResponse>> getAllDlc() {
        List<DlcResponse> dlcResponses = dlcRepository.findAll().stream().map(DlcResponse::new).toList();
        return dlcAssembler.toCollectionModel(dlcResponses);
    }

    public EntityModel<DlcResponse> addDlc(DlcRequest dlcRequest, Authentication authentication) {
        DLC newDlc = new DLC(dlcRequest.getDlcName(), dlcRequest.getDlcDescription(), dlcRequest.getDlcBasePrice(), StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()));
        Game game = gameService.getGameById_entity(dlcRequest.getOwningGameId());
        newDlc.setGame(game);
        return dlcAssembler.toModel(new DlcResponse(dlcRepository.save(newDlc)));
    }

    public EntityModel<DlcResponse> updateDlc(long id, DlcRequest dlcRequest, Authentication authentication) {
        DLC dlcToUpdate = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcToUpdate.setDlcName(dlcRequest.getDlcName());
        dlcToUpdate.setDlcDescription(dlcRequest.getDlcDescription());
        dlcToUpdate.setDlcBasePrice(dlcRequest.getDlcBasePrice());
        dlcToUpdate.setDlcThumbnail(StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()));
        DLC updatedDlc = dlcRepository.save(dlcToUpdate);
        return dlcAssembler.toModel(new DlcResponse(updatedDlc));
    }

    public void deleteDlc(long id) {
        dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcRepository.deleteById(id);
    }
}
