package com.example.steamreplica.model.purchasedLibrary.game;

import com.example.steamreplica.model.game.discount.Discount;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PurchasedGameDiscount {
    @EmbeddedId
    PurchasedGameDiscountKey id;

    @Column(nullable = false)
    @PositiveOrZero(message = "Discount percent at the time must be positive or zero")
    private BigDecimal discountPercentAtTheTime;

    @ManyToOne
    @MapsId("discountId")
    private Discount discount;
    
    @ManyToOne
    @MapsId("purchasedGameId")
    private PurchasedGame purchasedGame;
}
