package com.example.steamreplica.model.game.DLC;

import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Entity
@Table(name = "dlc_review")
@AllArgsConstructor
@NoArgsConstructor
public class DLCReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
