package com.example.steamreplica.model.purchasedLibrary.game;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class PurchasedGameDiscountKey implements Serializable {
    private long purchasedGameId;
    private long discountId;

    public PurchasedGameDiscountKey(long purchasedGameId, long discountId) {
        this.purchasedGameId = purchasedGameId;
        this.discountId = discountId;
    }
}
