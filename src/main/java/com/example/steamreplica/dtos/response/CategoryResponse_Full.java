package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.game.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse_Full extends CategoryResponse_Minimal {
    private String categoryDescription;
    public CategoryResponse_Full(Category category) {
        super(category);
        this.categoryDescription = category.getCategoryDescription();
    }
}
