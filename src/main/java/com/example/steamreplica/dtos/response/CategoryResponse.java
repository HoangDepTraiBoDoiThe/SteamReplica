package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.game.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private long id;
    private String categoryName;
    private String categoryDescription;
    public CategoryResponse(Category category ) {
        this.id = category.getId();
        this.categoryName = category.getCategoryName();
        this.categoryDescription = category.getCategoryDescription();
    }
}
