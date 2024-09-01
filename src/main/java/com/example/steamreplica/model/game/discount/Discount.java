package com.example.steamreplica.model.game.discount;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Discount extends BaseCacheableModel {
    @Column(nullable = false)
    private String discountName;
    @Column(unique = true, nullable = false)
    private String discountCode;
    private String discountDescription;
    @Column(nullable = false)
    private Double discountPercent;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    public Discount(String discountName, String discountCode, String discountDescription, Double discountPercent) {
        this.discountName = discountName;
        this.discountCode = discountCode;
        this.discountDescription = discountDescription;
        this.discountPercent = discountPercent;
    }
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "discounts")
    @JsonBackReference
    private Set<Game> discountedGames = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "discounts")
    @JsonBackReference
    private Set<DLC> discountedDlc = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "additionalDiscount")
    @JsonBackReference("discount_purchases")
    private Set<Purchase> purchases = new HashSet<>();
}
