package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.game.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DLC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "DLC name is required")
    @Column(nullable = false)
    private String dlcName;

    private String dlcDescription;

    @PositiveOrZero(message = "DLC base price must be positive or zero (Free)")
    @Column(nullable = false)
    private BigDecimal dlcBasePrice;

    @ManyToOne
    @JoinColumn
    private Game game;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "dlc")
    private Set<DLCImage> dlcImages;
}
