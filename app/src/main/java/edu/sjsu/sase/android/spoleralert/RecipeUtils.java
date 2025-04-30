package edu.sjsu.sase.android.spoleralert;

/*
 * test database
 * TODO: replace with database
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeUtils {
    public static List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Avocado Toast", Arrays.asList("Bread", "Avocado")));
        recipes.add(new Recipe("PB&J Sandwich", Arrays.asList("Peanut Butter", "Jelly", "Bread")));
        recipes.add(new Recipe("Fruit Salad", Arrays.asList("Apple", "Banana", "Orange")));
        recipes.add(new Recipe("Omelette", Arrays.asList("Egg", "Milk", "Cheese")));
        return recipes;
    }
}
