package com.example.steamreplica.service;

import com.example.steamreplica.controller.GameController;
import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.event.CategoryUpdateEvent;
import com.example.steamreplica.event.DiscountUpdateEvent;
import com.example.steamreplica.event.GameUpdateEvent;
import com.example.steamreplica.event.UserUpdateEvent;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.*;
import com.example.steamreplica.service.exception.GameException;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.IMyHelper;
import com.example.steamreplica.util.ServiceHelper;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserService userService;
    private final DevOwnedLibraryService devOwnedLibraryService;
    private final PublisherOwnedLibraryService publisherOwnedLibraryService;
    private final DiscountService discountService;
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final DevLibraryRepository devLibraryRepository;
    private final PublisherLibraryRepository publisherLibraryRepository;
    private final CategoryService categoryService;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;

    private final String GAME_LIST_CACHE = "gameListCache";
    private final String GAME_CACHE = "gameCache";
    private final String GAME_PAGINATION_CACHE_PREFIX = "gamePaginationCache";
    private final String NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX = "newAndTrending";
    private final String TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX = "topSeller";
    private final String GAME_OF_CATEGORY_PAGINATION_CACHE_PREFIX = "gamesOfCategory";
    private final String SPECIAL_GAME_PAGINATION_CACHE_PREFIX = "Special";
    private final String DEV_OWNING_GAME_PAGINATION_CACHE_PREFIX = "devOwningGame";
    private final String PUBLISHER_OWNING_GAME_PAGINATION_CACHE_PREFIX = "publisherOwningGame";
    private final Integer PAGE_RANGE = 10;
    private final Integer PAGE_SIZE = 10;

    @EventListener
    public void userUpdated(UserUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                GAME_CACHE,
                List.of(NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX, TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX, SPECIAL_GAME_PAGINATION_CACHE_PREFIX, GAME_PAGINATION_CACHE_PREFIX),
                List.of(GAME_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    Game game = (Game) entity;
                    User user = userService.findUsersWithById_entityFull((Long) id);
                    boolean matched = user.getPublisherOwnedLibrary().getGames().stream().anyMatch(g -> Objects.equals(g.getId(), game.getId())) || 
                            user.getDevOwnedLibrary().getGames().stream().anyMatch(g -> Objects.equals(g.getId(), game.getId()));
                    return matched;
                });
    }
    @EventListener
    public void categoryUpdated(CategoryUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                GAME_CACHE,
                List.of(NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX, TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX, SPECIAL_GAME_PAGINATION_CACHE_PREFIX, GAME_PAGINATION_CACHE_PREFIX),
                List.of(GAME_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    Game game = (Game) entity;
                    Category category = categoryService.getCategoryById_entityFull((Long) id);
                    return category.getGames().stream().anyMatch(g -> Objects.equals(g.getId(), game.getId()));
                });
    }
    @EventListener
    public void discountUpdated(DiscountUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                GAME_CACHE,
                List.of(NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX, TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX, SPECIAL_GAME_PAGINATION_CACHE_PREFIX, GAME_PAGINATION_CACHE_PREFIX),
                List.of(GAME_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    Game game = (Game) entity;
                    Discount discount = discountService.getDiscountById_entityFull((Long) id);
                    return discount.getDiscountedGames().stream().anyMatch(g -> Objects.equals(g.getId(), game.getId()));
                });
    }

    public CollectionModel<EntityModel<GameResponse_Basic>> getGamesOfCategory(int page, long categoryId, Authentication authentication) {
        List<Game> games = cacheHelper.getPaginationCache(GAME_OF_CATEGORY_PAGINATION_CACHE_PREFIX, page, gameRepository, repo -> repo.findAllByCategoryId(categoryId, PageRequest.of(page, PAGE_SIZE)).getContent());
        CollectionModel<EntityModel<GameResponse_Basic>> entityModels = serviceHelper.makeGameResponse_CollectionModel(GameResponse_Basic.class, games, authentication);

        return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> methodOn(GameController.class).getGamesOfCategory(currentPage, categoryId, authentication));
    }

    public CollectionModel<EntityModel<GameResponse_Basic>> getNewAndTrendingGames(int page, Authentication authentication) {
        List<Game> games = cacheHelper.getPaginationCache(NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX, page, gameRepository, repo -> repo.findAllByOrderByDownloadedCountDescReleaseDateDesc(PageRequest.of(page, PAGE_SIZE)).toList());
        CollectionModel<EntityModel<GameResponse_Basic>> entityModels = serviceHelper.makeGameResponse_CollectionModel(GameResponse_Basic.class, games, authentication);

        return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> methodOn(GameController.class).getNewAndTrendingGames(currentPage, authentication));
    }

    public CollectionModel<EntityModel<GameResponse_Basic>> getTopSellerGames(int page, Authentication authentication) {
        List<Game> games = cacheHelper.getPaginationCache(TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX, page, gameRepository, repo -> repo.findAllByOrderByDownloadedCountDesc(PageRequest.of(page, PAGE_SIZE)).toList());
        CollectionModel<EntityModel<GameResponse_Basic>> entityModels = serviceHelper.makeGameResponse_CollectionModel(GameResponse_Basic.class, games, authentication);

        return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> methodOn(GameController.class).getTopSellerGames(currentPage, authentication));
    }

    public CollectionModel<EntityModel<GameResponse_Basic>> getSpecialGames(int page, Authentication authentication) {
        List<Game> mostDownloadedGames = cacheHelper.getPaginationCache(SPECIAL_GAME_PAGINATION_CACHE_PREFIX, page, gameRepository, repo -> repo.findAllByOrderByDownloadedCountDescWithAvailableDiscounts(PageRequest.of(page, PAGE_SIZE)).toList());
        CollectionModel<EntityModel<GameResponse_Basic>> entityModels = serviceHelper.makeGameResponse_CollectionModel(GameResponse_Basic.class, mostDownloadedGames, authentication);

        return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> methodOn(GameController.class).getSpecialGames(currentPage, authentication));
    }

    @Transactional
    public CollectionModel<EntityModel<GameResponse_Basic>> getDevOwningGames(int page, long user_id, Authentication authentication) {
        List<Game> mostDownloadedGames = cacheHelper.getPaginationCache(DEV_OWNING_GAME_PAGINATION_CACHE_PREFIX, page, gameRepository, repo -> repo.findAllByDevOwner(user_id, PageRequest.of(page, PAGE_SIZE)).toList());
        CollectionModel<EntityModel<GameResponse_Basic>> entityModels =  serviceHelper.makeGameResponse_CollectionModel(GameResponse_Basic.class, mostDownloadedGames, authentication);

        return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> methodOn(GameController.class).getGamesBelongToDev(currentPage, user_id, authentication));
    }

    @Transactional
    public CollectionModel<EntityModel<GameResponse_Basic>> getPublisherOwningGames(int page, long user_id, Authentication authentication) {
        List<Game> mostDownloadedGames = cacheHelper.getPaginationCache(PUBLISHER_OWNING_GAME_PAGINATION_CACHE_PREFIX, page, gameRepository, repo -> repo.findAllByPublisherOwner(user_id, PageRequest.of(page, PAGE_SIZE)).toList());
        CollectionModel<EntityModel<GameResponse_Basic>> entityModels = serviceHelper.makeGameResponse_CollectionModel(GameResponse_Basic.class, mostDownloadedGames, authentication);

        return serviceHelper.addLinksToPaginationResponse(entityModels, page, currentPage -> methodOn(GameController.class).getGamesBelongToPublisher(currentPage, user_id, authentication));
    }
    
    @Transactional
    public EntityModel<GameResponse_Full> getGameById(long id, Authentication authentication) {
        Game game = cacheHelper.getCache(GAME_CACHE, id, gameRepository, repo -> repo.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id))));
        return serviceHelper.makeGameResponse(GameResponse_Full.class, game, authentication);
    }

    public Game getGameById_entity(long id) {
        return gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
    }

    @Transactional
    public EntityModel<GameResponse_Full> addGame(GameRequest gameRequest, Authentication authentication) {
        if (gameRepository.findGameByGameName(gameRequest.getName()).isPresent())
            throw new GameException("Game already exists");
        Game newGame = gameRequest.toModel();

        newGame.setDevOwners(gameRequest.getDeveloperIds().stream().map(devOwnedLibraryService::findByUserId_entity).collect(Collectors.toSet()));
        newGame.setPublisherOwners(gameRequest.getPublisherIds().stream().map(publisherOwnedLibraryService::findByUserId_entity).collect(Collectors.toSet()));
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
        gameToUpdate.setDevOwners(gameRequest.getDeveloperIds().stream().map(devOwnedLibraryService::findByUserId_entity).collect(Collectors.toSet()));
        gameToUpdate.setPublisherOwners(gameRequest.getPublisherIds().stream().map(publisherOwnedLibraryService::findByUserId_entity).collect(Collectors.toSet()));
        gameToUpdate.setDiscounts(gameRequest.getDiscountIds().stream().map(aLong -> discountService.getDiscountById_entity(aLong, authentication)).collect(Collectors.toSet()));
        gameToUpdate.setCategories(gameRequest.getCategoryIds().stream().map(aLong -> categoryService.getCategoryById_entity(aLong, authentication)).collect(Collectors.toSet()));

        Game updatedGame = gameRepository.save(gameToUpdate);
        cacheHelper.updateCache(updatedGame, GAME_CACHE, GAME_LIST_CACHE);
        cacheHelper.updatePaginationCache(updatedGame, PAGE_RANGE, 
                NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX, 
                TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX,
                DEV_OWNING_GAME_PAGINATION_CACHE_PREFIX,
                PUBLISHER_OWNING_GAME_PAGINATION_CACHE_PREFIX,
                SPECIAL_GAME_PAGINATION_CACHE_PREFIX, 
                GAME_OF_CATEGORY_PAGINATION_CACHE_PREFIX);
        cacheHelper.publishCacheEvent(new GameUpdateEvent(this, updatedGame.getId()));
        return serviceHelper.makeGameResponse(GameResponse_Full.class, updatedGame, authentication);
    }

    @Transactional
    public void deleteGame(long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        cacheHelper.deleteCaches(GAME_CACHE, game.getId(), GAME_LIST_CACHE);
        cacheHelper.deletePaginationCache(game.getId(), PAGE_RANGE, 
                NEW_AND_TRENDING_GAME_PAGINATION_CACHE_PREFIX, 
                TOP_SELLER_GAME_PAGINATION_CACHE_PREFIX,
                DEV_OWNING_GAME_PAGINATION_CACHE_PREFIX,
                PUBLISHER_OWNING_GAME_PAGINATION_CACHE_PREFIX, 
                SPECIAL_GAME_PAGINATION_CACHE_PREFIX);
        gameRepository.deleteById(id);
    }

    @Transactional
    public List<EntityModel<GameResponse_Minimal>> getGamesPurchased(long user_id, Authentication authentication) {
        return boughtLibraryRepository.findPurchasedGames(user_id).stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
    }

    @Transactional
    public List<EntityModel<GameResponse_Minimal>> getGameReviews(long id, Authentication authentication) {
        // Todo: WIP
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        return boughtLibraryRepository.findPurchasedGames(authUserDetail.getId()).stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
    }

    @Transactional
    public Game findGameWithById_withDLC(long id) {
        return gameRepository.findById_withDLC(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
    }

    @Transactional
    public Game findGameWithById_withPurchasedGame(long id) {
        return gameRepository.findById_withPurchasedGame(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
    }
}