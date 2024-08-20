package com.example.steamreplica.dtos.response.game.dlc;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlcResponse_Basic extends BaseResponse {
    private String name;
    private BigDecimal dlcBasePrice;
    private String dlcThumbnail;

    public DlcResponse_Basic(DLC dlc) {
        super(dlc.getId());
        this.name = dlc.getDlcName();
        this.dlcBasePrice = dlc.getDlcBasePrice();
        this.dlcThumbnail = StaticHelper.convertBlobToString(dlc.getDlcThumbnail());
    }
}
