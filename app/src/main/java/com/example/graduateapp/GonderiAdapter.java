package com.example.graduateapp;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GonderiAdapter extends RecyclerView.Adapter<GonderiAdapter.GonderiHolder>{

    private ArrayList<Gonderi> gonderiler;
    private Context context;

    public GonderiAdapter(ArrayList<Gonderi> gonderiler, Context context) {
        this.gonderiler = gonderiler;
        this.context = context;
    }

    @NonNull
    @Override
    public GonderiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_gonderi, parent, false);
        return new GonderiAdapter.GonderiHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GonderiHolder holder, int position) {
        Gonderi gonderi = gonderiler.get(position);
        holder.setData(gonderi);
    }

    @Override
    public int getItemCount() {
        return gonderiler.size();
    }

    class GonderiHolder extends RecyclerView.ViewHolder{

        ImageView gonderiMedya;
        TextView nameSurname, icerik;
        Button silButton;
        public GonderiHolder(@NonNull View itemView) {
            super(itemView);

            gonderiMedya = (ImageView) itemView.findViewById(R.id.gonderiMedya);
            nameSurname = (TextView) itemView.findViewById(R.id.gonderiName);
            icerik = (TextView) itemView.findViewById(R.id.gonderiIcerik);

        }
        public void setData(Gonderi gonderi){
            this.nameSurname.setText(gonderi.getName() + " " + gonderi.getSurname());
            this.icerik.setText(gonderi.getIcerik());
            if(gonderi.getMedyaLinki() != null){
                Glide.with(context).load(gonderi.getMedyaLinki()).into(gonderiMedya);
            }
        }

    }
}
