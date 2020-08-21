package com.iamdj.covid19tracker;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView cname; // for replacing current country name with passed through second activity.
    String url = "https://disease.sh/v3/covid-19/countries/india"; //  for india

    TextView tvPopulation,tvTotalCases,tvTotalRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths,tvTodayRecovered;
    SimpleArcLoader simpleArcLoader;
    ScrollView scrollView;
    PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register components
        tvPopulation = findViewById(R.id.tvPopulation);
        tvTotalCases = findViewById(R.id.tvTotalCases);
        tvTotalRecovered = findViewById(R.id.tvTotalRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvTodayRecovered = findViewById(R.id.tvTodayRecovered);

        simpleArcLoader = findViewById(R.id.loader);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.piechart);

        cname = findViewById(R.id.cname);

        fetchData();
    }

    private void fetchData() {
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //String url  = "https://disease.sh/v3/covid-19/all/"; // for all globe
        simpleArcLoader.start();


        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Setting fetched data into text views
                            tvPopulation.setText(jsonObject.getString("population"));
                            tvTotalCases.setText(jsonObject.getString("cases"));
                            tvTotalRecovered.setText(jsonObject.getString("recovered"));
                            tvCritical.setText(jsonObject.getString("critical"));
                            tvActive.setText(jsonObject.getString("active"));
                            tvTodayCases.setText(jsonObject.getString("todayCases"));
                            tvTotalDeaths.setText(jsonObject.getString("deaths"));
                            tvTodayDeaths.setText(jsonObject.getString("todayDeaths"));
                            tvTodayRecovered.setText(jsonObject.getString("todayRecovered"));

                            // Setting data into pie chart and distributed according to their quantity
                            pieChart.addPieSlice(new PieModel("Total Cases",Integer.parseInt(tvTotalCases.getText().toString()), Color.parseColor("#FFA726")));
                            pieChart.addPieSlice(new PieModel("Total Recovered",Integer.parseInt(tvTotalRecovered.getText().toString()), Color.parseColor("#66BB6A")));
                            pieChart.addPieSlice(new PieModel("Total Deaths",Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
                            pieChart.addPieSlice(new PieModel("Total Active",Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
                            pieChart.startAnimation();


                            // stop arc loader & make invisible
                            simpleArcLoader.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                            // Make scroll view visible
                            scrollView.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            simpleArcLoader.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleArcLoader.stop();
                simpleArcLoader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, error.getMessage()+" - Invalid country", Toast.LENGTH_SHORT).show();
                pieChart.clearChart();
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(request);
    }




    // Menu for Tracking corona details country wise
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh_id) {
            pieChart.clearChart();
            fetchData();
            Toast.makeText(this, "Loading data...", Toast.LENGTH_SHORT).show();
        }

        // Start Second Activity
        if(item.getItemId() == R.id.search_id){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(intent, 1); // sent request code
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("myKey");
                cname.setText(result+"\'s stats");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

                pieChart.clearChart();
                url = "https://disease.sh/v3/covid-19/countries/"+result;
                fetchData();
            }
            else{
                cname.setText("india's stats");
                url = "https://disease.sh/v3/covid-19/countries/india"; //  for india
            }
        }
    }
}
