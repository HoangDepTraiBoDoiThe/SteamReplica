package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.GameImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameImageResponse extends ImageResponse {
    private EntityModel<BaseResponse> game;

    public GameImageResponse(GameImage gameImage, EntityModel<BaseResponse> game) {
        super(gameImage.getId(), gameImage.getImageName(), gameImage.getImage());
        this.game = game;
    }
}
