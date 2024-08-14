package com.example.steamreplica.dtos.request;


import com.example.steamreplica.model.game.Game;
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

        @NotBlank
        @PastOrPresent(message = "Release date must be in the past or present")
        LocalDate releaseDate;
//        String category,

        @NotNull(message = "Game base price cannot be null")
        @PositiveOrZero(message = "Game base price must be zero(Free) or positive")
        @Length(max = 1000, message = "Game base price must be less than 1000 characters")
        BigDecimal price;

//        String image

        public Game toModel() {
                Game game = new Game(name, price, description, releaseDate);
                return game;
        }
}
