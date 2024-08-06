package com.example.steamreplica.model.game.transaction;

import com.example.steamreplica.model.boughtLibrary.Transaction;
import com.example.steamreplica.model.game.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TransactionGame {
    @Id
    @GeneratedValue
    private long id;
    
    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal gameBasePriceAtTheTime;
    
    @ManyToOne
    @JoinColumn(name = "transaction_Id", referencedColumnName = "id")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "game_Id", referencedColumnName = "id")
    private Game game;
}
