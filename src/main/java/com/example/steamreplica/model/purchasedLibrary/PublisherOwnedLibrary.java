package com.example.steamreplica.model.purchasedLibrary;

import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.userApplication.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PublisherOwnedLibrary {
    @Id
    private long id;

    public PublisherOwnedLibrary(User user) {
        this.user = user;
        this.id = user.getId();
    }
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
    
    @ManyToMany(mappedBy = "publisherOwners", fetch = FetchType.EAGER)
    private Set<Game> games = new HashSet<>();
}
