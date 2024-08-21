package com.example.steamreplica.model.game;

import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne()
    @JoinColumn(referencedColumnName = "id")
    private PurchasedGame purchasedGame;
}
