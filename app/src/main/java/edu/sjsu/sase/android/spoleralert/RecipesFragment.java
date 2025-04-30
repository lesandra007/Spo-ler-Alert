package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RecipesFragment extends Fragment {

    public RecipesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View recipes_view = inflater.inflate(R.layout.fragment_recipes, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        recipes_view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.groceriesFragment));
        recipes_view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.recipesFragment));
        recipes_view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.statisticsFragment));

        GroceryDatabase db = new GroceryDatabase(requireContext());
        List<Grocery> groceries = db.getGroceriesAlphabetical();

        List<String> groceryNames = groceries.stream()
                .map(g -> g.getName().toLowerCase())
                .collect(Collectors.toList());

        List<String> expiringSoon = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Grocery g : groceries) {
            if (!g.getExpirationDate().isBefore(today) && g.getExpirationDate().isBefore(today.plusDays(3))) {
                expiringSoon.add(g.getName().toLowerCase());
            }
        }

        // Fetch recipes from TheMealDB
        RecipeUtils.fetchRecipes("a", new RecipeUtils.RecipeCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> allRecipes) {
                List<Recipe> possibleRecipes = new ArrayList<>();
                Map<Recipe, Integer> recommendedScore = new HashMap<>();

                for (Recipe r : allRecipes) {
                    boolean canMake = true;
                    int expiringCount = 0;

                    for (String ing : r.getIngredients()) {
                        String ingLower = ing.toLowerCase();
                        if (!groceryNames.contains(ingLower)) {
                            canMake = false;
                        }
                        if (expiringSoon.contains(ingLower)) {
                            expiringCount++;
                        }
                    }

                    if (canMake) {
                        possibleRecipes.add(r);
                    }

                    if (expiringCount > 0) {
                        recommendedScore.put(r, expiringCount);
                    }
                }

                // Top 3 recommended recipes by expiring ingredient count
                List<Recipe> top3Recommended = recommendedScore.entrySet().stream()
                        .sorted((a, b) -> b.getValue() - a.getValue())
                        .limit(3)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                // Final combined list: Top 3 recommended + other possible
                List<Recipe> finalRecipes = new ArrayList<>(top3Recommended);
                for (Recipe r : possibleRecipes) {
                    if (!finalRecipes.contains(r)) {
                        finalRecipes.add(r);
                    }
                }

                // Sort alphabetically
                finalRecipes.sort(Comparator.comparing(Recipe::getTitle));

                RecyclerView recipesRecyclerView = recipes_view.findViewById(R.id.recipes_recycler_view);
                recipesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recipesRecyclerView.setAdapter(new RecipesAdapter(finalRecipes));
            }

            @Override
            public void onError(Exception e) {
                Log.e("RecipeFetchError", "Failed to fetch recipes", e);
            }
        });

        return recipes_view;
    }
}
