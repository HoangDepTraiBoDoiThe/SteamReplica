package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.BaseCacheableModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DLCImage extends BaseCacheableModel {
    private String imageName;

    @Lob
    private Blob image;

    
    @ManyToOne
    @JoinColumn(name = "dlc_id", referencedColumnName = "id")
    private DLC dlc;
}
