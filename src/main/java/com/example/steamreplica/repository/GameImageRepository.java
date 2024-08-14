package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.GameImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GameImageRepository extends JpaRepository<GameImage, Long> {
    
    @Query("select gi from GameImage gi where  gi.game.id = :gameId")
    Collection<GameImage> findAllByGameId(@Param("gameId") long id);
}
