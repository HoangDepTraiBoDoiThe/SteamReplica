package com.example.steamreplica.repository;

import com.example.steamreplica.model.purchasedLibrary.PurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long>
{
}
