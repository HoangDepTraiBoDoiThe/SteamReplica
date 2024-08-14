package com.example.steamreplica.dtos.request;


import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.util.StaticHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRequest {
        @NotBlank(message = "Game name cannot be empty")
        String name;

        String description;

        Set<Long> developerIds;
        Set<Long> publisherIds;
        Set<Long> discountIds;
        Set<Long> categoryIds;
        List<GameImageRequest> gameImagesRequest;
        String gameThumbNail;

        @PastOrPresent(message = "Release date must be in the past or present")
        LocalDate releaseDate;

        @NotNull(message = "Game base price cannot be null")
        @PositiveOrZero(message = "Game base price must be zero(Free) or positive")
        BigDecimal price;


        public Game toModel() {
                Game game = new Game(name, price, description, releaseDate, StaticHelper.convertToBlob(gameThumbNail));
                return game;
        }
}
