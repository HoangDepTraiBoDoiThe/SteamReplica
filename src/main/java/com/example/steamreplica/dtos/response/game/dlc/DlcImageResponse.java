package com.example.steamreplica.dtos.response.game.dlc;

import com.example.steamreplica.dtos.response.game.ImageResponse;
import com.example.steamreplica.model.game.GameImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlcImageResponse extends ImageResponse {
    private EntityModel<DlcResponse_Basic> dlc;

    public DlcImageResponse(GameImage gameImage, EntityModel<DlcResponse_Basic> dlc) {
        super(gameImage.getId(), gameImage.getImageName(), gameImage.getImage());
        this.dlc = dlc;
    }
}
