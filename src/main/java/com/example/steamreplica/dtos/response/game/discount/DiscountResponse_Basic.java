package com.example.steamreplica.dtos.response.game.discount;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.discount.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponse_Basic extends DiscountResponse_Minimal {
    private String discountDescription;

    public DiscountResponse_Basic(Discount discount) {
        super(discount);
        this.discountDescription = discount.getDiscountDescription();
    }
}
