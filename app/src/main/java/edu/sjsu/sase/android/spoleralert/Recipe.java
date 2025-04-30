package edu.sjsu.sase.android.spoleralert;

import java.util.List;

/*
 * class for recipes and the required ingredients
 */
public class Recipe {
    private String name;
    private List<String> ingredients;

    public Recipe(String title, List<String> ingredients) {
        this.name = title;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
