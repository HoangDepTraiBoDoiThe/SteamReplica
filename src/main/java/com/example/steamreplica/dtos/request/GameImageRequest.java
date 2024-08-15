package com.example.steamreplica.dtos.request;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameImageRequest {
    private long id;
    private String imageName;
    private String image;

    public GameImageRequest(String imageName, String image) {
        this.imageName = imageName;
        this.image = image;
    }
}
