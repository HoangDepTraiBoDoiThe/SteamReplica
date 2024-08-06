package com.example.steamreplica.model.boughtLibrary;

import com.example.steamreplica.model.userApplication.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BoughtLibrary {
    @Id
    @GeneratedValue
    private long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
    
    @OneToMany(mappedBy = "boughtLibrary")
    private Set<Transaction> transactions = new HashSet<>();
}
