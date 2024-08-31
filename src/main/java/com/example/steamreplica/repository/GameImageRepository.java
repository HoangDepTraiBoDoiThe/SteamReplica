package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.GameImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface GameImageRepository extends JpaRepository<GameImage, Long> {

    @Query("select gi from GameImage gi JOIN FETCH gi.game g JOIN g.publisherOwners where gi.game.id = :gameId")
    Optional<GameImage> findGameImageWithOwner(@Param("gameId") long id);
    
    
    @Query("select gi from GameImage gi where  gi.game.id = :gameId")
    Collection<GameImage> findAllByGameId(@Param("gameId") long id);
}
