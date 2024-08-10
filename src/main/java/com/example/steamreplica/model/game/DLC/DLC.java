package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "game_dlc")
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
    
    @OneToMany(mappedBy = "dlc", cascade = CascadeType.ALL)
    private Set<PurchasedDLC> purchasedDLCs = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "dlc")
    private Set<DLCImage> dlcImages;

    @ManyToMany
    @JoinTable(name = "discount_dlc", joinColumns = @JoinColumn(name = "dlc_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "discount_Id", referencedColumnName = "id"))
    private Set<Discount> discounts = new HashSet<>();
}
