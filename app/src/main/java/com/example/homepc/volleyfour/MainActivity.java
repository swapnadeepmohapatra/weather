package com.example.homepc.volleyfour;

//Made by- Swapnadeep Mohapatra
//All Rights Reserved

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // "https://api.openweathermap.org/data/2.5/weather?q=Bhubaneshwar&appid=2ba5d9b471b7913ef01dbf36905c83bb"

    String baseURL = "https://api.openweathermap.org/data/2.5/weather?q=";
    String api = "&appid=2ba5d9b471b7913ef01dbf36905c83bb";

    Button button;
    EditText city;
    TextView desc;
    TextView hum;
    TextView temp, place;
    ImageView imageView;
    ProgressDialog progressDialog;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-7589870232837078~4656918164");

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-7589870232837078/9717673153");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        // Declaration
        button = findViewById(R.id.buttonapi);
        city = findViewById(R.id.editText);
        desc = findViewById(R.id.textView3);
        hum = findViewById(R.id.textView5);
        temp = findViewById(R.id.textView4);
        imageView = findViewById(R.id.imageView);
        place = findViewById(R.id.city);
        progressDialog = new ProgressDialog(this);

        //  On Click Listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }

                // Progress Dialog
                progressDialog.setMessage("Fetching....");

                String mCity = city.getText().toString();
                city.getText().clear();

                // Show Progress Dialog
                progressDialog.show();

                // When City is not Entered
                if (mCity.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {

                    String myURL = baseURL + mCity + api;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, myURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Json", "JSON :  " + response);

                            try {
                                String name = response.getString("name");
                                place.setText(name);
                                place.setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Main Weather & Description
                            try {
                                String info = response.getString("weather");

                                JSONArray ar = new JSONArray(info);

                                for (int i = 0; i < ar.length(); i++) {
                                    JSONObject parObj = ar.getJSONObject(i);

                                    // Weather Description Text
                                    String weatherDesc = parObj.getString("description");
                                    desc.setText(weatherDesc);
                                    desc.setVisibility(View.VISIBLE);

                                    // Weather Icon URL
                                    String weatherIcon = parObj.getString("icon");
                                    Log.i("Icon", "Icon : " + weatherIcon);

                                    final String imgMyUrl = "http://www.openweathermap.org/img/w/" + weatherIcon + ".png";

                                    // Image For The Weather
                                    ImageRequest imageRequest = new ImageRequest(
                                            imgMyUrl,
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap response) {
                                                    imageView.setImageBitmap(response);
                                                    imageView.setVisibility(View.VISIBLE);

                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.i("Error", "Error");
                                                }
                                            }
                                    );
                                    MySingelton.getInstance(MainActivity.this).addToRequestQue(imageRequest);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Temperature and Humidity

                            try {
                                String in = response.getString("main");

                                JSONObject co = new JSONObject(in);

                                // Fetching Temp & Hum
                                int tem = co.getInt("temp");
                                int humidity = co.getInt("humidity");

                                //Temp Conversion to celsius
                                tem = tem - 273;

                                // Adding Suffix and Prefix
                                String humidityMsg = "Humidity : " + humidity + "%";
                                String tempMsg = "Temp : " + tem + "˚C";

                                // Displaying Text for Temp & Humidity
                                hum.setText(humidityMsg);
                                hum.setVisibility(View.VISIBLE);
                                temp.setText(tempMsg);
                                temp.setVisibility(View.VISIBLE);

                                // Dismiss Progress Dialog
                                progressDialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Error
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Error", "Err: " + error);
                            String err = error.toString();
                            Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    });
                    MySingelton.getInstance(MainActivity.this).addToRequestQue(jsonObjectRequest);
                }
            }
        });
    }
}
