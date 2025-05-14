package edu.sjsu.sase.android.spoleralert.recentadapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.sjsu.sase.android.spoleralert.Grocery;
import edu.sjsu.sase.android.spoleralert.R;

public class RecentPurchaseAdapter extends RecyclerView.Adapter<RecentPurchaseAdapter.ViewHolder> {
    private final List<Grocery> groceries;

    public RecentPurchaseAdapter(List<Grocery> groceries) {
        this.groceries = groceries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, subInfoView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.item_name);
            subInfoView = itemView.findViewById(R.id.item_subinfo);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Grocery g = groceries.get(position);
        holder.nameView.setText(g.getName());
        holder.subInfoView.setText("Bought on " + g.getExpirationDate());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_item_card, parent, false);
        Log.d("ADAPTER", "Creating view holder");
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }

}