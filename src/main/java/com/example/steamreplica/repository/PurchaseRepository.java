package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>
{
    Collection<Purchase> findAllByBoughtLibrary_Id(long id);
}
