package edu.sjsu.sase.android.spoleralert.recipe;

import java.util.List;

/*
 * class for recipes and the required ingredients
 */
public class Recipe {
    private String name;
    private String imageUrl;
    private String category;
    private String area;
    private String instructions;
    private List<String> ingredients;

    public Recipe(String name, String imageUrl, String category, String area, String instructions, List<String> ingredients) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}