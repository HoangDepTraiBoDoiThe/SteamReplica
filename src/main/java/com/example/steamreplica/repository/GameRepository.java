package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findGameByGameName(String gameName);
    @EntityGraph(attributePaths = {"purchasedGame"})
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<Game> findById_withPurchasedGame(long id);
    @EntityGraph(attributePaths = {"dlcs"})
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<Game> findById_withDLC(long id);
    @EntityGraph(attributePaths = {"gameImages"})
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<Game> findById_withWithImages(long id);

    @Query("SELECT g FROM Game g JOIN g.categories c WHERE c.id = :categoryId")
    Page<Game> findAllByCategoryId(@Param("categoryId") long categoryId, Pageable pageable);
    Page<Game> findAllByOrderByReleaseDate(Pageable pageable);
    Page<Game> findAllByOrderByDownloadedCountDesc(Pageable pageable);
    @Query("SELECT g FROM Game g JOIN g.devOwners do WHERE do.user = :user_id")
    Page<Game> findAllByDevOwner(@Param("user_id") long user_id, Pageable pageable);
    @Query("SELECT g FROM Game g JOIN g.publisherOwners do WHERE do.user = :user_id")
    Page<Game> findAllByPublisherOwner(@Param("user_id") long user_id, Pageable pageable);

    @EntityGraph(attributePaths = "publisherOwners")
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<Game> findGamesByIdWithPublisherOwners(long id);
    Page<Game> findAllByOrderByDownloadedCountDescReleaseDateDesc(Pageable pageable);

    @Query("SELECT DISTINCT g FROM Game g " +
            "JOIN g.discounts d " +
            "WHERE d.discountStartDate <= CURRENT_DATE AND d.discountEndDate >= CURRENT_DATE " +
            "ORDER BY g.downloadedCount DESC")
    Page<Game> findAllByOrderByDownloadedCountDescWithAvailableDiscounts(Pageable pageable);

    @EntityGraph(attributePaths = {"gameImages"})
    Optional<Game> findGameWithAllImagesById(long id);

    @EntityGraph(attributePaths = {"gameImages", "categories", "discounts", "purchasedGame", "devOwners", "publisherOwners", "dlcs"})
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<Game> findGameWithAll(long id);

}
