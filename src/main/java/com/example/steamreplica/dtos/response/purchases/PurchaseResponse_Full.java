package com.example.steamreplica.dtos.response.purchases;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse_Full extends PurchaseResponse_Basic {
    private EntityModel<UserResponse_Minimal> buyer;
    private List<EntityModel<PurchaseGameResponse>> purchasedGames;
    private List<EntityModel<PurchaseDlcResponse>> purchasedDlc;

    public PurchaseResponse_Full(Purchase purchase, BigDecimal totalPrice, EntityModel<UserResponse_Minimal> buyer, List<EntityModel<PurchaseGameResponse>> purchasedGames, List<EntityModel<PurchaseDlcResponse>> purchasedDlc, double additionalDiscountPercent) {
        super(purchase, totalPrice, additionalDiscountPercent);
        this.purchasedGames = purchasedGames;
        this.purchasedDlc = purchasedDlc;
        this.buyer = buyer;
    }
}
