//S2023359_Ronnie_Ineza
package org.me.gcu.ineza_ronnie_s2023359;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Chart extends AppCompatActivity {

    private ScatterChart mScatterChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        // Get a reference to the ScatterChart defined in the XML layout file
        mScatterChart = findViewById(R.id.chart);

        // Set up the ScatterChart
        mScatterChart.getDescription().setEnabled(false);

        Intent intent = getIntent();
        ArrayList<Earthquake> earthquakes = (ArrayList<Earthquake>) intent.getSerializableExtra("earthquakes");

        // Create a list of earthquake data points
        ArrayList<Entry> earthquakeData = new ArrayList<>();
        Log.d("arraySize", "Initial size of earthquakeData: " + earthquakeData.size());
        float maxMagnitude = 0f;
        float maxDepth = 0f;
        int counter = 0;
        for (Earthquake earthquake : earthquakes) {
            float magnitude = (float) earthquake.getMagnitude();
            //Log.i("magnitude", "" + magnitude);
            float depth = Float.parseFloat(earthquake.getDepth());
            //Log.i("Depth", "" + depth);
            Entry entry = new Entry(magnitude, depth);
            earthquakeData.add(entry);
            Log.d("arraySize", "Size of earthquakeData after adding entry: " + earthquakeData.size());
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
            }
            if (depth > maxDepth) {
                maxDepth = depth;
            }
            /**counter ++;
            if (counter == 5){
                break;
            }**/
        }
        Log.i("Earthquake Data", ": "+ earthquakeData);
        // Set up the ScatterDataSet for the earthquake data
        Collections.sort(earthquakeData, (e1, e2) -> Float.compare(e1.getY(), e2.getY()));
        ScatterDataSet dataSet = new ScatterDataSet(earthquakeData, "Earthquakes");

        // Set unique colors for each earthquake
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < earthquakes.size(); i++) {
            colors.add(ColorTemplate.COLORFUL_COLORS[i % ColorTemplate.COLORFUL_COLORS.length]);
        }
        dataSet.setColors(colors);

        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        dataSet.setScatterShapeSize(40f); // Set point size to be 20f
        dataSet.setDrawValues(false);

        // Add the ScatterDataSet to a ScatterData object
        ScatterData scatterData = new ScatterData(dataSet);

        // Set the ScatterData on the ScatterChart
        mScatterChart.setData(scatterData);

        // Set the x-axis and y-axis ranges based on the maximum magnitude and depth values
        mScatterChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Set X axis to be at the bottom
        mScatterChart.getXAxis().setGranularity(1f); // Set X axis granularity to 1f
        mScatterChart.getXAxis().setLabelCount( (int)maxMagnitude + 1); // Set X axis label count to maxMagnitude + 1

        mScatterChart.getAxisLeft().setAxisMinimum(0f);
        mScatterChart.getAxisLeft().setAxisMaximum(maxDepth + 100f);
        mScatterChart.getAxisLeft().setLabelCount((int) maxDepth + 1);

        // Refresh the ScatterChart
        mScatterChart.invalidate();
    }
    @Override
    public void onBackPressed() {
        // Create an intent to start the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        // Set the flag to clear the activity stack and start a new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start the activity
        startActivity(intent);
        finish();

    }


}