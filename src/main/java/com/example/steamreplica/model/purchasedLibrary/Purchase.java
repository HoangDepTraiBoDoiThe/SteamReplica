package com.example.steamreplica.model.purchasedLibrary;

import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue
    private long id;

    private ZonedDateTime TransactionDate;
    
    @NotBlank
    @Column(nullable = false)
    private String transactionType;

    public Purchase(ZonedDateTime transactionDate, String transactionType, BoughtLibrary boughtLibrary, Set<PurchasedGame> purchasedGames, Set<PurchasedDLC> purchasedDLCs) {
        TransactionDate = transactionDate;
        this.transactionType = transactionType;
        this.boughtLibrary = boughtLibrary;
        this.purchasedGames = purchasedGames;
        this.purchasedDLCs = purchasedDLCs;
    }

    @ManyToOne
    @JoinColumn(name = "boughtLibrary", referencedColumnName = "id")
    private BoughtLibrary boughtLibrary;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PurchasedGame> purchasedGames = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PurchasedDLC> purchasedDLCs = new HashSet<>();
}
