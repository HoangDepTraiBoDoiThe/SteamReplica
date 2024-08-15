package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.DLC.DLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DlcRepository extends JpaRepository<DLC, Long> {
    Optional<DLC> findDLCByDlcName(String dlcName);
    List<DLC> getAllByGame_Id(long id);
}
