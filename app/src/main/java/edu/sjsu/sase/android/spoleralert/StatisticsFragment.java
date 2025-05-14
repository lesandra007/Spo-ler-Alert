package edu.sjsu.sase.android.spoleralert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sjsu.sase.android.spoleralert.profile.ProfilePickerActivity;
import com.github.mikephil.charting.formatter.ValueFormatter;


public class StatisticsFragment extends Fragment {

    private ArrayList<BarEntry> barArraylist;
    private ArrayList<String> monthLabels;
    private GroceryDatabase groceryDb;
    ImageView profilePic;
    private ActivityResultLauncher<Intent> profilePickerLauncher;
    private float maxMoneyY = -1;
    private float maxFoodY = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the result callback
        profilePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        int selectedImageRes = data.getIntExtra("selectedImageRes", R.drawable.bird1_green);
                        String selectedBirdName = data.getStringExtra("selectedBirdName");

                        SharedPreferences prefs = requireContext().getSharedPreferences("AvatarPrefs", Context.MODE_PRIVATE);
                        prefs.edit()
                                .putInt("avatarImage", selectedImageRes)
                                .putString("avatarName", selectedBirdName)
                                .apply();

                        // ⏬ Immediately update the UI after saving
                        if (getView() != null) {
                            ImageView profilePic = getView().findViewById(R.id.profilePicture);
                            TextView birdNameView = getView().findViewById(R.id.birdNameText);
                            profilePic.setImageResource(selectedImageRes);
                            birdNameView.setText(selectedBirdName);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View statistics_view = inflater.inflate(R.layout.fragment_statistics, container, false);

        TextView birdSpeech = statistics_view.findViewById(R.id.birdSpeech);
        profilePic = statistics_view.findViewById(R.id.profilePicture);
        TextView birdNameView = statistics_view.findViewById(R.id.birdNameText);

        // Load saved avatar data
        SharedPreferences prefs = requireContext().getSharedPreferences("AvatarPrefs", Context.MODE_PRIVATE);
        int savedAvatar = prefs.getInt("avatarImage", R.drawable.no_avatar);
        String savedName = prefs.getString("avatarName", "Choose your avatar");

        // Set image and name
        profilePic.setImageResource(savedAvatar);
        birdNameView.setText(savedName);

        // Profile Picture shows speech bubble
        profilePic.setOnClickListener(v -> {
            List<String> phrases = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                String phrase = prefs.getString("phrase_" + i, null);
                if (phrase != null) phrases.add(phrase);
            }

            if (!phrases.isEmpty()) {
                String selectedPhrase = phrases.get((int) (Math.random() * phrases.size()));
                birdSpeech.setText(selectedPhrase);
                birdSpeech.setAlpha(0f);
                birdSpeech.setVisibility(View.VISIBLE);
                birdSpeech.animate().alpha(1f).setDuration(300).start();
                birdSpeech.postDelayed(() -> birdSpeech.setVisibility(View.GONE), 4000);
            }
        });

        // Edit icon opens the profile picker
        ImageView editIcon = statistics_view.findViewById(R.id.editIcon);
        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfilePickerActivity.class);
            profilePickerLauncher.launch(intent);
        });

        // Set up Bar Chart and other UI elements as usual
        groceryDb = new GroceryDatabase(requireContext());
        groceryDb.logAllGroceries();
        BarChart barChart = statistics_view.findViewById(R.id.barchart);

        List<MonthlyStat> initialData = getMoneySpentData();
        if (initialData == null || initialData.isEmpty()) {
            initialData = getFallbackData();
        }

        barArraylist = new ArrayList<>();
        monthLabels = new ArrayList<>();

        for (int i = 0; i < initialData.size(); i++) {
            MonthlyStat stat = initialData.get(i);
            barArraylist.add(new BarEntry(i, stat.value));
            monthLabels.add(stat.month.getMonth().name().substring(0, 3));
        }

        BarDataSet barDataSet = new BarDataSet(barArraylist, "Spo!ler Alert Statistics");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barDataSet.setLabel("Your Statistics");
        barChart.getDescription().setEnabled(true);

        // Bar chart legend and axis setup
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

        List<MonthlyStat> moneySpent = getMoneySpentData();
        List<MonthlyStat> moneyWaste = getMoneyWasteData();
        List<MonthlyStat> moneyUsed = getMoneyUsedData();
        List<MonthlyStat> foodBought = getFoodBoughtData();
        List<MonthlyStat> foodWaste = getFoodWasteData();
        List<MonthlyStat> foodUsed = getFoodUsedData();

        maxMoneyY = getMaxValue(List.of(moneySpent, moneyWaste, moneyUsed)) * 1.1f;
        maxFoodY = getMaxValue(List.of(foodBought, foodWaste, foodUsed)) * 1.1f;

        gs_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sorting_type = parent.getItemAtPosition(position).toString();

                List<MonthlyStat> selectedData = null;
                String label = "";
                int colorRes = R.color.fresh_green;
                float defaultMaxY = 10f;

                switch (sorting_type) {
                    case "Money Spent":
                        selectedData = getMoneySpentData();
                        label = "Money Spent in Dollars";
                        colorRes = R.color.fresh_green;
                        break;
                    case "Money Waste":
                        selectedData = getMoneyWasteData();
                        label = "Money Waste in Dollars";
                        colorRes = R.color.expiring_red;
                        break;
                    case "Total Money Used":
                        selectedData = getMoneyUsedData();
                        label = "Total Money Used in Dollars";
                        colorRes = R.color.fresh_green;
                        break;
                    case "Food Bought":
                        selectedData = getFoodBoughtData();
                        label = "Food Bought in lbs";
                        colorRes = R.color.fresh_green;
                        break;
                    case "Food Wasted":
                        selectedData = getFoodWasteData();
                        label = "Food Wasted in lbs";
                        colorRes = R.color.expiring_red;
                        break;
                    case "Total Food Used":
                        selectedData = getFoodUsedData();
                        label = "Total Food Used in lbs";
                        colorRes = R.color.fresh_green;
                        break;
                    case "Money Overview":
                        List<MonthlyStat> spent = getMoneySpentData();
                        List<MonthlyStat> waste = getMoneyWasteData();
                        List<MonthlyStat> used = getMoneyUsedData();
                        updateGroupedChart(spent, waste, used, barChart);
                        return; // skip normal single-bar update
                }

                // Null/empty check
                if (selectedData == null || selectedData.isEmpty()) {
                    selectedData = getFallbackData();
                    Toast.makeText(requireContext(), "No data found. Showing default chart.", Toast.LENGTH_SHORT).show();
                }

                float maxY = sorting_type.contains("Money") ? maxMoneyY : maxFoodY;
                updateChart(selectedData, barChart, label, colorRes, maxY > 0 ? maxY : defaultMaxY);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Set up navigation buttons
        NavController controller = NavHostFragment.findNavController(this);
        statistics_view.findViewById(R.id.grocery_list_bottom_bar).setOnClickListener(view -> {
            controller.navigate(R.id.groceriesFragment);
        });
        statistics_view.findViewById(R.id.recipes_bottom_bar).setOnClickListener(view -> {
            controller.navigate(R.id.recipesFragment);
        });
        statistics_view.findViewById(R.id.statistics_bottom_bar).setOnClickListener(view -> {
            controller.navigate(R.id.statisticsFragment);
        });

        return statistics_view;
    }

    private List<MonthlyStat> getFallbackData() {
        List<MonthlyStat> fallback = new ArrayList<>();
        YearMonth current = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            fallback.add(new MonthlyStat(month, 0));
        }
        return fallback;
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
        return groceryDb.getMoneySpentData();
    }

    private List<MonthlyStat> getMoneyWasteData() {
        return groceryDb.getMoneyWasteData();
    }

    private List<MonthlyStat> getMoneyUsedData() {
        return groceryDb.getMoneyUsedData();
    }

    public List<MonthlyStat> getFoodBoughtData() {
        Map<YearMonth, Float> monthTotals = new HashMap<>();
        for (Grocery g : groceryDb.getAllGroceries()) {
            YearMonth ym = YearMonth.from(g.getExpirationDate());
            float weightLbs = (g.getPounds() * 16 + g.getOunces()) / 16f;
            monthTotals.put(ym, monthTotals.getOrDefault(ym, 0f) + weightLbs);
        }
        return groceryDb.toLast6MonthsList(monthTotals);
    }

    public List<MonthlyStat> getFoodWasteData() {
        return groceryDb.getMonthlyUpdateTotals(false, false); // false for waste, false for pounds
    }

    public List<MonthlyStat> getFoodUsedData() {
        return groceryDb.getMonthlyUpdateTotals(true, false); // true for used, false for pounds
    }

    private void updateChart(List<MonthlyStat> data, BarChart chart, String label, int colorResId, float maxY) {
        if (data == null || data.isEmpty()) {
            chart.clear();
            chart.setNoDataText("No data available.");
            chart.invalidate();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            MonthlyStat stat = data.get(i);

            // Avoid corrupt/huge values
            if (Float.isNaN(stat.value) || stat.value < 0 || stat.value > 1_000_000f) {
                Log.e("ChartSkip", "Skipping invalid stat: " + stat.month + " -> " + stat.value);
                continue;
            }

            entries.add(new BarEntry(i, stat.value));
            labels.add(stat.month.getMonth().name().substring(0, 3));
        }

        if (entries.isEmpty()) {
            chart.clear();
            chart.setNoDataText("No valid chart data.");
            chart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(ContextCompat.getColor(requireContext(), colorResId));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);

        // Show whole dollars inside the bars
        if (label.toLowerCase().contains("money")) {
            dataSet.setValueFormatter(new DollarValueFormatter(true));
            chart.getAxisLeft().setValueFormatter(new DollarValueFormatter(false));
        } else {
            dataSet.setValueFormatter(new WeightValueFormatter());
            chart.getAxisLeft().setValueFormatter(new WeightValueFormatter());
        }

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(Math.max(maxY, 10f));  // avoid maxY == 0

        chart.getXAxis().setCenterAxisLabels(true);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }

    private void updateGroupedChart(
            List<MonthlyStat> spentData,
            List<MonthlyStat> wasteData,
            List<MonthlyStat> usedData,
            BarChart chart
    ) {
        ArrayList<BarEntry> spentEntries = new ArrayList<>();
        ArrayList<BarEntry> wasteEntries = new ArrayList<>();
        ArrayList<BarEntry> usedEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < spentData.size(); i++) {
            float x = i;
            spentEntries.add(new BarEntry(x, spentData.get(i).value));
            wasteEntries.add(new BarEntry(x, wasteData.get(i).value));
            usedEntries.add(new BarEntry(x, usedData.get(i).value));
            labels.add(spentData.get(i).month.getMonth().name().substring(0, 3));
        }

        float maxY = getMaxValue(List.of(spentData, wasteData, usedData)) * 1.1f;

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(Math.max(maxY, 10f));  // ensure it's not flat

        chart.getAxisRight().setEnabled(false);

        BarDataSet spentSet = new BarDataSet(spentEntries, "Spent");
        BarDataSet wasteSet = new BarDataSet(wasteEntries, "Wasted");
        BarDataSet usedSet = new BarDataSet(usedEntries, "Used");

        spentSet.setColor(ContextCompat.getColor(requireContext(), R.color.fresh_green));
        wasteSet.setColor(ContextCompat.getColor(requireContext(), R.color.expiring_red));
        usedSet.setColor(ContextCompat.getColor(requireContext(), R.color.spoiler_yellow));

        spentSet.setValueTextSize(12f);
        wasteSet.setValueTextSize(12f);
        usedSet.setValueTextSize(12f);

        spentSet.setDrawValues(false);
        wasteSet.setDrawValues(false);
        usedSet.setDrawValues(false);

        chart.getAxisLeft().setValueFormatter(new DollarValueFormatter(false));

        BarData data = new BarData(spentSet, wasteSet, usedSet);
        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.25f;

        data.setBarWidth(barWidth);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        float groupWidth = data.getGroupWidth(groupSpace, barSpace);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(groupWidth * labels.size());

        chart.groupBars(0f, groupSpace, barSpace);
        chart.invalidate();
    }

    public class DollarValueFormatter extends ValueFormatter {
        private final boolean roundToWhole;

        public DollarValueFormatter() {
            this.roundToWhole = false;
        }

        public DollarValueFormatter(boolean roundToWhole) {
            this.roundToWhole = roundToWhole;
        }

        @Override
        public String getFormattedValue(float value) {
            if (Math.abs(value) < 0.01f) return "0";
            return roundToWhole ? "$" + Math.round(value) : "$" + String.format("%.2f", value);
        }
    }

    public class WeightValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            if (Math.abs(value) < 0.1f) return "0";
            return String.format("%.1f lbs", value);
        }
    }

}