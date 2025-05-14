package edu.sjsu.sase.android.spoleralert.recentadapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.sjsu.sase.android.spoleralert.GroceryDatabase;
import edu.sjsu.sase.android.spoleralert.R;

public class RecentUpdateAdapter extends RecyclerView.Adapter<RecentUpdateAdapter.ViewHolder> {
    private final List<GroceryDatabase.GroceryUsageEntry> updates;

    public RecentUpdateAdapter(List<GroceryDatabase.GroceryUsageEntry> updates) {
        this.updates = updates;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_item_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroceryDatabase.GroceryUsageEntry entry = updates.get(position);
        holder.nameView.setText(entry.name);

        double ounces = entry.update.getWeight();
        int pounds = (int) (ounces / 16);
        int remainderOz = (int) (ounces % 16);

        String weightString;
        if (pounds > 0 && remainderOz > 0)
            weightString = pounds + " lb " + remainderOz + " oz";
        else if (pounds > 0)
            weightString = pounds + " lb";
        else
            weightString = remainderOz + " oz";

        String label = entry.update.getType() ? "Eaten" : "Wasted";
        holder.nameView.setText(entry.name);
        holder.subInfoView.setText(label + " " + weightString + " on " + entry.update.getUpdateDate());
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }
}