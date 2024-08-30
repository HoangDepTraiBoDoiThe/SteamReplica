package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.model.game.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DlcImageRepository extends JpaRepository<DLCImage, Long> {
    @EntityGraph(attributePaths = {"dlc"})
    @Query("SELECT d FROM DLCImage d WHERE d.id = :id")
    Optional<DLCImage> findById_full(long id);
}
