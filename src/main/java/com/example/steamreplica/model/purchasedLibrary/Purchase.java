package com.example.steamreplica.model.purchasedLibrary;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Purchase extends BaseCacheableModel {
    private ZonedDateTime TransactionDate;

    @NotBlank
    @Column(nullable = false)
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "boughtLibrary", referencedColumnName = "id")
    private BoughtLibrary boughtLibrary;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private Set<PurchasedGame> purchasedGames = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private Set<PurchasedDLC> purchasedDLCs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "additionalDiscount_id")
    private Discount additionalDiscount;
}
