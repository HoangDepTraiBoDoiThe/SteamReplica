package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.game.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse_Minimal extends BaseResponse {
    private String categoryName;
    public CategoryResponse_Minimal(Category category ) {
        super(category.getId());
        this.categoryName = category.getCategoryName();
    }
}
