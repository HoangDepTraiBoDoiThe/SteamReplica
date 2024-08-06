package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.game.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DLCImage {
    @Id
    @GeneratedValue
    private long id;

    private String imageName;

    @Lob
    private Blob image;

    @ManyToOne
    @JoinColumn
    private DLC dlc;
}
