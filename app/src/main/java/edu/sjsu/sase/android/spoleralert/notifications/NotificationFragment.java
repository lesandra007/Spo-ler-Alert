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

    ArrayList<Notification> notifications = new ArrayList<>();
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

        adapter = new NotificationAdapter(notifications);
        recyclerView = (RecyclerView) view;
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void addNotification(int num, String notifTime) {
        notifications.add(new Notification(num, notifTime));
        adapter.notifyItemInserted(notifications.size()-1);
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
}