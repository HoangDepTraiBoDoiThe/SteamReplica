package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameImageResponse {
    private long id;
    private long gameId;
    private String imageName;
    private String image;

    public GameImageResponse(long gameId, GameImage gameImage) {
        this.id = gameImage.getId();
        this.imageName = gameImage.getImageName();
        this.image = StaticHelper.convertBlobToString(gameImage.getImage());
        this.gameId = gameId;
    }
}
