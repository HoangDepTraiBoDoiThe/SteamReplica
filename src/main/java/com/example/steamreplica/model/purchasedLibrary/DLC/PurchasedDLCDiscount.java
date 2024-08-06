package com.example.steamreplica.model.purchasedLibrary.DLC;

import com.example.steamreplica.model.game.discount.Discount;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "purchased_game_discount")
@AllArgsConstructor
@NoArgsConstructor
public class PurchasedDLCDiscount {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @PositiveOrZero(message = "Discount percent at the time must be positive or zero")
    private BigDecimal discountPercentAtTheTime;

    @ManyToOne
    @JoinColumn(name = "discount_Id", referencedColumnName = "id")
    private Discount discount;
    
    @Column(name = "purchased_dlc")
    @ManyToOne
    @JoinColumn(name = "purchasedGame_Id", referencedColumnName = "id")
    private PurchasedDLC purchasedDLC;
}
