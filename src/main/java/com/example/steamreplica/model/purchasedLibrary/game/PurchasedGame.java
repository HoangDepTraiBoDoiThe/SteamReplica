package com.example.steamreplica.model.purchasedLibrary.game;

import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameReview;
import com.example.steamreplica.model.purchasedLibrary.PurchaseTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
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
    private PurchaseTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<PurchasedGameDiscount> purchasedGameDiscounts;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(orphanRemoval = true, mappedBy = "purchasedGame")
    private GameReview gameReview;
}
