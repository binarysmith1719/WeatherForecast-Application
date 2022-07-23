package com.codezilla.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView  citynameTV,temperatureTV,conditionTV;
    private RecyclerView weatherRV;
    private ImageView backIV,iconIV,searchIV;
    private TextInputEditText cityEdt;
    private ProgressBar pgbr;
    private RelativeLayout homeRL;
    private ArrayList<WeatherRVmodel>  weatherRVmodelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int Permissioncode=1;
    private String cname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL=findViewById(R.id.idRLHome);
        citynameTV=findViewById(R.id.textView);
        temperatureTV=findViewById(R.id.idTVTemperature);
        conditionTV=findViewById(R.id.idTVCondition);
        cityEdt=findViewById(R.id.idEDTCity);
        pgbr=findViewById(R.id.progressBar);
        weatherRV=findViewById(R.id.idRvWeather);
        backIV=findViewById(R.id.idIVback);
        searchIV=findViewById(R.id.idIVsearch);
        iconIV=findViewById(R.id.idIVIcon);
        weatherRVmodelArrayList=new ArrayList<>();
        Log.d("giv","here1");
        weatherRVAdapter=new WeatherRVAdapter(this,weatherRVmodelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        cname="Delhi";
        getWeatherInfo(cname);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city= cityEdt.getText().toString();
                Log.d("giv",city);
                if(city.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    citynameTV.setText(cname);
                    getWeatherInfo(city);

                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==Permissioncode)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(MainActivity.this, "Please provide the permission", Toast.LENGTH_SHORT).show();
            finish();;
        }
    }

    private void getWeatherInfo(String cname)
    {
        String url="http://api.weatherapi.com/v1/forecast.json?key=4caf333e3dc347cab5750204222207&q="+cname+"&days=1&aqi=no&alerts=no";
        citynameTV.setText(cname);
        Toast.makeText(MainActivity.this, cname, Toast.LENGTH_SHORT).show();
        RequestQueue queue =Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pgbr.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVmodelArrayList.clear();
                Log.d("bug","got_response");
                try {
                    String temp= response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temp+"°C");
                    int isday=response.getJSONObject("current").getInt("is_day");
                    String condition= response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionicon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionicon)).into(iconIV);
                    conditionTV.setText(condition);
                    if(isday==1)
                    {
                        Picasso.get().load("https://images.unsplash.com/photo-1484402628941-0bb40fc029e7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV);
                    }
                    else
                        Picasso.get().load("https://images.unsplash.com/photo-1505322022379-7c3353ee6291?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV);

                    JSONArray hourarray= response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour");
//                   Log.d("giv","here6");
                    for(int i=0;i<hourarray.length();i++)
                    {
                        JSONObject hrobj=hourarray.getJSONObject(i);
                        String time=hrobj.getString("time");
                        String temp1=hrobj.getString("temp_c");
                        String image=hrobj.getJSONObject("condition").getString("icon");
                        String time1=hrobj.getString("time");
                        String wind=hrobj.getString("wind_kph");
                        Log.d("giv",time1+" "+temp1+" "+image+" "+wind);
                        weatherRVmodelArrayList.add(new WeatherRVmodel(time1,temp1,image,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
//                   Log.d("giv","here7");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Enter valid city name", Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(jsonObjectRequest);
    }
}