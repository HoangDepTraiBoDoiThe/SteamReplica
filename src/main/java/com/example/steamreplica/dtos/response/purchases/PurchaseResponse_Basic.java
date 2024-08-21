package com.example.steamreplica.dtos.response.purchases;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse_Basic extends BaseResponse {
    private ZonedDateTime TransactionDate;
    private String transactionType;
    private BigDecimal totalPrice;
    private double additionalDiscountPercent;
    public PurchaseResponse_Basic(Purchase purchase, BigDecimal totalPrice, double additionalDiscountPercent) {
        super(purchase.getId());
        this.TransactionDate = purchase.getTransactionDate();
        this.totalPrice = totalPrice;
        this.transactionType = purchase.getTransactionType();
        this.additionalDiscountPercent = additionalDiscountPercent;
    }
}
