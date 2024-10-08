package com.example.steamreplica.model.purchasedLibrary.game;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameReview;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PurchasedGame extends BaseCacheableModel {
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
    @JsonBackReference
    @OneToOne(orphanRemoval = true, mappedBy = "purchasedGame")
    private GameReview gameReview;
}
