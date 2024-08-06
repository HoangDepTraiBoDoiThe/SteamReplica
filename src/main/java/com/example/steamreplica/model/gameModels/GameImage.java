package com.example.steamreplica.model.gameModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
}
