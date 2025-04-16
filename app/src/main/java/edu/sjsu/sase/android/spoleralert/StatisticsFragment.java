package edu.sjsu.sase.android.spoleralert;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private ArrayList<BarEntry> barArraylist;
    private ArrayList<String> monthLabels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View statistics_view = inflater.inflate(R.layout.fragment_statistics, container, false);
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
        List<MonthlyStat> recentFive = allStats.subList(0, Math.min(5, allStats.size()));

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
}