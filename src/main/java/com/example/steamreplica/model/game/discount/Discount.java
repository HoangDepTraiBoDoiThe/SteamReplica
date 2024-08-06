package com.example.steamreplica.model.game.discount;

import com.example.steamreplica.model.purchasedLibrary.PurchasedGameDiscount;
import com.example.steamreplica.model.game.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "Discount can not be empty.")
    @Size(max = 200, message = "Discount's name is too long.")
    private String discountName;

    @NotBlank(message = "Discount code can not be empty")
    @Column(unique = true, nullable = false)
    private String discountCode;

    private String discountDescription;

    @PositiveOrZero(message = "Discount percent value must be a positive value.")
    private BigDecimal discountPercent;

    @ManyToMany(mappedBy = "discounts")
    private Set<Game> games = new HashSet<>();
    
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<PurchasedGameDiscount> purchasedGameDiscounts;
}
