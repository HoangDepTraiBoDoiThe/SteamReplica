package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameImageResponse_Basic extends BaseResponse {
    private String imageName;
    private String image;

    public GameImageResponse_Basic(GameImage gameImage) {
        super(gameImage.getId());
        this.imageName = gameImage.getImageName();
        this.image = StaticHelper.convertBlobToString(gameImage.getImage());
    }
}
