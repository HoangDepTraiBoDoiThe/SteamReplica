package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.util.StaticHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Basic extends BaseResponse {
    LocalDate releaseDate;
    BigDecimal price;
    List<?> discounts;
    List<?> categories;
    String gameThumbnail;

    public GameResponse_Basic(Game game, List<?> discounts, List<?> categories) {
        super(game.getId());
        this.releaseDate = game.getReleaseDate();
        this.price = game.getGameBasePrice();
        this.discounts = discounts;
        this.categories = categories;
        this.gameThumbnail = StaticHelper.convertBlobToString(game.getGameThumbnail());
    }
}
