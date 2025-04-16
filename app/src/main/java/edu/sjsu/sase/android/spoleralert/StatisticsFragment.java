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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    ArrayList barArraylist;

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
        barArraylist = new ArrayList();
        barArraylist.add(new BarEntry(2f, 60));
        barArraylist.add(new BarEntry(3f, 50));
        barArraylist.add(new BarEntry(4f, 40));
        barArraylist.add(new BarEntry(5f, 30));
        barArraylist.add(new BarEntry(6f, 20));
    }
}