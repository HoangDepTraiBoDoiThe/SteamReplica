package com.example.steamreplica.dtos.request;

import com.example.steamreplica.model.purchasedLibrary.Purchases;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionRequest {
    @NotBlank(message = "Transaction time is required")
    private ZonedDateTime transactionDate;
    
    @NotBlank(message = "Transaction type is required")
    private String transactionType;
    
    @NotBlank(message = "Bought library id is required")
    private long boughtLibraryId;

    public Purchases toPurchaseTransaction() {
        return null;
    }

    // Todo: Add games n DLCs data.
}
