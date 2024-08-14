package com.example.steamreplica.model.game;

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
public class Category {
    @Id
    @GeneratedValue
    private long id;
    
    @Column(nullable = false)
    private String categoryName;
    private String categoryDescription;
    
    @ManyToMany(mappedBy = "categories")
    private Set<Game> games = new HashSet<>();

    public Category(String categoryName, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }
}
