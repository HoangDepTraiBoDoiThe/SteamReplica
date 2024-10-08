package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Blob;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "game_dlc")
@AllArgsConstructor
@NoArgsConstructor
public class DLC extends BaseCacheableModel {
    @NotBlank(message = "DLC name is required")
    @Column(nullable = false)
    private String dlcName;

    private String dlcDescription;
    
    @Column(nullable = false)
    private ZonedDateTime releaseDate;

    @PositiveOrZero(message = "DLC base price must be positive or zero (Free)")
    @Column(nullable = false)
    private BigDecimal dlcBasePrice;

    @Lob
    private Blob dlcThumbnail;

    public DLC(String dlcName, String dlcDescription, BigDecimal dlcBasePrice, Blob dlcThumbnail, ZonedDateTime releaseDate) {
        this.dlcName = dlcName;
        this.dlcDescription = dlcDescription;
        this.dlcBasePrice = dlcBasePrice;
        this.dlcThumbnail = dlcThumbnail;
        this.releaseDate = releaseDate;
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
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
    @JsonBackReference
    private Set<DLCImage> dlcImages = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "discount_dlc", joinColumns = @JoinColumn(name = "dlc_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "discount_Id", referencedColumnName = "id"))
    private Set<Discount> discounts = new HashSet<>();
}
