package com.example.steamreplica.model.game;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.userApplication.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Blob;
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

    @Column(nullable = false, unique = true)
    private String gameName;

    @Column(nullable = false)
    private BigDecimal gameBasePrice;

    @Column(length = 1000)
    private String gameDescription;

    @Column(nullable = false)
    private LocalDate releaseDate;
    
    @Lob
    private Blob gameThumbnail;

    public Game(String gameName, BigDecimal gameBasePrice, String gameDescription, LocalDate releaseDate, Blob gameThumbnail) {
        this.gameName = gameName;
        this.gameBasePrice = gameBasePrice;
        this.gameDescription = gameDescription;
        this.releaseDate = releaseDate;
        this.gameThumbnail = gameThumbnail;
    }

    public Game(Long id, String gameName, BigDecimal gameBasePrice, String gameDescription, LocalDate releaseDate, Set<User> developers, Set<User> publishers, Blob gameThumbnail) {
        this.id = id;
        this.gameName = gameName;
        this.gameBasePrice = gameBasePrice;
        this.gameDescription = gameDescription;
        this.gameThumbnail = gameThumbnail;
        this.releaseDate = releaseDate;
        this.developers = developers;
        this.publishers = publishers;
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GameImage> gameImages = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "game_category", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<Category> categories = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "game_discount", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "discount_Id", referencedColumnName = "id"))
    private Set<Discount> discounts = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "game_Developer", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_Id", referencedColumnName = "id"))
    private Set<User> developers = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "game_Publisher", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_Id", referencedColumnName = "id"))
    private Set<User> publishers = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "game")
    private Set<PurchasedGame> purchasedGame = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Column(name = "dlcs")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    private Set<DLC> dlcs = new HashSet<>();
}