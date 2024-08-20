package com.example.steamreplica.dtos.response;

import com.example.steamreplica.model.game.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse_Full extends CategoryResponse_Minimal {
    private String categoryDescription;
    private List<?> games;
    public CategoryResponse_Full(Category category, List<?> games) {
        super(category);
        this.categoryDescription = category.getCategoryDescription();
        this.games = games;
    }
}
