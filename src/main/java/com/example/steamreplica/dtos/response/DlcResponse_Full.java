package com.example.steamreplica.dtos.response;

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
public class DlcResponse_Full extends ResponseBase{
    private String dlcDescription;
    private BigDecimal dlcBasePrice;
    private String dlcThumbnail;
    private CollectionModel<?> discounts;
    private CollectionModel<?> gameImages;
    private EntityModel<?> game;

    public DlcResponse_Full(DLC dlc) {
        super(dlc.getId(), dlc.getDlcName());
        this.id = dlc.getId();
        this.name = dlc.getDlcName();
        this.dlcDescription = dlc.getDlcDescription();
        this.dlcBasePrice = dlc.getDlcBasePrice();
        this.dlcThumbnail = StaticHelper.convertBlobToString(dlc.getDlcThumbnail());
    }
}
