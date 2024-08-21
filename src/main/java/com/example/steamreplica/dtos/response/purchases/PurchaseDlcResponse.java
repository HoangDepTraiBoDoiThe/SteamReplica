package com.example.steamreplica.dtos.response.purchases;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
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
public class PurchaseDlcResponse extends BaseResponse {
    private BigDecimal basePriceAtTheTime;
    private EntityModel<DlcResponse_Basic> dlc;

    public PurchaseDlcResponse(PurchasedDLC purchasedDLC, EntityModel<DlcResponse_Basic> dlc) {
        super(purchasedDLC.getId());
        this.basePriceAtTheTime = purchasedDLC.getPriceAtTheTime();
        this.dlc = dlc;
    }
}
