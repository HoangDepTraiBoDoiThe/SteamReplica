package com.example.steamreplica.dtos.response;

import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Basic;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BoughtLibraryResponse extends BaseResponse{
    private UserResponse_Minimal user;
    private List<PurchaseResponse_Basic> purchases;

    public BoughtLibraryResponse(BoughtLibrary boughtLibrary, UserResponse_Minimal user, List<PurchaseResponse_Basic> purchases) {
        super(boughtLibrary.getId());
        this.user = user;
        this.purchases = purchases;
    }
}

