package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.DLC.DLC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DlcRepository extends JpaRepository<DLC, Long> {
    Optional<DLC> findDLCByDlcName(String dlcName);
    Page<DLC> findAllByGame_Id(long id, Pageable pageable);

    @EntityGraph(attributePaths = {"purchasedDLCs"})
    Optional<DLC> findById_withPurchasedDLCs(long id);
}
