package com.example.steamreplica.model.boughtLibrary;

import com.example.steamreplica.constants.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private long id;

    private ZonedDateTime TransactionDate;
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "boughtLibrary", referencedColumnName = "id")
    private BoughtLibrary boughtLibrary;

    @OneToMany(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PurchasedGame> purchasedGames = new HashSet<>();
}
