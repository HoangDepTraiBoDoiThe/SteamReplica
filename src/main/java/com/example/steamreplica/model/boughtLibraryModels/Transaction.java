package com.example.steamreplica.model.boughtLibraryModels;

import com.example.steamreplica.constants.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private long id;
    
    private ZonedDateTime TransactionDate;
    private TransactionType transactionType;
    
    @ManyToOne
    @JoinColumn(name = "boughtLibrary", referencedColumnName = "id")
    private BoughtLibrary boughtLibrary;
    
    @OneToMany(mappedBy = "transaction")
    private Set<TransactionGame> transactionGames = new HashSet<>();
}
