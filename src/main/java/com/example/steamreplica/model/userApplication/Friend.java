package com.example.steamreplica.model.userApplication;

import com.example.steamreplica.model.BaseCacheableModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Friend extends BaseCacheableModel {
    private ZonedDateTime friendSince;
    
    @ManyToOne
    @JoinColumn(name = "friend_Id", referencedColumnName = "id")
    private User friend;

    @ManyToOne
    @JoinColumn(name = "user_Id", referencedColumnName = "id")
    private User user;
}
