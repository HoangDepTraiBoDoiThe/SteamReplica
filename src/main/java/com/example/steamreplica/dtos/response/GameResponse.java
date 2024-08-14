package com.example.steamreplica.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    long id;
    String name;
    String description;

    LocalDate releaseDate;

    BigDecimal price;
    CollectionModel<?> developer;
    CollectionModel<?> publisher;
//        String category,
    //        String image
}
