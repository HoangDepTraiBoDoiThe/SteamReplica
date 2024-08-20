package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.model.game.Game;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Full extends GameResponse_Basic {
    CollectionModel<?> developer;
    CollectionModel<?> publisher;
    CollectionModel<?> gameImages;

    public GameResponse_Full(Game game, CollectionModel<?> developer, CollectionModel<?> publisher, CollectionModel<?> discounts, CollectionModel<?> categories, CollectionModel<?> gameImages) {
        super(game, discounts, categories);
        this.developer = developer;
        this.publisher = publisher;
        this.gameImages = gameImages;
    }
}
