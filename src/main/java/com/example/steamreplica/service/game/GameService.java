package com.example.steamreplica.service.game;

import com.example.steamreplica.service.game.exception.GameException;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.game.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getGameById(long id) {
        return gameRepository.findById(id).orElseThrow(() -> new GameException(String.format(Long.toString(id), "Game with this id [%s] not found")));
    }
    
    public Game addGame(Game game) {
        // Todo: publishers, devs are required.
        gameRepository.findGameByGameName(game.getGameName()).orElseThrow(() -> new GameException("Game already exists"));
        return gameRepository.save(game);
    }

    public void deleteGame(long id) {
        gameRepository.deleteById(id);
    }

    public Game updateGame(long id, Game game) {
        // Todo: publishers, devs should be put into the consideration.
        
        Game gameToUpdate = gameRepository.findById(id).orElseThrow(() -> new GameException("Game not found"));
        gameToUpdate.setGameName(game.getGameName());
        gameToUpdate.setGameDescription(game.getGameDescription());
        gameToUpdate.setGameBasePrice(game.getGameBasePrice());
        gameToUpdate.setReleaseDate(game.getReleaseDate());
        gameToUpdate.setGameImages(game.getGameImages());
        gameToUpdate.setCategories(game.getCategories());
        return gameRepository.save(gameToUpdate);
    }
}
