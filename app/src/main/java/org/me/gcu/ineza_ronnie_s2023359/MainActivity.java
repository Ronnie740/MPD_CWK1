//S2023359_Ronnie_Ineza
package org.me.gcu.ineza_ronnie_s2023359;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button startButton, chart, map;
    private String result = "";
    // Source of the earthquake data
    private String urlSource = "https://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";

    private boolean isDownloading = false;
    private ProgressDialog progressDialog;

    //handle the downloads and set them to 10 minute intervals while the activity is running
    private Handler handler = new Handler();
    private Runnable downloadRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isDownloading) {
                downloadXmlData(urlSource);
                Log.i("Download", "New Download");
            }
            handler.postDelayed(downloadRunnable, 10 * 60 * 1000); // schedule the download to run again in 10 minutes
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if device is connected to the internet and notify the user
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(this, "No internet connection available\n Can't Download XML Data", Toast.LENGTH_SHORT).show();
        }

        Log.e("MyTag", "in onCreate");
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        Log.e("MyTag", "after startButton");
        chart = findViewById(R.id.chart);
        chart.setOnClickListener(this);
        map = findViewById(R.id.map);
        map.setOnClickListener(this);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        handler.postDelayed(downloadRunnable, 0);

    }

    @Override
    public void onClick(View v) {
        Log.e("MyTag", "in onClick");
        if (v.getId() == R.id.startButton) {
            Parser parser = new Parser();
            parser.parseData(result);

            Intent intent = new Intent(this, table.class);
            intent.putExtra("earthquakes", parser.getEarthquakeList());
            startActivity(intent);
        }
        else if(v.getId() == R.id.chart){
            setContentView(R.layout.activity_chart);

            Parser parser = new Parser();
            parser.parseData(result);
            Log.d("Chart", "Start ----------- ");
            parser.printEarthquakes();
            Log.d("Chart", "END ----------- ");

            Intent intent = new Intent(this, Chart.class);
            intent.putExtra("earthquakes", parser.getEarthquakeList());
            startActivity(intent);

        }
        else if(v.getId() == R.id.map){
                setContentView(R.layout.activity_map);
                Parser parser = new Parser();
                parser.parseData(result);
                Log.d("<Map>", "Start ----------- ");
                parser.printEarthquakes();
                Log.d("<Map>", "END ----------- ");
                Intent intent = new Intent(this, Map.class);
                intent.putExtra("earthquakes", parser.getEarthquakeList());
                startActivity(intent);
            }
        Log.e("MyTag", "after startProgress");
    }
    private void downloadXmlData(String url) {
        if (isDownloading) {
            return;
        }
        isDownloading = true;
        progressDialog.show();
        new Thread(() -> {
            try {
                URL aurl = new URL(url);
                URLConnection yc = aurl.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                String inputLine;
                int itemCounter = 0;
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);
                    if (inputLine.contains("</item>")) {
                        itemCounter++;
                        if (itemCounter == 20) {
                            break;
                        }
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Result", "Result: "+ result);
            runOnUiThread(() -> {
                progressDialog.dismiss();
                Parser parser = new Parser();
                parser.parseData(result);
                isDownloading = false;
            });
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Stopped Downloads", "Download Runnable destroyed");
        handler.removeCallbacks(downloadRunnable); // stop the download task when the activity is destroyed
    }

}