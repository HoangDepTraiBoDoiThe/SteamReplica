package com.example.steamreplica.dtos.response;

import com.example.steamreplica.dtos.response.game.ImageResponse;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.model.game.DLC.DLCImage;
import org.springframework.hateoas.EntityModel;

public class DlcImageResponse extends ImageResponse {
    private EntityModel<DlcResponse_Basic> dlc;

    public DlcImageResponse(DLCImage dlcImage, EntityModel<DlcResponse_Basic> dlc) {
        super(dlcImage.getId(), dlcImage.getImageName(), dlcImage.getImage());
        this.dlc = dlc;
    }
}
