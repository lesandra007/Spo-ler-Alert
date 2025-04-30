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

import java.util.List;

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

        RecipeUtils.fetchRecipes("a", new RecipeUtils.RecipeCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> allRecipes) {
                Log.d("MEAL_API", "Recipes loaded: " + allRecipes.size());

                RecyclerView recipesRecyclerView = recipes_view.findViewById(R.id.all_recipes_grid);
                recipesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recipesRecyclerView.setAdapter(new RecipesAdapter(allRecipes));
            }

            @Override
            public void onError(Exception e) {
                Log.e("MEAL_API", "API call failed", e);
            }
        });

        return recipes_view;
    }
}
