package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>
{
}
