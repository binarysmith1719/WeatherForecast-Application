package com.codezilla.weather2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVmodel> weatherRVmodelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVmodel> weatherRVmodelArrayList) {
        this.context = context;
        this.weatherRVmodelArrayList = weatherRVmodelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherRVmodel model = weatherRVmodelArrayList.get(position);
        holder.temperatureTV.setText(model.getTemperature()+ "°C");
        holder.windTV.setText(model.getCondition());
        holder.timeTV.setText(model.getTime());
        Picasso.get().load(model.getIcon()).into(holder.conditionIV);
//        SimpleDateFormat input= new SimpleDateFormat("yyyy-mm-dd hh:mm");
//        SimpleDateFormat output= new SimpleDateFormat("hh:mm aa");
//        try{

//            Date t= input.parse(model.getTime());
//            holder.timeTV.setText(output.format(t));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int getItemCount() {
        return weatherRVmodelArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView temperatureTV,timeTV,windTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV=itemView.findViewById(R.id.idWindspeed);
            temperatureTV=itemView.findViewById(R.id.idTVTemperature);
            timeTV=itemView.findViewById(R.id.idTVTime);
            conditionIV=itemView.findViewById(R.id.idTVCondition);
        }
    }
}
