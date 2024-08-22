package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.BaseCacheableModel;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "dlc_review")
@AllArgsConstructor
@NoArgsConstructor
public class DLCReview extends BaseCacheableModel {
    private String review;
    
    @NotBlank
    @Column(nullable = false)
    private boolean recommended;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne
    @PrimaryKeyJoinColumn(name = "purchased_dlc", referencedColumnName = "id")
    private PurchasedDLC purchasedDLC;
}
