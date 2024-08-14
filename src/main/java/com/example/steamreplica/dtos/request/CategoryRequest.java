package com.example.steamreplica.dtos.request;
import com.example.steamreplica.model.game.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    private long id;
    private String categoryName;
    private String categoryDescription;

    public Category toModel() {
        return new Category(categoryName, categoryDescription);
    }
}
