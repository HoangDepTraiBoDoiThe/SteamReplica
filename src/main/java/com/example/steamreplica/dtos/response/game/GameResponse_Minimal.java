package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.util.StaticHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Minimal extends BaseResponse {
    BigDecimal price;
    String name;
    String gameThumbnail;

    public GameResponse_Minimal(Game game) {
        super(game.getId());
        this.name = game.getGameName();
        this.price = game.getGameBasePrice();
        this.gameThumbnail = StaticHelper.convertBlobToString(game.getGameThumbnail());
    }
}
