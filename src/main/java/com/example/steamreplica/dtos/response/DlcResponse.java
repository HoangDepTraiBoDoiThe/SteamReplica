package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.util.StaticHelper;
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

    public DlcResponse(DLC dlc) {
        this.id = dlc.getId();
        this.dlcName = dlc.getDlcName();
        this.dlcDescription = dlc.getDlcDescription();
        this.dlcBasePrice = dlc.getDlcBasePrice();
        this.dlcThumbnail = StaticHelper.convertBlobToString(dlc.getDlcThumbnail());
    }
}
