package edu.sjsu.sase.android.spoleralert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.sjsu.sase.android.spoleralert.profile.ProfilePickerActivity;

public class StatisticsFragment extends Fragment {

    private ArrayList<BarEntry> barArraylist;
    private ArrayList<String> monthLabels;
    private GroceryDatabase groceryDb;
    ImageView profilePic;
    private ActivityResultLauncher<Intent> profilePickerLauncher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the result callback
        profilePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int selectedImageRes = data.getIntExtra("selectedImageRes", R.drawable.pfp1);
                            profilePic.setImageResource(selectedImageRes);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View statistics_view = inflater.inflate(R.layout.fragment_statistics, container, false);

        profilePic = statistics_view.findViewById(R.id.profilePicture);

        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfilePickerActivity.class);
            profilePickerLauncher.launch(intent);
        });

        groceryDb = new GroceryDatabase(requireContext());
        BarChart barChart = statistics_view.findViewById(R.id.barchart);
        getData();
        BarDataSet barDataSet = new BarDataSet(barArraylist, "Spo!ler Alert Statistics");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barDataSet.setLabel("Your Statistics");
        barChart.getDescription().setEnabled(true);

//      Editing the legend to be on the bottom left
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(14f);
        l.setTextSize(16f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);

        Spinner gs_dropdown = statistics_view.findViewById(R.id.bar_graph_dropdown);
        ArrayAdapter<CharSequence> gs_adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.bar_graph_sorting,
                android.R.layout.simple_spinner_item
        );
        gs_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gs_dropdown.setAdapter(gs_adapter);

        gs_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sorting_type = parent.getItemAtPosition(position).toString();

                float maxMoneyY = getMaxValue(List.of(
                        getMoneySpentData(),
                        getMoneyWasteData(),
                        getMoneyUsedData()
                )) * 1.1f;

                float maxFoodY = getMaxValue(List.of(
                        getFoodBoughtData(),
                        getFoodWasteData(),
                        getFoodUsedData()
                )) * 1.1f;

                // Update bar chart based on selection
                switch (sorting_type) {
                    case "Money Spent":
                        updateChart(getMoneySpentData(), barChart, "Money Spent in Dollars", R.color.fresh_green, maxMoneyY);
                        break;
                    case "Money Waste":
                        updateChart(getMoneyWasteData(), barChart, "Money Waste in Dollars", R.color.expiring_red, maxMoneyY);
                        break;
                    case "Total Money Used":
                        updateChart(getMoneyUsedData(), barChart, "Total Money Used in Dollars", R.color.fresh_green, maxMoneyY);
                        break;
                    case "Food Bought":
                        updateChart(getFoodBoughtData(), barChart, "Food Bought in lbs", R.color.fresh_green, maxFoodY);
                        break;
                    case "Food Wasted":
                        updateChart(getFoodWasteData(), barChart, "Food Wasted in lbs", R.color.expiring_red, maxFoodY);
                        break;
                    case "Total Food Used":
                        updateChart(getFoodUsedData(), barChart, "Total Food Used in lbs", R.color.fresh_green, maxFoodY);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        NavController controller = NavHostFragment.findNavController(this);
        //clicking on the buttons in the bottom bar to go to the different main parts of the app
        statistics_view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.groceriesFragment);
            }
        });

        statistics_view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.recipesFragment);
            }
        });

        statistics_view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.navigate(R.id.statisticsFragment);
            }
        });

        return statistics_view;

    }

    private void getData() {
        // Simulated full dataset
        ArrayList<MonthlyStat> allStats = new ArrayList<>();
        allStats.add(new MonthlyStat(YearMonth.of(2023, 12), 30));
        allStats.add(new MonthlyStat(YearMonth.of(2024, 1), 40));
        allStats.add(new MonthlyStat(YearMonth.of(2024, 2), 50));
        allStats.add(new MonthlyStat(YearMonth.of(2024, 3), 70));
        allStats.add(new MonthlyStat(YearMonth.of(2024, 4), 80));
        allStats.add(new MonthlyStat(YearMonth.of(2024, 5), 60)); // Extra month

        // Sort descending by month
        allStats.sort((a, b) -> b.month.compareTo(a.month));

        // Take only the 5 most recent
        List<MonthlyStat> recentFive = allStats.subList(0, Math.min(6, allStats.size()));

        // Reverse to display oldest to newest
        Collections.reverse(recentFive);

        // Prepare data for chart
        barArraylist = new ArrayList<>();
        monthLabels = new ArrayList<>();

        for (int i = 0; i < recentFive.size(); i++) {
            MonthlyStat stat = recentFive.get(i);
            barArraylist.add(new BarEntry(i, stat.value));
            monthLabels.add(stat.month.getMonth().name().substring(0, 3)); // e.g. "JAN"
        }
    }

    private float getMaxValue(List<List<MonthlyStat>> groups) {
        float max = Float.MIN_VALUE;
        for (List<MonthlyStat> group : groups) {
            for (MonthlyStat stat : group) {
                if (stat.value > max) {
                    max = stat.value;
                }
            }
        }
        return max;
    }

    private List<MonthlyStat> getMoneySpentData() {
        return groceryDb.getMonthlyMoneySpentStats();
    }

    private List<MonthlyStat> getMoneyWasteData() {
        return groceryDb.getMonthlyMoneySpentStats();
    }

    private List<MonthlyStat> getMoneyUsedData() {
        return groceryDb.getMonthlyMoneySpentStats();
    }

    private List<MonthlyStat> getFoodBoughtData() {
        return groceryDb.getMonthlyMoneySpentStats();
    }

    private List<MonthlyStat> getFoodWasteData() {
        return groceryDb.getMonthlyMoneySpentStats();
    }

    private List<MonthlyStat> getFoodUsedData() {
        return groceryDb.getMonthlyMoneySpentStats();
    }

    private void updateChart(List<MonthlyStat> data, BarChart chart, String label, int colorResId, float maxY) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, data.get(i).value));
            labels.add(data.get(i).month.getMonth().name().substring(0, 3));
        }

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(ContextCompat.getColor(requireContext(), colorResId));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(maxY); // lock the upper bound

        chart.getAxisRight().setEnabled(false); // optional: hide right axis
        chart.invalidate(); // refresh
    }
}