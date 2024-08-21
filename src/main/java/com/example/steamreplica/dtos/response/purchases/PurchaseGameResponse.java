package com.example.steamreplica.dtos.response.purchases;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseGameResponse extends BaseResponse {
    private BigDecimal basePriceAtTheTime;
    private BigDecimal purchasedPrice;
    private EntityModel<GameResponse_Minimal> game;
    private double discountPercent;

    public PurchaseGameResponse(PurchasedGame purchasedGame, EntityModel<GameResponse_Minimal> gameResponse, double discountPercent, BigDecimal purchasedPrice) {
        super(purchasedGame.getId());
        this.basePriceAtTheTime = purchasedGame.getGameBasePriceAtTheTime();
        this.discountPercent = discountPercent;
        this.purchasedPrice = purchasedPrice;
        this.game = gameResponse;
    }
}
