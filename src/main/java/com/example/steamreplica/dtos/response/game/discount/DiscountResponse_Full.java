package com.example.steamreplica.dtos.response.game.discount;
import com.example.steamreplica.model.game.discount.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponse_Full extends DiscountResponse_Basic {
    private String discountCode;
    private List<?> games;
    private List<?> dlcs;

    public DiscountResponse_Full(Discount discount, List<?> games, List<?> dlcs) {
        super(discount);
        this.discountCode = discount.getDiscountCode();
        this.games = games;
        this.dlcs = dlcs;
    }
}
