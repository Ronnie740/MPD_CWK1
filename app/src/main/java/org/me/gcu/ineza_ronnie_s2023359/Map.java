package org.me.gcu.ineza_ronnie_s2023359;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class Map extends View {
    private ArrayList<Earthquake> earthquakes;

    public Map(Context context, ArrayList<Earthquake> earthquakes) {
        super(context);
        this.earthquakes = earthquakes;
    }

    public Map(Context context, AttributeSet attrs, ArrayList<Earthquake> earthquakes) {
        super(context, attrs);
        this.earthquakes = earthquakes;
    }

    public void setEarthquakes(ArrayList<Earthquake> earthquakes) {
        this.earthquakes = earthquakes;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set up the Paint object to draw the scatter plot points
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);

        // Set up the Paint object to draw the axis lines
        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(3f);


        // Find the minimum and maximum values for magnitude and depth
        float xMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        float yMin = Float.MAX_VALUE;
        float yMax = Float.MIN_VALUE;
        for (Earthquake earthquake : earthquakes) {
            float magnitude = (float) earthquake.getMagnitude();
            float depth = Float.parseFloat(earthquake.getDepth());
            if (magnitude < xMin) {
                xMin = magnitude;
            }
            if (magnitude > xMax) {
                xMax = magnitude;
            }
            if (depth < yMin) {
                yMin = depth;
            }
            if (depth > yMax) {
                yMax = depth;
            }
        }

        // Calculate the x and y scales to map the data points to the view coordinates
        float xRange = xMax - xMin;
        float yRange = yMax - yMin;
        float xScale = getWidth() / xRange;
        float yScale = getHeight() / yRange;

        // Draw the x and y axis lines
        canvas.drawLine(0f, yScale * yMax, getWidth(), yScale * yMax, axisPaint);
        canvas.drawLine(xScale * xMin, getHeight(), xScale * xMin, 0f, axisPaint);

        // Draw the scatter plot points
        for (Earthquake earthquake : earthquakes) {
            float x = (float) earthquake.getMagnitude() * xScale;
            float y = (yMax - Float.parseFloat(earthquake.getDepth())) * yScale;
            canvas.drawCircle(x, y, 10f, pointPaint);
        }
    }
}
