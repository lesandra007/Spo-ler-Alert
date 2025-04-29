package edu.sjsu.sase.android.spoleralert.notifications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.sjsu.sase.android.spoleralert.R;

/**
 * A fragment representing a list of Items.
 */
public class NotificationFragment extends Fragment {

    ArrayList<Notification> data = new ArrayList<>();
    NotificationAdapter adapter;
    RecyclerView recyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);

        // hardcoded data for testing
        for (int i = 1; i <= 2; i++) {
            data.add(new Notification(i, "days before"));
        }

        adapter = new NotificationAdapter(data);
        recyclerView = (RecyclerView) view;
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void addNotification(int num, String notifTime) {
        data.add(new Notification(num, notifTime));
        adapter.notifyItemInserted(data.size()-1);
    }
}