package com.example.steamreplica.model.BoughtLibraryModels;

import com.example.steamreplica.model.userApplicationModels.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
