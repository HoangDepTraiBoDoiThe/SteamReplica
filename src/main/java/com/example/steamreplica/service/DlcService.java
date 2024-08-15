package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.DiscountAssembler;
import com.example.steamreplica.controller.assembler.DlcAssembler;
import com.example.steamreplica.controller.assembler.GameAssembler;
import com.example.steamreplica.dtos.request.DlcRequest;
import com.example.steamreplica.dtos.response.DiscountResponse;
import com.example.steamreplica.dtos.response.DlcResponse_Full;
import com.example.steamreplica.dtos.response.ResponseBase;
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

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DlcService {
    private final DlcRepository dlcRepository;
    private final DlcAssembler dlcAssembler;
    private final DiscountAssembler discountAssembler;
    private final GameAssembler gameAssembler;
    private final GameService gameService;
    private final DlcImageService dlcImageService;
    
    public EntityModel<DlcResponse_Full> getDlcById(long id, Authentication authentication) {
        DLC dlc = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
        DlcResponse_Full dlcResponseFull = makeDlcResponse(authentication, dlc);
        return dlcAssembler.toModel(dlcResponseFull, authentication);
    }

    public CollectionModel<EntityModel<DlcResponse_Full>> getAllDlcOfGame(long gameId, Authentication authentication) {
        List<DlcResponse_Full> dlcResponsFulls = dlcRepository.getAllByGame(gameId).stream().map(dlc -> makeDlcResponse(authentication, dlc)).toList();
        return dlcAssembler.toCollectionModel(dlcResponsFulls, authentication);
    }
    
    public CollectionModel<EntityModel<DlcResponse_Full>> getAllDlc(Authentication authentication) {
        List<DlcResponse_Full> dlcResponsFulls = dlcRepository.findAll().stream().map(dlc -> makeDlcResponse(authentication, dlc)).toList();
        return dlcAssembler.toCollectionModel(dlcResponsFulls, authentication);
    }

    public EntityModel<DlcResponse_Full> addDlc(DlcRequest dlcRequest, Authentication authentication) {
        if (dlcRepository.findDLCByDlcName(dlcRequest.getDlcName()).isPresent()) throw new ResourceExitedException(String.format("DLC with name [%s] already exists", dlcRequest.getDlcName()));
        DLC newDlc = new DLC(dlcRequest.getDlcName(), dlcRequest.getDlcDescription(), dlcRequest.getDlcBasePrice(), StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()), dlcRequest.getReleaseDate());
        Game game = gameService.getGameById_entity(dlcRequest.getOwningGameId());
        List<DLCImage> dlcImages = dlcImageService.getDlcImagesByIds_entity(dlcRequest.getDlcImages());
        newDlc.setGame(game);
        newDlc.setDlcImages(new HashSet<>(dlcImages));
       
        DLC newCreatedDlc = dlcRepository.save(newDlc);
        
        return dlcAssembler.toModel(makeDlcResponse(authentication, newCreatedDlc), authentication);
    }

    public EntityModel<DlcResponse_Full> updateDlc(long id, DlcRequest dlcRequest, Authentication authentication) {
        DLC dlcToUpdate = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcToUpdate.setDlcName(dlcRequest.getDlcName());
        dlcToUpdate.setDlcDescription(dlcRequest.getDlcDescription());
        dlcToUpdate.setDlcBasePrice(dlcRequest.getDlcBasePrice());
        dlcToUpdate.setDlcThumbnail(StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()));
        dlcToUpdate.setReleaseDate(dlcRequest.getReleaseDate());
        DLC updatedDlc = dlcRepository.save(dlcToUpdate);
        return dlcAssembler.toModel(makeDlcResponse(authentication, updatedDlc), authentication);
    }

    public void deleteDlc(long id) {
        dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcRepository.deleteById(id);
    }

    public DlcResponse_Full makeDlcResponse(Authentication authentication, DLC dlc) {
        List<DiscountResponse> discountResponses = dlc.getDiscounts().stream().map(DiscountResponse::new).toList();
        CollectionModel<EntityModel<DiscountResponse>> discountCollectionModel = discountAssembler.toCollectionModel(discountResponses, authentication);
        Game game = gameService.getGameById_entity(dlc.getId());
        // todo: dlc images.

        DlcResponse_Full dlcResponseFull = new DlcResponse_Full(dlc);
        dlcResponseFull.setDiscounts(discountCollectionModel);
        dlcResponseFull.setGame(gameAssembler.toModel(new ResponseBase(game.getId(), game.getGameName()), authentication));
        return dlcResponseFull;
    }
}
