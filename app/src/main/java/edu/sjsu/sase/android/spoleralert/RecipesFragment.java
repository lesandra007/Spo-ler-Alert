package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipesFragment extends Fragment implements RecipesAdapter.OnRecipeClickListener {

    public RecipesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View recipes_view = inflater.inflate(R.layout.fragment_recipes, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        recipes_view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.groceriesFragment));
        recipes_view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.recipesFragment));
        recipes_view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.statisticsFragment));

        GroceryDatabase db = new GroceryDatabase(requireContext());
        List<Grocery> groceries = db.getGroceriesAlphabetical();

        Set<String> expiringSoon = new HashSet<>();
        LocalDate today = LocalDate.now();
        for (Grocery g : groceries) {
            if (!g.getExpirationDate().isBefore(today) && g.getExpirationDate().isBefore(today.plusDays(3))) {
                expiringSoon.add(g.getName().toLowerCase());
            }
        }

        RecipeUtils.fetchRecipes("a", new RecipeUtils.RecipeCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> allRecipes) {
                Log.d("MEAL_API", "Recipes loaded: " + allRecipes.size());

                // Sort recipes by number of matching expiring ingredients
                Map<Recipe, Integer> scoreMap = new HashMap<>();
                for (Recipe r : allRecipes) {
                    int count = 0;
                    for (String ing : r.getIngredients()) {
                        if (expiringSoon.contains(ing.toLowerCase())) {
                            count++;
                        }
                    }
                    scoreMap.put(r, count);
                }

                List<Recipe> sorted = new ArrayList<>(allRecipes);
                sorted.sort((a, b) -> scoreMap.get(b) - scoreMap.get(a));

                List<Recipe> recommended = sorted.subList(0, Math.min(3, sorted.size()));
                List<Recipe> remaining = new ArrayList<>(allRecipes);
                remaining.removeAll(recommended);

                RecyclerView recommendedRecycler = recipes_view.findViewById(R.id.recommended_recycler);
                recommendedRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                recommendedRecycler.setAdapter(new RecipesAdapter(recommended, RecipesFragment.this) {
                    @Override
                    public void onBindViewHolder(ViewHolder holder, int position) {
                        super.onBindViewHolder(holder, position);

                        Recipe recipe = recommended.get(position);
                        List<String> matches = recipe.getIngredients().stream()
                                .filter(ing -> expiringSoon.contains(ing.toLowerCase()))
                                .collect(Collectors.toList());

                        if (!matches.isEmpty()) {
                            holder.titleTextView.append("\nUses: " + String.join(", ", matches));
                        }
                    }
                });

                RecyclerView recipesGrid = recipes_view.findViewById(R.id.all_recipes_grid);
                recipesGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                recipesGrid.setAdapter(new RecipesAdapter(remaining, RecipesFragment.this));
            }

            @Override
            public void onError(Exception e) {
                Log.e("MEAL_API", "API call failed", e);
            }
        });

        return recipes_view;
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        StringBuilder builder = new StringBuilder();
        builder.append("Category: ").append(recipe.getCategory()).append("\n");
        builder.append("Area: ").append(recipe.getArea()).append("\n\n");
        builder.append("Instructions:\n").append(recipe.getInstructions()).append("\n\n");
        builder.append("Ingredients:\n");
        for (String ing : recipe.getIngredients()) {
            builder.append("- ").append(ing).append("\n");
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(recipe.getTitle())
                .setMessage(builder.toString())
                .setPositiveButton("Close", null)
                .show();
    }
}
