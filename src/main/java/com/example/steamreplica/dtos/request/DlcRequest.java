package com.example.steamreplica.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlcRequest {
    @NotBlank(message = "DLC name is required")
    @Length(max = 100, message = "DLC name must be less than or equal to 100 characters. Any more detail can be added in the description.")
    private String dlcName;
    private String dlcDescription;

    @PositiveOrZero(message = "DLC base price must be positive or zero (Free)")
    private BigDecimal dlcBasePrice;

    @NotBlank(message = "DLC thumbnail is required")
    private String dlcThumbnail;
}
