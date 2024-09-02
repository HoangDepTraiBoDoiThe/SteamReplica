package com.example.steamreplica.service;

import com.example.steamreplica.controller.DlcController;
import com.example.steamreplica.dtos.request.DlcRequest;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Full;
import com.example.steamreplica.event.DiscountUpdateEvent;
import com.example.steamreplica.event.GameUpdateEvent;
import com.example.steamreplica.event.DlcImageUpdateEvent;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.repository.BoughtLibraryRepository;
import com.example.steamreplica.repository.DlcRepository;
import com.example.steamreplica.service.exception.AuthenticationException;
import com.example.steamreplica.service.exception.ResourceExitedException;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DlcService {
    private final DlcRepository dlcRepository;
    private final ServiceHelper serviceHelper;
    private final GameService gameService;
    private final DlcImageService dlcImageService;
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final CacheHelper cacheHelper;
    private final DiscountService discountService;

    private final String DLC_LIST_CACHE = "dlcListCache";
    private final String DLC_CACHE = "dlcCache";
    private final String DLC_PAGINATION_CACHE_PREFIX = "dlcPaginationCache";
    private final String NEW_AND_TRENDING_DLC_PAGINATION_CACHE_PREFIX = "newAndTrending";
    private final String TOP_SELLER_DLC_PAGINATION_CACHE_PREFIX = "topSeller";
    private final String SPECIAL_DLC_PAGINATION_CACHE_PREFIX = "Special";
    private final Integer PAGE_RANGE = 10;
    private final Integer PAGE_SIZE = 10;

    @EventListener
    public void gameUpdated(GameUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                DLC_CACHE,
                List.of(DLC_PAGINATION_CACHE_PREFIX),
                List.of(DLC_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    Game game = gameService.findGameWithById_withDLC((Long) id);
                    return game.getDlcs().stream().anyMatch(dlc -> Objects.equals(dlc.getId(), entity.getId()));
                });
    }

    @EventListener
    public void imageUpdated(DlcImageUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                DLC_CACHE,
                List.of(DLC_PAGINATION_CACHE_PREFIX),
                List.of(DLC_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    DLCImage dlcImage = dlcImageService.findById_entityFull((Long) id);
                    return Objects.equals(dlcImage.getDlc().getId(), entity.getId());
                });
    }

    @EventListener
    public void discountUpdated(DiscountUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                DLC_CACHE,
                List.of(DLC_PAGINATION_CACHE_PREFIX),
                List.of(DLC_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    Discount discount = discountService.getDiscountById_entityFull((Long) id);
                    return discount.getDiscountedDlc().stream().anyMatch(dlc -> Objects.equals(dlc.getId(), entity.getId()));
                });
    }

    public EntityModel<DlcResponse_Full> getDlcById(long id, Authentication authentication) {
        return cacheHelper.getCache(DLC_CACHE, DlcResponse_Full.class, id, dlcRepository, repo -> {
            DLC dlc = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
            return serviceHelper.makeDlcResponse(DlcResponse_Full.class, dlc, authentication);
        }, 30);
    }

    public DLC getDlcById_entity(long id) {
        return dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
    }

    public DLC getDlcById_withPurchasedDLCs(long id) {
        return dlcRepository.findById_withPurchasedDLCs(id).orElseThrow(() -> new ResourceNotFoundException(String.format("DLC with id %d not found", id)));
    }

    @Transactional
    public CollectionModel<EntityModel<DlcResponse_Basic>> getAllDlcOfGame(long gameId, int page, Authentication authentication) {
        return cacheHelper.getPaginationCache(DLC_PAGINATION_CACHE_PREFIX, DlcResponse_Basic.class, page, dlcRepository, repo -> {
            List<DLC> dlcs = repo.findAllByGame_Id(gameId, PageRequest.of(page, PAGE_SIZE)).getContent();
            CollectionModel<EntityModel<DlcResponse_Basic>> entityModels = serviceHelper.makeDlcResponse_CollectionModel(DlcResponse_Basic.class, dlcs, authentication);
            return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> DummyInvocationUtils.methodOn(DlcController.class).getAllDlcOfGame(gameId, currentPage, authentication));
        });

    }

//    public List<EntityModel<DlcResponse_Basic>> getAllDlc(Authentication authentication) {
//        return dlcRepository.findAll().stream().map(dlc -> serviceHelper.makeDlcResponse(DlcResponse_Basic.class, dlc, authentication)).toList();
//    }

    @Transactional
    public EntityModel<DlcResponse_Full> addDlc(DlcRequest dlcRequest, Authentication authentication) {
        if (dlcRepository.findDLCByDlcName(dlcRequest.getDlcName()).isPresent())
            throw new ResourceExitedException(String.format("DLC with name [%s] already exists", dlcRequest.getDlcName()));
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
        EntityModel<DlcResponse_Full> responseFull = serviceHelper.makeDlcResponse(DlcResponse_Full.class, updatedDlc, authentication);
        
        cacheHelper.updateCache(responseFull, DLC_CACHE);
        cacheHelper.deleteListCaches(DLC_LIST_CACHE);
        cacheHelper.updatePaginationCache(responseFull, PAGE_RANGE, DLC_PAGINATION_CACHE_PREFIX);
        return responseFull;
    }

    @Transactional
    public void deleteDlc(long id) {
        DLC dlc = dlcRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DLC not found"));
        dlcRepository.deleteById(id);
        cacheHelper.deleteCaches(DLC_CACHE, dlc.getId(), DLC_LIST_CACHE);
        cacheHelper.deletePaginationCache(PAGE_RANGE, DLC_PAGINATION_CACHE_PREFIX);
    }

    @Transactional
    public List<EntityModel<DlcResponse_Basic>> getPurchasedDlcOfGame(long gameId, Authentication authentication) {
        AuthUserDetail authUserDetail = StaticHelper.extractAuthUserDetail(authentication).orElseThrow(() -> new AuthenticationException("Authentication failed"));
        return boughtLibraryRepository.findPurchasedDlcOfGame(authUserDetail.getId(), gameId).stream().map(dlc -> serviceHelper.makeDlcResponse(DlcResponse_Basic.class, dlc, authentication)).toList();
    }
}
