package org.me.gcu.ineza_ronnie_s2023359;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Parser {
    ArrayList<Earthquake> earthquakeList = new ArrayList<>();

    public void parseData(String dataToParse) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(dataToParse));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    if (tagName.equalsIgnoreCase("description")) {
                        // Get the text inside the description tag and split it by semicolon
                        String[] parts = xpp.nextText().split(";");

                        Earthquake earthquake = new Earthquake();

                        for (String part : parts) {
                            String trim = part.substring(part.indexOf(":") + 1).trim();
                            if (part.contains("Location")) {
                                // Extract the earthquake location
                                earthquake.setLocation(trim);
                            } else if (part.contains("Lat/long")) {
                                // Extract the latitude and longitude
                                String[] latLong = trim.split(",");
                                double latitude = Double.parseDouble(latLong[0].trim());
                                double longitude = Double.parseDouble(latLong[1].trim());
                                earthquake.setLatitude(latitude);
                                earthquake.setLongitude(longitude);
                            } else if (part.contains("Depth")) {
                                // Extract the depth
                                String depth = trim.replaceAll("[^\\d.]", "");
                                earthquake.setDepth(depth);
                            } else if (part.contains("Magnitude")) {
                                // Extract the magnitude
                                double magnitude = Double.parseDouble(trim);
                                earthquake.setMagnitude(magnitude);
                            } else if (part.contains("date/time")) {
                                String dateTime = trim;
                                // Create a SimpleDateFormat object to parse the date
                                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
                                // Parse the date string
                                Date date = inputFormat.parse(dateTime);
                                // Create a SimpleDateFormat object to format the date in the required format
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                // Format the date in the required format
                                String formattedDate = outputFormat.format(date);
                                earthquake.setDate(formattedDate);
                            }
                        }

                        if (earthquake.getLocation() != null && earthquake.getDepth() != null && earthquake.getMagnitude() != 0.0 && earthquake.getLongitude() != 0.0 && earthquake.getLatitude() != 0.0) {
                            earthquakeList.add(earthquake);
                        }
                    }
                }

                eventType = xpp.next();
            } // End of while
        } catch (XmlPullParserException ae1) {
            Log.e("MyTag", "Parsing error" + ae1.toString());
        } catch (IOException ae1) {
            Log.e("MyTag", "IO error during parsing");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Earthquake> getEarthquakeList() {
        return earthquakeList;
    }

    public void printEarthquakes() {
        for (Earthquake earthquake : earthquakeList) {
            Log.i("Earthquake", "Location: " + earthquake.getLocation());
            Log.i("Earthquake", "Magnitude: " + earthquake.getMagnitude());
            Log.i("Earthquake", "Depth: " + earthquake.getDepth());
            Log.i("Earthquake", "Date: " + earthquake.getDate());
            Log.i("Earthquake", "Latitude: " + earthquake.getLatitude());
            Log.i("Earthquake", "Longitude: " + earthquake.getLongitude());
        }
    }
}