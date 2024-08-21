package com.example.steamreplica.model.purchasedLibrary.game;

import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameReview;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.Purchases;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PurchasedGame {
    @Id
    @GeneratedValue
    private long id;

    @PositiveOrZero
    private BigDecimal gameBasePriceAtTheTime;

    @ManyToOne
    @JoinColumn(name = "transaction_Id", referencedColumnName = "id")
    private Purchases transaction;

    @ManyToOne
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(orphanRemoval = true, mappedBy = "purchasedGame")
    private GameReview gameReview;
}
