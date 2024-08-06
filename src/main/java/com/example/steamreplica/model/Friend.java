package com.example.steamreplica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue
    private long id;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime friendSince;
    
    @ManyToOne
    @JoinColumn(name = "friend_Id", referencedColumnName = "id")
    private User friend;

    @ManyToOne
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
}
