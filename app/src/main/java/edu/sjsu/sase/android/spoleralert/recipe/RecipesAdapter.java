package edu.sjsu.sase.android.spoleralert.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.sjsu.sase.android.spoleralert.R;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private OnRecipeClickListener listener;
    private Set<String> expiringIngredients;

    public RecipesAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this(recipes, listener, null);
    }

    public RecipesAdapter(List<Recipe> recipes, OnRecipeClickListener listener, Set<String> expiringIngredients) {
        this.recipes = recipes;
        this.listener = listener;
        this.expiringIngredients = expiringIngredients;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView imageView;
        public TextView includedLabel;
        public LinearLayout expiringListContainer;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.recipe_title);
            imageView = view.findViewById(R.id.recipe_image);
            includedLabel = view.findViewById(R.id.included_label);
            expiringListContainer = view.findViewById(R.id.expiring_ingredient_list);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item_appearance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getTitle());

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageUrl())
                .centerCrop()
                .into(holder.imageView);

        holder.expiringListContainer.removeAllViews();
        holder.includedLabel.setVisibility(View.GONE);

        if (expiringIngredients != null) {
            List<String> matched = recipe.getIngredients().stream()
                    .filter(ing -> expiringIngredients.contains(ing.toLowerCase()))
                    .collect(Collectors.toList());
            if (!matched.isEmpty()) {
                holder.includedLabel.setVisibility(View.VISIBLE);
                for (String ingredient : matched) {
                    TextView tv = new TextView(holder.itemView.getContext());
                    tv.setText("- " + ingredient);
                    tv.setTextSize(14);
                    tv.setTextColor(0xFF444444);
                    holder.expiringListContainer.addView(tv);
                }
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
