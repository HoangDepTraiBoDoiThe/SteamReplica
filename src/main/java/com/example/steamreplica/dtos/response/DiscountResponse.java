package com.example.steamreplica.dtos.response;
import com.example.steamreplica.model.game.discount.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponse {
    private long id;
    private String discountName;
    private String discountCode;
    private String discountDescription;
    private BigDecimal discountPercent;

    public DiscountResponse(Discount discount) {
        this.id = discount.getId();
        this.discountName = discount.getDiscountName();
        this.discountCode = discount.getDiscountCode();
        this.discountDescription = discount.getDiscountDescription();
        this.discountPercent = discount.getDiscountPercent();
    }
}
