package com.example.steamreplica.repository;

import com.example.steamreplica.model.game.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = {"games"})
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findById_full(long id);
}
