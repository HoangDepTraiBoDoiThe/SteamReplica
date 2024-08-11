package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findGameByGameName(String gameName);
}
