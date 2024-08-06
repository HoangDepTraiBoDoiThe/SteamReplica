package com.example.steamreplica.model.gameModels;

import com.example.steamreplica.model.boughtLibraryModels.TransactionGame;
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
    private Set<TransactionGame> transactionGames = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GameImage> gameImages;

    @ManyToMany()
    @JoinTable(name = "game_category", joinColumns = @JoinColumn(name = "game_Id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<Category> categories = new HashSet<>();
}