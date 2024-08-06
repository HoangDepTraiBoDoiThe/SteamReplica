package com.example.steamreplica.model.BoughtLibraryModels;

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
    private BigDecimal gameBasePriceAtTheTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_Id", referencedColumnName = "id")
    private Transaction transaction;
}
