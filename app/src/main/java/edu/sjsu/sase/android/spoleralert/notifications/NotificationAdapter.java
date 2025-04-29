package edu.sjsu.sase.android.spoleralert.notifications;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import edu.sjsu.sase.android.spoleralert.databinding.FragmentNotificationBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> mValues;

    public NotificationAdapter(List<Notification> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String current = mValues.get(position).toString();
        holder.binding.content.setText(current);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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