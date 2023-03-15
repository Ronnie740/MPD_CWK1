package org.me.gcu.ineza_ronnie_s2023359;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button startButton, map;
    private String result = "";
    // Source of the earthquake data
    private String urlSource = "https://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";

    private boolean isDownloading = false;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MyTag", "in onCreate");
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        Log.e("MyTag", "after startButton");
        map = findViewById(R.id.map);
        map.setOnClickListener(this);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        //startProgress();
        if (!isDownloading) {
            isDownloading = true;
            new DownloadXmlTask().execute(urlSource);
        }


    }

    /**public void startProgress() {
        if (!isDownloading) {
            isDownloading = true;
            new DownloadXmlTask().execute(urlSource);
        }
    }**/

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
        else if(v.getId() == R.id.map){
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
        Log.e("MyTag", "after startProgress");
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";
            Log.e("MyTag", "in run");
            try {
                Log.e("MyTag", "in try");
                aurl = new URL(urls[0]);
                Log.e("MyTag", "URL: "+ aurl );
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag", "after ready");
                //
                // Now read the data. Make sure that there are no specific headers
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                // original
                /** while ((inputLine = in.readLine()) != null) {
                 //    result = result + inputLine;
                 //    Log.e("MyTag", inputLine);
                 }**/
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
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }
            return result;
        }
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            MainActivity.this.runOnUiThread(() -> {
                Log.d("UI thread", "I am the UI thread");

                /**Intent i = new Intent(MainActivity.this, Parser.class);
                i.putExtra("result", result); // pass the result as an intent extra
                startActivity(i);**/
                Parser parser = new Parser();
                parser.parseData(result);
                isDownloading = false;
            });
        }

    }

}