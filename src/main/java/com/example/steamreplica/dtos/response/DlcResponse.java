package com.example.steamreplica.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlcResponse {
    private Long id;
    private String dlcName;
    private String dlcDescription;
    private BigDecimal dlcBasePrice;
    private String dlcThumbnail;
}
