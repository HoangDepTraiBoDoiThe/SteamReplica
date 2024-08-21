package com.example.steamreplica.model.purchasedLibrary.DLC;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.DLC.DLCReview;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

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

    public PurchasedDLC(DLC dlc) {
        this.priceAtTheTime = dlc.getDlcBasePrice();
        this.dlc = dlc;
    }

    @ManyToOne
    @JoinColumn(name = "transaction_Id", referencedColumnName = "id")
    private Purchase transaction;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "dlc_Id", referencedColumnName = "id")
    private DLC dlc;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(orphanRemoval = true, mappedBy = "purchasedDLC", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private DLCReview DLCReview;
}
