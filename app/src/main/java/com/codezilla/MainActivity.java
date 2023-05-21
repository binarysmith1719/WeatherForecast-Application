package com.codezilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codezilla.weather2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Searching", Toast.LENGTH_SHORT).show();
                String city= cityEdt.getText().toString();
                cname=city;
                Log.d("giv",city);
                if(city.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    citynameTV.setText(cname);
                    getCityPosition_thenData(city);

                }

            }
        });

        getCityPosition_thenData(cname);
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

    private void getCityPosition_thenData(String cname)
    {
        String url="http://api.openweathermap.org/geo/1.0/direct?q="+cname+"&limit=1&appid=4fdcbf2e83a26cc2e9581e2415f59d61";
        RequestQueue queue=Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int lat=jsonObject.getInt("lat");
                    int lon=jsonObject.getInt("lon");
//                    Toast.makeText(MainActivity.this,""+lat+"**"+lon, Toast.LENGTH_SHORT).show();
                    //After getting the City's latitude and longitude get the weather info
                    getWeatherInfo(""+lat,""+lon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },null);

        queue.add(stringRequest);
    }

    private void getWeatherInfo(String lat , String lon)
    {
        String url="http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&appid=4fdcbf2e83a26cc2e9581e2415f59d61";
//        Toast.makeText(MainActivity.this,"getting the data", Toast.LENGTH_SHORT).show();
        RequestQueue queue =Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(MainActivity.this,"got the data", Toast.LENGTH_SHORT).show();
                citynameTV.setText(cname);
                pgbr.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVmodelArrayList.clear();
//                Log.d("bug","got_response");
                try {
                    JSONArray jsonArray= response.getJSONArray("list");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject= jsonArray.getJSONObject(i);
                        long time_in_sec=jsonObject.getLong("dt");
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy   HH:mm");
                        String time = sdf.format(new Date(time_in_sec*1000));

                        double tempInKelvin=jsonObject.getJSONObject("main").getDouble("temp");
                        double tempInCelcius=tempInKelvin-273.15;
                        DecimalFormat decfor= new DecimalFormat("0.00");
                        String temp = decfor.format(tempInCelcius);
//                        =Double.toString(tempInCelcius);

                        String condition= jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                        String iconid=jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                        String icon="https://openweathermap.org/img/wn/"+iconid+"@2x.png";
//                    if(isday==1)
//                    {
//                        Picasso.get().load("https://images.unsplash.com/photo-1484402628941-0bb40fc029e7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV);
//                    }
//                    else
//                        Picasso.get().load("https://images.unsplash.com/photo-1505322022379-7c3353ee6291?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV);
                        weatherRVmodelArrayList.add(new WeatherRVmodel(time,temp,icon,condition));

                        if(i==0)
                        {
                            temperatureTV.setText(temp+"°C");
                            conditionTV.setText(condition);
                            Picasso.get().load(icon).into(iconIV);
                        }
                    }

                    weatherRVAdapter.notifyDataSetChanged();
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