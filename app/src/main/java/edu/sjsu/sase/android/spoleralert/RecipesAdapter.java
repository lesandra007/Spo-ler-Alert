package edu.sjsu.sase.android.spoleralert;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private List<Recipe> recipes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView ingredientsTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.recipe_title);
            ingredientsTextView = view.findViewById(R.id.recipe_ingredients);
        }
    }

    public RecipesAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item_appearance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.ingredientsTextView.setText("Ingredients: " + String.join(", ", recipe.getIngredients()));
        Log.d("recipessssss", "recipes -> " + recipes + ". Positions -> " + position);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
