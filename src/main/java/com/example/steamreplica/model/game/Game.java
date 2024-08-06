package com.example.steamreplica.model.game;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.purchasedLibrary.PurchasedGame;
import com.example.steamreplica.model.game.discount.Discount;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Game name cannot be empty")
    @Column(nullable = false)
    private String gameName;

    @NotNull(message = "Game base price cannot be null")
    @PositiveOrZero(message = "Game base price must be zero(Free) or positive")
    @Column(nullable = false)
    private BigDecimal gameBasePrice;

    @Column(length = 1000)
    private String gameDescription;

    @PastOrPresent(message = "Release date must be in the past or present")
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "game")
    private Set<PurchasedGame> purchasedGames = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GameImage> gameImages;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "game_category", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "game_discount", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "discount_Id", referencedColumnName = "id"))
    private Set<Discount> discounts = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PurchasedGame> purchasedGame = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    private Set<DLC> dlc;
}