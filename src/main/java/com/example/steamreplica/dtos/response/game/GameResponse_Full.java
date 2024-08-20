package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.model.game.Game;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Full extends GameResponse_Basic {
    List<?> developer;
    List<?> publisher;
    List<?> gameImages;

    public GameResponse_Full(Game game, List<?> developer, List<?> publisher, List<?> discounts, List<?> categories, List<?> gameImages) {
        super(game, discounts, categories);
        this.developer = developer;
        this.publisher = publisher;
        this.gameImages = gameImages;
    }
}
