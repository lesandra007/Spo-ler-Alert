package edu.sjsu.sase.android.spoleralert;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.sjsu.sase.android.spoleralert.recipe.Recipe;
import edu.sjsu.sase.android.spoleralert.recipe.RecipeUtils;
import edu.sjsu.sase.android.spoleralert.recipe.RecipesAdapter;

public class RecipesFragment extends Fragment implements RecipesAdapter.OnRecipeClickListener {

    private List<Recipe> allRecipes;
    private List<Recipe> filteredRecipes;
    private List<Recipe> recommendedRecipes;
    private Set<String> expiringIngredients;
    private RecipesAdapter allRecipesAdapter;
    private RecipesAdapter recommendedAdapter;
    private RecyclerView allRecipesGrid;
    private RecyclerView recommendedRecycler;
    private Spinner sortSpinner;
    private EditText searchBar;

    public RecipesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        ImageView profilePic = view.findViewById(R.id.profilePicture);
        TextView birdSpeech = view.findViewById(R.id.birdSpeech);
        profilePic.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("AvatarPrefs", Context.MODE_PRIVATE);
            List<String> phrases = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                String phrase = prefs.getString("phrase_" + i, null);
                if (phrase != null) phrases.add(phrase);
            }

            if (!phrases.isEmpty()) {
                String selectedPhrase = phrases.get((int) (Math.random() * phrases.size()));
                birdSpeech.setText(selectedPhrase);
                birdSpeech.setVisibility(View.VISIBLE);

                // ✨ Fade in animation
                birdSpeech.setAlpha(0f);
                birdSpeech.animate().alpha(1f).setDuration(300).start();

                // ⏳ Auto-hide after 4 seconds
                birdSpeech.postDelayed(() -> birdSpeech.setVisibility(View.GONE), 4000);
            }
        });

        SharedPreferences prefs = requireContext().getSharedPreferences("AvatarPrefs", getContext().MODE_PRIVATE);
        int savedAvatar = prefs.getInt("avatarImage", R.drawable.bird1_green);
        String selectedName = prefs.getString("avatarName", "Chirplin");

        profilePic.setImageResource(savedAvatar);

        NavController controller = NavHostFragment.findNavController(this);

        view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.groceriesFragment));
        view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.recipesFragment));
        view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.statisticsFragment));

        allRecipesGrid = view.findViewById(R.id.all_recipes_grid);
        allRecipesGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        recommendedRecycler = view.findViewById(R.id.recommended_recycler);
        recommendedRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        sortSpinner = view.findViewById(R.id.sort_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"Alphabetical", "Cuisine"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        searchBar = view.findViewById(R.id.search_bar);

        GroceryDatabase db = new GroceryDatabase(requireContext());
        List<Grocery> groceries = db.getGroceriesAlphabetical();
        expiringIngredients = new HashSet<>();
        LocalDate today = LocalDate.now();
        for (Grocery g : groceries) {
            if (!g.getExpirationDate().isBefore(today) && g.getExpirationDate().isBefore(today.plusDays(3))) {
                expiringIngredients.add(g.getName().toLowerCase());
            }
        }

        RecipeUtils.fetchRecipes("a", new RecipeUtils.RecipeCallback() {
            @Override
            public void onRecipesLoaded(List<Recipe> recipes) {
                Log.d("RECIPE_FETCH", "Fetched: " + recipes.size());
                allRecipes = recipes;

                List<Recipe> sorted = new ArrayList<>(allRecipes);
                sorted.sort((a, b) -> {
                    long countB = b.getIngredients().stream().filter(i -> expiringIngredients.contains(i.toLowerCase())).count();
                    long countA = a.getIngredients().stream().filter(i -> expiringIngredients.contains(i.toLowerCase())).count();
                    return Long.compare(countB, countA);
                });
                recommendedRecipes = sorted.subList(0, Math.min(3, sorted.size()));

                recommendedAdapter = new RecipesAdapter(recommendedRecipes, RecipesFragment.this, expiringIngredients);
                recommendedRecycler.setAdapter(recommendedAdapter);

                sortAndDisplay("Alphabetical");
            }

            @Override
            public void onError(Exception e) {
                Log.e("RECIPE_FETCH", "Error fetching recipes", e);
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortOption = parent.getItemAtPosition(position).toString();
                sortAndDisplay(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void sortAndDisplay(String method) {
        if (allRecipes == null) return;

        filteredRecipes = new ArrayList<>(allRecipes);

        if (method.equals("Alphabetical")) {
            Collections.sort(filteredRecipes, Comparator.comparing(Recipe::getTitle));
        } else if (method.equals("Cuisine")) {
            Collections.sort(filteredRecipes, Comparator.comparing(Recipe::getArea));
        }

        filterRecipes(searchBar.getText().toString());
    }

    private void filterRecipes(String query) {
        if (filteredRecipes == null) return;

        List<Recipe> results = filteredRecipes.stream()
                .filter(r -> r.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        allRecipesAdapter = new RecipesAdapter(results, this);
        allRecipesGrid.setAdapter(allRecipesAdapter);
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
