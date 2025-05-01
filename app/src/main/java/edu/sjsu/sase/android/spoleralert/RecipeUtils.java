package edu.sjsu.sase.android.spoleralert;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeUtils {

    public interface RecipeCallback {
        void onRecipesLoaded(List<Recipe> recipes);
        void onError(Exception e);
    }

    public static void fetchRecipes(String searchTerm, RecipeCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://www.themealdb.com/api/json/v1/1/search.php?s=" + searchTerm);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                reader.close();
                JSONObject json = new JSONObject(jsonBuilder.toString());
                JSONArray mealsArray = json.optJSONArray("meals");

                List<Recipe> recipes = new ArrayList<>();
                if (mealsArray != null) {
                    for (int i = 0; i < mealsArray.length(); i++) {
                        JSONObject meal = mealsArray.getJSONObject(i);
                        String name = meal.optString("strMeal", "Unnamed");
                        String imageUrl = meal.optString("strMealThumb", "");
                        String category = meal.optString("strCategory", "");
                        String area = meal.optString("strArea", "");
                        String instructions = meal.optString("strInstructions", "");

                        List<String> ingredients = new ArrayList<>();
                        for (int j = 1; j <= 20; j++) {
                            String ing = meal.optString("strIngredient" + j);
                            if (ing != null && !ing.trim().isEmpty()) {
                                ingredients.add(ing.trim());
                            }
                        }

                        recipes.add(new Recipe(name, imageUrl, category, area, instructions, ingredients));
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> callback.onRecipesLoaded(recipes));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }
        }).start();
    }
}