package com.example.steamreplica.dtos.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {
    @NotBlank(message = "Discount can not be empty.")
    @Size(max = 200, message = "Discount's name is too long.")
    private String discountName;

    @NotBlank(message = "Discount code can not be empty")
    private String discountCode;

    private String discountDescription;

    @PositiveOrZero(message = "Discount percent value must be a positive value.")
    private double discountPercent;
}
