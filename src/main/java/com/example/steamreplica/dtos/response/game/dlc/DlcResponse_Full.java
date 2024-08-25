package com.example.steamreplica.dtos.response.game.dlc;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlcResponse_Full extends DlcResponse_Basic {
    private String dlcDescription;
    private List<EntityModel<BaseResponse>> discounts;
    private List<EntityModel<BaseResponse>> gameImages;
    private EntityModel<BaseResponse> game;

    public DlcResponse_Full(DLC dlc, List<EntityModel<BaseResponse>> discounts, List<EntityModel<BaseResponse>> gameImages, EntityModel<BaseResponse> game) {
        super(dlc);
        this.discounts = discounts;
        this.gameImages = gameImages;
        this.game = game;
        this.dlcDescription = dlc.getDlcDescription();
    }
}
