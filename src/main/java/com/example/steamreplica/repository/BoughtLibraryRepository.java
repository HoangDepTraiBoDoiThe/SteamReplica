package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface BoughtLibraryRepository extends JpaRepository<BoughtLibrary, Long> {
    @Query("SELECT DISTINCT pg.game FROM BoughtLibrary bl" +
    " JOIN bl.purchases p" + 
    " JOIN p.purchasedGames pg" + 
    " where bl.id = :library_id")
    Collection<Game> findPurchasedGames(@Param("library_id") long library_id);

    @Query("SELECT pdlc.dlc FROM BoughtLibrary bl"
    + " JOIN bl.purchases p"
    + " JOIN p.purchasedDLCs pdlc"
    + " JOIN pdlc.dlc.game g"
    + " where bl.id = :id AND g.id = :game_id")
    Collection<DLC> findPurchasedDlcOfGame(@Param("id") long id, @Param("game_id") long game_id);
}
