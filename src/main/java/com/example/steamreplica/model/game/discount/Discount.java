package com.example.steamreplica.model.game.discount;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String discountName;
    @Column(unique = true, nullable = false)
    private String discountCode;
    private String discountDescription;
    @Column(nullable = false)
    private BigDecimal discountPercent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "discounts")
    private Set<Game> discountedGames = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "discounts")
    private Set<DLC> discountedDlc = new HashSet<>();

    public Discount(String discountName, String discountCode, String discountDescription, BigDecimal discountPercent) {
        this.discountName = discountName;
        this.discountCode = discountCode;
        this.discountDescription = discountDescription;
        this.discountPercent = discountPercent;
    }
}
