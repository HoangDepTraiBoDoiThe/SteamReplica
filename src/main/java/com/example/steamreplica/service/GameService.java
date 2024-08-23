package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.purchasedLibrary.PublisherOwnedLibrary;
import com.example.steamreplica.repository.*;
import com.example.steamreplica.service.exception.GameException;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserService userService;
    private final DiscountService discountService;
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final DevLibraryRepository devLibraryRepository;
    private final PublisherLibraryRepository publisherLibraryRepository;
    private final CategoryService categoryService;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;
    
    @Cacheable(value = "gameListCache")
    public List<EntityModel<GameResponse_Basic>> getAllGames(Authentication authentication) {
        return gameRepository.findAll().stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Basic.class, game, authentication)).toList();
    }

    @Cacheable(value = "gameCache", key = "#id")
    public EntityModel<GameResponse_Full> getGameById(long id, Authentication authentication) {
        Game game = gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        return serviceHelper.makeGameResponse(GameResponse_Full.class, game, authentication);
    }

    public Game getGameById_entity(long id) {
        return gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
    }

    @Transactional
    public EntityModel<GameResponse_Full> addGame(GameRequest gameRequest, Authentication authentication) {
        if (gameRepository.findGameByGameName(gameRequest.getName()).isPresent()) throw new GameException("Game already exists");
        Game newGame = gameRequest.toModel();

        newGame.setDevelopers(gameRequest.getDeveloperIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        newGame.setPublishers(gameRequest.getPublisherIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        newGame.setDiscounts(gameRequest.getDiscountIds().stream().map(aLong -> discountService.getDiscountById_entity(aLong, authentication)).collect(Collectors.toSet()));
        newGame.setCategories(gameRequest.getCategoryIds().stream().map(aLong -> categoryService.getCategoryById_entity(aLong, authentication)).collect(Collectors.toSet()));
        List<GameImage> newGameImages = gameRequest.getGameImagesRequest().stream().map(image -> new GameImage(image.getImageName(), StaticHelper.convertToBlob(image.getImage()), newGame)).toList();
        newGame.setGameImages(new HashSet<>(newGameImages));

        Game newCreatedGame = gameRepository.save(newGame);
        return serviceHelper.makeGameResponse(GameResponse_Full.class, newCreatedGame, authentication);
    }

    @Transactional
    public EntityModel<GameResponse_Full> updateGame(long id, GameRequest gameRequest, Authentication authentication) {
        Game gameToUpdate = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        gameToUpdate.setGameName(gameRequest.getName());
        gameToUpdate.setGameDescription(gameRequest.getDescription());
        gameToUpdate.setGameBasePrice(gameRequest.getPrice());
        gameToUpdate.setReleaseDate(gameRequest.getReleaseDate());
        gameToUpdate.setDevelopers(gameRequest.getDeveloperIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        gameToUpdate.setPublishers(gameRequest.getPublisherIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        gameToUpdate.setDiscounts(gameRequest.getDiscountIds().stream().map(aLong -> discountService.getDiscountById_entity(aLong, authentication)).collect(Collectors.toSet())); 
        gameToUpdate.setCategories(gameRequest.getCategoryIds().stream().map(aLong -> categoryService.getCategoryById_entity(aLong, authentication)).collect(Collectors.toSet()));
    
        Game updatedGame = gameRepository.save(gameToUpdate);
        cacheHelper.updateCacheSelective(updatedGame, "gameCache", "gameListCache");
        return serviceHelper.makeGameResponse(GameResponse_Full.class, updatedGame, authentication);
    }

    @Transactional
    public void deleteGame(long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        cacheHelper.deleteCacheSelective(game, "gameCache", "gameListCache");
        gameRepository.deleteById(id);
    }

    @Transactional
    public List<EntityModel<GameResponse_Minimal>> getGamesPurchased(Authentication authentication) {
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        return boughtLibraryRepository.findPurchasedGames(authUserDetail.getId()).stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
    }

    @Transactional
    public List<EntityModel<GameResponse_Minimal>> getDevOwnedGames(Authentication authentication) {
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        DevOwnedLibrary devOwnedLibrary = devLibraryRepository.findById(authUserDetail.getId()).orElseThrow(() -> new ResourceNotFoundException("Dev Library not found"));
        return devOwnedLibrary.getGames().stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
    }
    
    @Transactional
    public List<EntityModel<GameResponse_Minimal>> getPublisherOwnedGames(Authentication authentication) {
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        PublisherOwnedLibrary publisherOwnedLibrary = publisherLibraryRepository.findById(authUserDetail.getId()).orElseThrow(() -> new ResourceNotFoundException("Publisher Library not found"));
        return publisherOwnedLibrary.getGames().stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
    }
    
    @Transactional
    public List<EntityModel<GameResponse_Minimal>> getGameReviews(long id, Authentication authentication) {
        // Todo: WIP
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        return boughtLibraryRepository.findPurchasedGames(authUserDetail.getId()).stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
    }

}
