package com.example.steamreplica.dtos.request;

import com.example.steamreplica.model.purchasedLibrary.Purchase;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {
    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    List<Long> gameIds;
    List<Long> dlcIds;
    long additionalDiscountId;
}
