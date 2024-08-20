package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameImageResponse_Full extends GameImageResponse_Basic {
    private EntityModel<?> game;

    public GameImageResponse_Full(GameImage gameImage, EntityModel<?> game) {
        super(gameImage);
        this.game = game;
    }
}
