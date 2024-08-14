package com.example.steamreplica.model.game;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameImage {
    @Id
    @GeneratedValue
    private long id;

    private String imageName;

    @Lob
    private Blob image;

    public GameImage(String imageName, Blob image, Game game) {
        this.imageName = imageName;
        this.image = image;
        this.game = game;
    }

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
}
