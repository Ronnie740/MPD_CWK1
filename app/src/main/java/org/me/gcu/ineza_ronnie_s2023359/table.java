//S2023359_Ronnie_Ineza
package org.me.gcu.ineza_ronnie_s2023359;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class table extends AppCompatActivity {

    private ArrayList<Earthquake> earthquakes;
    private TableLayout tableLayout;
    private boolean magnitudeAscending = true;
    private boolean depthAscending = true;
    Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table);
        Log.i("TAG", "onCreate: Table Class");

        Intent intent = getIntent();
        earthquakes = (ArrayList<Earthquake>) intent.getSerializableExtra("earthquakes");

        tableLayout = findViewById(R.id.table_layout);

        for (Earthquake earthquake : earthquakes) {
            // Create a new row
            TableRow row = new TableRow(this);

            // Create a reusable LayoutParams object
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
            layoutParams.setMargins(0, 5, 0, 5);

            // Create TextViews for each earthquake property and set their layout parameters
            TextView locationView = new TextView(this);
            locationView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            locationView.setText(getLocationCode(earthquake.getLocation()));
            locationView.setLayoutParams(layoutParams);
            row.addView(locationView);

            TextView magnitudeView = new TextView(this);
            magnitudeView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            magnitudeView.setText(Double.toString(earthquake.getMagnitude()));
            double magnitude = earthquake.getMagnitude();
            setMagnitudeViewColor(magnitudeView, magnitude);
            magnitudeView.setLayoutParams(layoutParams);
            row.addView(magnitudeView);

            TextView depthView = new TextView(this);
            depthView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            depthView.setText(earthquake.getDepth());
            depthView.setLayoutParams(layoutParams);
            row.addView(depthView);


            // Create the column headers
            TextView magnitudeHeader = findViewById(R.id.magnitude);
            magnitudeHeader.setOnClickListener(v -> {
                sortAndDisplayEarthquakes(new Comparator<Earthquake>() {
                    @Override
                    public int compare(Earthquake e1, Earthquake e2) {
                        return Double.compare(e1.getMagnitude(), e2.getMagnitude());
                    }
                }, magnitudeAscending);

                magnitudeAscending = !magnitudeAscending;
            });

            TextView depthHeader = findViewById(R.id.depth);
            depthHeader.setOnClickListener(v -> {
                sortAndDisplayEarthquakes((e1, e2) -> {
                    double depth1 = Double.parseDouble(e1.getDepth());
                    double depth2 = Double.parseDouble(e2.getDepth());
                    return Double.compare(depth1, depth2);
                }, depthAscending);

                depthAscending = !depthAscending;
            });

            TextView dateView = new TextView(this);
            dateView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dateView.setText(earthquake.getDate());
            dateView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            row.addView(dateView);

            String distance = getDirectionFromMauritius(earthquake);
            int x = calculateDistance(earthquake.getLatitude(),earthquake.getLongitude());

            row.setOnClickListener(v -> {
                // Show alert box with earthquake details
                AlertDialog.Builder builder = new AlertDialog.Builder(table.this);
                builder.setTitle("Earthquake Details")
                        .setMessage("Magnitude: " + earthquake.getMagnitude() + "\nLocation: " + earthquake.getLocation() + "\nDate: " + earthquake.getDate()+"\nDirection: "+distance+"\nDistance from\nMauritius:"+x+" kms")
                        .setPositiveButton("OK", null)
                        .show();
            });

            // Add the row to the table layout
            tableLayout.addView(row);
        }

        // Create the filter row
        TableRow filterRow = new TableRow(this);

        Spinner magnitudeSpinner = findViewById(R.id.magnitude_spinner); // get existing spinner by id
        String[] magnitudeRange = {"Choose","0-2", "2-4", "4-6", "6-8", "8+"};
        ArrayAdapter<String> magnitudeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, magnitudeRange);
        magnitudeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magnitudeSpinner.setAdapter(magnitudeAdapter);
        applyFilter(magnitudeSpinner,"Magnitude");


        Spinner locationSpinner = findViewById(R.id.location_spinner); // get existing spinner by id
        // Get unique location values from earthquakes
        Set<String> uniqueLocations = new HashSet<>();
        for (Earthquake earthquake : earthquakes) {
            uniqueLocations.add(earthquake.getLocation());
        }
        // Create array adapter with unique location values
        String[] locationValues = new String[uniqueLocations.size() + 1];
        locationValues[0] = "Choose";
        int i = 1;
        for (String location : uniqueLocations) {
            locationValues[i] = location;
            i++;
        }
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationValues);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        applyFilter(locationSpinner,"Location");

        // Date filter button to display calendar popup
        Spinner depthSpinner = findViewById(R.id.depth_spinner); // get existing spinner by id
        String[] depthRange = {"Choose","0-100", "100-200", "200-400", "400-600","600+"};
        ArrayAdapter<String> depthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, depthRange);
        depthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        depthSpinner.setAdapter(depthAdapter);
        applyFilter(depthSpinner,"Depth");
        // Add the filter row to the table layout
        tableLayout.addView(filterRow);

        dateButton = findViewById(R.id.date_button);
        dateButton.setOnClickListener(v -> {
            // Create a calendar object to get the current date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Set the selected date to a calendar object
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year1, monthOfYear, dayOfMonth);

                        // Filter the earthquake data based on the selected date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String dateString = dateFormat.format(selectedDate.getTime());
                        ArrayList<Earthquake> filteredEarthquakes = filterEarthquakesByDate(earthquakes, dateString);

                        // Update the table layout with the filtered data
                        updateTableLayout(filteredEarthquakes);
                    }, year, month, day);

            // Show the date picker dialog
            datePickerDialog.show();
        });

    }
    public String getLocationCode(String location) {
        String code = "";
        String[] parts = location.split(",");
        if (parts.length > 0) {
            String locationName = parts[0].trim();
            if (locationName.length() >= 3) {
                code = locationName.substring(0, 3).toUpperCase();
            } else {
                code = locationName.toUpperCase();
            }
        }
        return code;
    }

    private void updateTableLayout(List<Earthquake> filteredEarthquakes) {
        TableLayout tableLayout = findViewById(R.id.filtered_list);

        // Remove all existing rows from the table
        tableLayout.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createHeaderTextView("Date"));
        headerRow.addView(createHeaderTextView("Location"));
        headerRow.addView(createHeaderTextView("Magnitude"));
        headerRow.addView(createHeaderTextView("Depth"));
        tableLayout.addView(headerRow);

        // Add rows for each earthquake that matches the selected filters
        for (Earthquake earthquake : filteredEarthquakes) {
            TableRow row = new TableRow(this);
            row.addView(createDataTextView(earthquake.getDate()));
            row.addView(createDataTextView(earthquake.getLocation()));
            row.addView(createDataTextView(Double.toString(earthquake.getMagnitude())));
            row.addView(createDataTextView(earthquake.getDepth()));
            tableLayout.addView(row);
        }
    }

    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundColor(Color.LTGRAY);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    private TextView createDataTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }
    public ArrayList<Earthquake> filterEarthquakesByDate(ArrayList<Earthquake> earthquakes, String dateString) {
        ArrayList<Earthquake> filteredEarthquakes = new ArrayList<>();

        for (Earthquake earthquake : earthquakes) {
            if (earthquake.getDate().equals(dateString)) {
                filteredEarthquakes.add(earthquake);
            }
        }
        return filteredEarthquakes;
    }

    private void setMagnitudeViewColor(TextView magnitudeView, double magnitude) {
        if (magnitude >= 7.0) {
            magnitudeView.setBackgroundColor(Color.parseColor("#FF0000")); // Red
        } else if (magnitude >= 6.0) {
            magnitudeView.setBackgroundColor(Color.parseColor("#FFA500")); // Orange
        } else if (magnitude >= 4.0) {
            magnitudeView.setBackgroundColor(Color.parseColor("#FFFF00")); // Yellow
        } else if (magnitude >= 2.0) {
            magnitudeView.setBackgroundColor(Color.parseColor("#00FF00")); // Lime Green
        } else {
            magnitudeView.setBackgroundColor(Color.parseColor("#0000FF")); // Blue
        }
    }

    // Returns the direction of the earthquake relative to Mauritius
    public String getDirectionFromMauritius(Earthquake earthquake) {
        double mauritiusLatitude = -20.3484049; // Mauritius latitude
        double mauritiusLongitude = 57.5521526; // Mauritius longitude
        String direction="";

        double earthquakeLatitude = earthquake.getLatitude();
        double earthquakeLongitude = earthquake.getLongitude();

        if (earthquakeLongitude < mauritiusLongitude) {
            if (earthquakeLatitude < mauritiusLatitude) {
                direction = "southwest";
            } else if (earthquakeLatitude > mauritiusLatitude) {
                direction =  "northwest";
            } else {
                direction =  "west";
            }
        } else if (earthquakeLongitude > mauritiusLongitude) {
            if (earthquakeLatitude < mauritiusLatitude) {
                direction = "southeast";
            } else if (earthquakeLatitude > mauritiusLatitude) {
                direction= "northeast";
            } else {
                direction = "east";
            }
        } else {
            if (earthquakeLatitude < mauritiusLatitude) {
                direction = "south";
            } else if (earthquakeLatitude > mauritiusLatitude) {
                direction =  "north";
            } else {
                direction = "Mauritius";
            }
    }
        return direction;
    }

    // Returns the distance between two points on Earth given their longitudes and latitudes in Kilometers using the haversine formula/function
    private int calculateDistance(double lat2, double lon2) {
        double lat1 = -20.3484049; // Mauritius latitude
        double lon1 = 57.5521526;
        int R = 6371; // Earth radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return (int)distance;
    }
    private TableRow createTableRowForEarthquake(Earthquake earthquake){
        TableRow row = new TableRow(this);
        TextView location = new TextView(this);
        TextView magnitude = new TextView(this);
        TextView depth = new TextView(this);
        TextView date = new TextView(this);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        layoutParams.setMargins(0, 5, 0, 5);

        location.setText(getLocationCode(earthquake.getLocation()));
        location.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        location.setLayoutParams(layoutParams);
        row.addView(location);

        magnitude.setText(Double.toString(earthquake.getMagnitude()));
        magnitude.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setMagnitudeViewColor(magnitude, earthquake.getMagnitude());
        magnitude.setLayoutParams(layoutParams);
        row.addView(magnitude);

        depth.setText(earthquake.getDepth());
        depth.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        depth.setLayoutParams(layoutParams);
        row.addView(depth);

        date.setText(earthquake.getDate());
        date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        date.setLayoutParams(layoutParams);
        row.addView(date);
        return row;
    }
    private void applyFilter(Spinner spinner, String filterType) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                List<Earthquake> filteredEarthquakes = new ArrayList<>();
                if (!selectedItem.equals("Choose")) {
                    for (Earthquake earthquake : earthquakes) {
                        switch (filterType) {
                            case "Location":
                                if (selectedItem.equals(earthquake.getLocation())) {
                                    filteredEarthquakes.add(earthquake);
                                }
                                break;
                            case "Magnitude":
                                if (selectedItem.equals("0-2")) {
                                    if (earthquake.getMagnitude() >= 0.0 && earthquake.getMagnitude() <= 2.0) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("2-4")) {
                                    if (earthquake.getMagnitude() >= 2.0 && earthquake.getMagnitude() <= 4.0) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("4-6")) {
                                    if (earthquake.getMagnitude() > 4.0 && earthquake.getMagnitude() <= 6.0) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("6-8")) {
                                    if (earthquake.getMagnitude() > 6.0 && earthquake.getMagnitude() <= 8.0) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("8+")) {
                                    if (earthquake.getMagnitude() > 8.0) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                }
                                break;
                            case "Depth":
                                int loopDepth = Integer.parseInt(earthquake.getDepth());
                                if (selectedItem.equals("0-100")) {
                                    if (loopDepth >= 0 && loopDepth <= 100) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("100-200")) {
                                    if (loopDepth > 100 && loopDepth <= 200) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("200-400")) {
                                    if (loopDepth > 200 && loopDepth <= 400) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("400-600")) {
                                    if (loopDepth > 400 && loopDepth <= 600) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                } else if (selectedItem.equals("600+")) {
                                    if (loopDepth > 600) {
                                        filteredEarthquakes.add(earthquake);
                                    }
                                }
                                break;
                        }
                    }
                    updateTableLayout(filteredEarthquakes);
                } else {
                    TableLayout tableLayout = findViewById(R.id.filtered_list);
                    tableLayout.removeAllViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }
    private void sortAndDisplayEarthquakes(Comparator<Earthquake> comparator, boolean isAscending) {
        // Sort earthquakes based on the given comparator
        Collections.sort(earthquakes, comparator);

        // Toggle the order of the rows
        if (!isAscending) {
            Collections.reverse(earthquakes);
        }

        TableRow header = findViewById(R.id.header_row);
        TableRow filter1 = findViewById(R.id.filter1);
        TableRow filter2 = findViewById(R.id.filter2);
        TableRow filter3 = findViewById(R.id.filter3);
        TableRow filter4 = findViewById(R.id.filter4);
        TableRow filter5 = findViewById(R.id.filter5);
        TableRow filter6 = findViewById(R.id.filter6);
        TableRow filter7 = findViewById(R.id.filter7);
        TableRow filter8 = findViewById(R.id.filter8);
        // Clear the existing rows
        tableLayout.removeAllViews();

        // Add the filters and header
        tableLayout.addView(filter1);
        tableLayout.addView(filter2);
        tableLayout.addView(filter3);
        tableLayout.addView(filter4);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tableLayout.addView(filter5);
            tableLayout.addView(filter6);
            tableLayout.addView(filter7);
            tableLayout.addView(filter8);
        }
        tableLayout.addView(header);

        // Add the sorted rows to the table layout
        for (Earthquake earthquake : earthquakes) {
            TableRow row = createTableRowForEarthquake(earthquake);
            String distance = getDirectionFromMauritius(earthquake);
            int x = calculateDistance(earthquake.getLatitude(),earthquake.getLongitude());
            row.setOnClickListener(c -> {
                // Show alert box with earthquake details
                AlertDialog.Builder builder = new AlertDialog.Builder(table.this);
                builder.setTitle("Earthquake Details")
                        .setMessage("Magnitude: " + earthquake.getMagnitude() + "\nLocation: " + earthquake.getLocation() + "\nDate: " + earthquake.getDate()+"\nDirection: "+distance+"\nDistance from \n Mauritius: "+x+"kms")
                        .setPositiveButton("OK", null)
                        .show();
            });
            tableLayout.addView(row);
        }
    }


}


