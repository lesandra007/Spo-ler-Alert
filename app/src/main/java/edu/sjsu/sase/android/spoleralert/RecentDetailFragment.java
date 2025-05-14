package edu.sjsu.sase.android.spoleralert;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.sjsu.sase.android.spoleralert.recentadapters.RecentPurchaseAdapter;
import edu.sjsu.sase.android.spoleralert.recentadapters.RecentUpdateAdapter;

public class RecentDetailFragment extends Fragment {

    private GroceryDatabase db;
    private RecyclerView recentList;
    private TextView listTitle;
    private Spinner filterSpinner;

    public RecentDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_detail, container, false);

        db = new GroceryDatabase(requireContext());

        recentList = view.findViewById(R.id.recent_full_list);
        recentList.setLayoutManager(new LinearLayoutManager(getContext()));

        listTitle = view.findViewById(R.id.recent_list_title);
        filterSpinner = view.findViewById(R.id.recent_list_filter_spinner);

        setupSpinner();

        Button backBtn = view.findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        Log.d("DEBUG", "Purchases: " + db.getRecentPurchases(100).size());
        Log.d("DEBUG", "Eaten: " + db.getRecentUpdates(true, 100).size());
        Log.d("DEBUG", "Wasted: " + db.getRecentUpdates(false, 100).size());

        return view;
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.recent_list_filters, // This is a string-array you'll define in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Initial load
        updateList(0); // default to Recent Purchases

        filterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateList(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void updateList(int position) {
        switch (position) {
            case 0: // Recent Purchases
                listTitle.setText("All Recent Purchases");
                List<Grocery> purchases = db.getRecentPurchases(100);
                recentList.setAdapter(new RecentPurchaseAdapter(purchases));
                break;

            case 1: // Recently Eaten
                listTitle.setText("Recently Eaten");
                List<GroceryDatabase.GroceryUsageEntry> eats = db.getRecentUpdates(true, 100);
                recentList.setAdapter(new RecentUpdateAdapter(eats));
                break;

            case 2: // Recently Wasted
                listTitle.setText("Recently Wasted");
                List<GroceryDatabase.GroceryUsageEntry> wastes = db.getRecentUpdates(false, 100);
                recentList.setAdapter(new RecentUpdateAdapter(wastes));
                break;
        }
    }
}