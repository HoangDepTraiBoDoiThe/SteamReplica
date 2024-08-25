package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.Game;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Basic extends GameResponse_Minimal {
    LocalDate releaseDate;
    List<?> discounts;
    List<EntityModel<BaseResponse>> categories;

    public GameResponse_Basic(Game game, List<?> discounts, List<EntityModel<BaseResponse>> categories) {
        super(game);
        this.releaseDate = game.getReleaseDate();
        this.discounts = discounts;
        this.categories = categories;
    }
}
