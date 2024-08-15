package com.example.steamreplica.model.game.discount;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLCDiscount;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGameDiscount;
import com.example.steamreplica.model.game.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @Column(nullable = false)
    private String discountName;
    @Column(unique = true, nullable = false)
    private String discountCode;
    private String discountDescription;
    @Column(nullable = false)
    private BigDecimal discountPercent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "discounts")
    private Set<Game> discountedGames = new HashSet<>();
    
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<PurchasedGameDiscount> purchasedGameDiscounts;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "discounts")
    private Set<DLC> discountedDlc = new HashSet<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<PurchasedDLCDiscount> purchasedDLCDiscounts = new HashSet<>();

    public Discount(String discountName, String discountCode, String discountDescription, BigDecimal discountPercent) {
        this.discountName = discountName;
        this.discountCode = discountCode;
        this.discountDescription = discountDescription;
        this.discountPercent = discountPercent;
    }
}
