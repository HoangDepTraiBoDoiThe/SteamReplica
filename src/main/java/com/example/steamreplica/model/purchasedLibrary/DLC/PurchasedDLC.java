package com.example.steamreplica.model.purchasedLibrary.DLC;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.DLC.DLCReview;
import com.example.steamreplica.model.purchasedLibrary.PurchaseTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchased_dlc")
public class PurchasedDLC {
    @Id
    @GeneratedValue
    private long id;

    @PositiveOrZero
    private BigDecimal priceAtTheTime;

    @ManyToOne
    @JoinColumn(name = "transaction_Id", referencedColumnName = "id")
    private PurchaseTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "dlc_Id", referencedColumnName = "id")
    private DLC dlc;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, mappedBy = "purchasedDLC")
    private Set<PurchasedDLCDiscount> purchasedDlcDiscounts;

    @OneToOne(orphanRemoval = true, mappedBy = "purchasedDLC", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private DLCReview DLCReview;
}
