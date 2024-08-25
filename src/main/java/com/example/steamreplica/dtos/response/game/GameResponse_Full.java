package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.Game;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Full extends GameResponse_Basic {
    List<EntityModel<BaseResponse>> developer;
    List<EntityModel<BaseResponse>> publisher;
    List<EntityModel<ImageResponse>> gameImages;

    public GameResponse_Full(Game game, List<EntityModel<BaseResponse>> developer, List<EntityModel<BaseResponse>> publisher, List<EntityModel<BaseResponse>> discounts, List<EntityModel<BaseResponse>> categories, List<EntityModel<ImageResponse>> gameImages) {
        super(game, discounts, categories);
        this.developer = developer;
        this.publisher = publisher;
        this.gameImages = gameImages;
    }
}
