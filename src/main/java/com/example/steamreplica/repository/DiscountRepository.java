package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.discount.Discount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d FROM Discount d WHERE d.discountName = :query OR d.discountCode = :query")
    Optional<Discount> findDiscountByDiscountNameOrDiscountCode(@Param("query") String query);

    @EntityGraph(attributePaths = {"discountedGames", "discountedDlc", "purchases"})
    Optional<Discount> findById_full(long id);
    Optional<Discount> findDiscountByDiscountCode(String code);
}
