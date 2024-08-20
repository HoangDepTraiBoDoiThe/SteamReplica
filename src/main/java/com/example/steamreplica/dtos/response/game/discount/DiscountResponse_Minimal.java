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
public class DiscountResponse_Minimal extends BaseResponse {
    private String discountName;
    private BigDecimal discountPercent;

    public DiscountResponse_Minimal(Discount discount) {
        super(discount.getId());
        this.discountName = discount.getDiscountName();
        this.discountPercent = discount.getDiscountPercent();
    }
}
