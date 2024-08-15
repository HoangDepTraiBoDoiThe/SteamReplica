package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.DLC.DLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DlcRepository extends JpaRepository<DLC, Long> {
    List<DLC> getAllByGame(long id);
}
