package com.example.steamreplica.model.game;

import com.example.steamreplica.model.purchasedLibrary.PurchasedGame;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String review;
    
    @NotBlank
    @Column(nullable = false)
    private boolean recommended;
    
    @OneToOne(mappedBy = "gameReview")
    private PurchasedGame purchasedGame;
}
