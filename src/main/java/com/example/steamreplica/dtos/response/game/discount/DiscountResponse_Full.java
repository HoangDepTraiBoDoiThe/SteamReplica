package com.example.steamreplica.dtos.response.game.discount;
import com.example.steamreplica.model.game.discount.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponse_Full {
    private long id;
    private String discountName;
    private String discountCode;
    private String discountDescription;
    private BigDecimal discountPercent;
    private List<?> games;


    public DiscountResponse_Full(Discount discount) {
        this.id = discount.getId();
        this.discountName = discount.getDiscountName();
        this.discountCode = discount.getDiscountCode();
        this.discountDescription = discount.getDiscountDescription();
        this.discountPercent = discount.getDiscountPercent();
    }
}
