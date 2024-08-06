package com.example.steamreplica.model.purchasedLibrary;

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
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @PositiveOrZero(message = "Discount percent at the time must be positive or zero")
    private BigDecimal discountPercentAtTheTime;

    @ManyToOne
    @JoinColumn(name = "discount_Id", referencedColumnName = "id")
    private Discount discount;
    
    @ManyToOne
    @JoinColumn(name = "purchasedGame_Id", referencedColumnName = "id")
    private PurchasedGame purchasedGame;
}
