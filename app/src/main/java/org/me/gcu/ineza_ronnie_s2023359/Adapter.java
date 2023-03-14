package org.me.gcu.ineza_ronnie_s2023359;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    private Context context;
    private ArrayList<Earthquake> earthquakes;

    public Adapter(Context context, ArrayList<Earthquake> earthquakes) {
        this.context = context;
        this.earthquakes = earthquakes;
    }

    @Override
    public int getCount() {
        return earthquakes.size();
    }

    @Override
    public Earthquake getItem(int position) {
        return earthquakes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public TableLayout getView(int position, View convertView, ViewGroup parent) {
        TableLayout row = (TableLayout) convertView;

        if (row == null) {
            row = (TableLayout) LayoutInflater.from(context).inflate(R.layout.table, parent, false);
        }

        Earthquake earthquake = getItem(position);

        /**TextView locationTextView = row.findViewById(R.id.location);
        TextView magnitudeTextView = row.findViewById(R.id.magnitude);
        TextView depthTextView = row.findViewById(R.id.depth);
        TextView longitudeTextView = row.findViewById(R.id.longitude);
        TextView latitudeTextView = row.findViewById(R.id.latitude);
        TextView dateTextView = row.findViewById(R.id.date);

        locationTextView.setText(earthquake.getLocation());
        magnitudeTextView.setText(Double.toString(earthquake.getMagnitude()));
        depthTextView.setText(earthquake.getDepth());
        longitudeTextView.setText(Double.toString(earthquake.getLongitude()));
        latitudeTextView.setText(Double.toString(earthquake.getLatitude()));
        dateTextView.setText(earthquake.getDate().toString());
         **/
        return row;
    }

}

