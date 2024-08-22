package com.example.steamreplica.model.game;

import com.example.steamreplica.model.BaseCacheableModel;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameImage extends BaseCacheableModel {
    private String imageName;

    @Lob
    private Blob image;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
}
