package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.userApplication.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "friends", "friendOf", "boughtLibrary", "devOwnedLibrary", "publisherOwnedLibrary"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById_full(long id);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    Collection<User> findUsersByRoleName(String roleName);
}
