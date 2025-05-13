package edu.sjsu.sase.android.spoleralert.notifications;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import edu.sjsu.sase.android.spoleralert.databinding.FragmentNotificationBinding;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications;

    public NotificationAdapter(List<Notification> items) {
        notifications = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Adapter Debug", "onCreateViewHolder() called");

        return new ViewHolder(FragmentNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String current = notifications.get(position).toString();
        Log.d("Adapter Debug", "Binding item at position: " + position + " -> " + current.toString());

        holder.binding.content.setText(current);
    }

    @Override
    public int getItemCount() {
        Log.d("Adapter Debug", "Total item count: " + notifications.size());
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected final FragmentNotificationBinding binding;
        public ViewHolder(FragmentNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // The root represent one row
            /*
            this.binding.getRoot().setOnClickListener(view ->
                    Toast.makeText(view.getContext(), "A row clicked",
                            Toast.LENGTH_SHORT).show());

             */
        }
    }
}