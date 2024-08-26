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
    Optional<Game> findById_full(long id);

    @Query("SELECT g FROM Game g JOIN g.categories c WHERE c.id = :categoryId")
    Page<Game> findAllByCategoryId(@Param("categoryId") long categoryId, Pageable pageable);
    
    Page<Game> findAllByOrderByReleaseDate(Pageable pageable);
    Page<Game> findAllByOrderByDownloadedCountDesc(Pageable pageable);
    Page<Game> findAllByOrderByDownloadedCountDescReleaseDateDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"gameImages"})
    Optional<Game> findGameWithAllImagesById(long id);
}
