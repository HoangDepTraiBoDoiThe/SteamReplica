package com.example.steamreplica.model.purchasedLibrary.game;

import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameReview;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

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

    @PositiveOrZero
    private double discountPercent;

    public PurchasedGame(Game game, double discountPercent) {
        this.gameBasePriceAtTheTime = game.getGameBasePrice();
        this.game = game;
        this.discountPercent = discountPercent;
    }

    @ManyToOne
    @JoinColumn(name = "transaction_Id", referencedColumnName = "id")
    private Purchase transaction;

    @ManyToOne
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(orphanRemoval = true, mappedBy = "purchasedGame")
    private GameReview gameReview;
}
