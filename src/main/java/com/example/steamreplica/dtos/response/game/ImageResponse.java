package com.example.steamreplica.dtos.response.game;

import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.util.StaticHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse extends BaseResponse {
    private String imageName;
    private String image;

    public ImageResponse(long id, String imageName, Blob imageBlob) {
        super(id);
        this.imageName = imageName;
        this.image = StaticHelper.convertBlobToString(imageBlob);
    }
}
