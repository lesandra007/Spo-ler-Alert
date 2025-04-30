package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipesFragment newInstance(String param1, String param2) {
        RecipesFragment fragment = new RecipesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View recipes_view = inflater.inflate(R.layout.fragment_recipes, container, false);
        NavController controller = NavHostFragment.findNavController(this);

        // bottom bar navigation
        recipes_view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.groceriesFragment));
        recipes_view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.recipesFragment));
        recipes_view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(v -> controller.navigate(R.id.statisticsFragment));

        // Load grocery names from database
        GroceryDatabase db = new GroceryDatabase(requireContext());
        List<Grocery> groceries = db.getGroceriesAlphabetical();
        List<String> groceryNames = new ArrayList<>();
        for (Grocery g : groceries) {
            groceryNames.add(g.getName().toLowerCase());
            Log.d(";0;", "sleepy" + g.getName());
        }

        // Filter recipes based on available groceries
        List<Recipe> possibleRecipes = new ArrayList<>();
        for (Recipe r : RecipeUtils.getAllRecipes()) {
            boolean canMake = true;
            for (String ingredient : r.getIngredients()) {
                if (!groceryNames.contains(ingredient.toLowerCase())) {
                    canMake = false;
                    Log.d("we're in", groceryNames + " doesn't contain " + ingredient);
//                  YOU SHALL NOT PASS!!!... if you don't have all the ingredients
                    break;
                }
                else {
                    Log.d("we're in", "contains " + ingredient);
                }
            }
            if (canMake) possibleRecipes.add(r);
        }

        Log.d("final cans", "Good Recipes: " + possibleRecipes);

        RecyclerView recipesRecyclerView = recipes_view.findViewById(R.id.recipes_recycler_view);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipesRecyclerView.setAdapter(new RecipesAdapter(possibleRecipes));


        return recipes_view;
    }

}