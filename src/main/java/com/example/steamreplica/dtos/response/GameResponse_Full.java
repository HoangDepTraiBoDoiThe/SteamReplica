package com.example.steamreplica.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameResponse_Full extends ResponseBase {
    String description;

    LocalDate releaseDate;

    BigDecimal price;
    CollectionModel<?> developer;
    CollectionModel<?> publisher;
    CollectionModel<?> discounts;
    CollectionModel<?> categories;
    CollectionModel<?> gameImages;
    String gameThumbnail;

    public GameResponse_Full(long id, String name, String description, LocalDate releaseDate, BigDecimal price, CollectionModel<?> developer, CollectionModel<?> publisher, CollectionModel<?> discounts, CollectionModel<?> categories, CollectionModel<?> gameImages, String gameThumbnail) {
        super(id, name);
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.developer = developer;
        this.publisher = publisher;
        this.discounts = discounts;
        this.categories = categories;
        this.gameImages = gameImages;
        this.gameThumbnail = gameThumbnail;
    }

    public GameResponse_Full(String description, LocalDate releaseDate, BigDecimal price, CollectionModel<?> developer, CollectionModel<?> publisher, CollectionModel<?> discounts, CollectionModel<?> categories, CollectionModel<?> gameImages, String gameThumbnail) {
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.developer = developer;
        this.publisher = publisher;
        this.discounts = discounts;
        this.categories = categories;
        this.gameImages = gameImages;
        this.gameThumbnail = gameThumbnail;
    }
}
