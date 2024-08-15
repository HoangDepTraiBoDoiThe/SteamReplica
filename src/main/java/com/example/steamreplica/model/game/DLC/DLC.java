package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Blob;
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

    @Lob
    private Blob dlcThumbnail;

    public DLC(String dlcName, String dlcDescription, BigDecimal dlcBasePrice, Blob dlcThumbnail) {
        this.dlcName = dlcName;
        this.dlcDescription = dlcDescription;
        this.dlcBasePrice = dlcBasePrice;
        this.dlcThumbnail = dlcThumbnail;
    }

    @ManyToOne
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "dlc", cascade = {CascadeType.MERGE})
    private Set<PurchasedDLC> purchasedDLCs = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "dlc")
    private Set<DLCImage> dlcImages = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "discount_dlc", joinColumns = @JoinColumn(name = "dlc_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "discount_Id", referencedColumnName = "id"))
    private Set<Discount> discounts = new HashSet<>();
}
