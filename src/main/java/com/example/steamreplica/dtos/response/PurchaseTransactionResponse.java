package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.Purchases;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionResponse {
    private long id;
    private ZonedDateTime TransactionDate;
    private String transactionType;
    private BoughtLibrary boughtLibrary;
    // Todo: what games, DLCs
    //    private Set<PurchasedGame> purchasedGames = new HashSet<>();
    //    private Set<PurchasedDLC> purchasedDLCs = new HashSet<>();

    public PurchaseTransactionResponse(Purchases purchases) {
        this.id = purchases.getId();
        this.TransactionDate = purchases.getTransactionDate();
        this.transactionType = purchases.getTransactionType();
        this.boughtLibrary = purchases.getBoughtLibrary();
    }
}
