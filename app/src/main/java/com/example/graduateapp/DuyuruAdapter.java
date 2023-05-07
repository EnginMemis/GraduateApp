package com.example.graduateapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DuyuruAdapter extends RecyclerView.Adapter <DuyuruAdapter.DuyuruHolder>{

    private ArrayList<Duyuru> duyurular;
    private Context context;


    public DuyuruAdapter(ArrayList<Duyuru> duyurular, Context context) {
        this.duyurular = duyurular;
        this.context = context;
    }

    @NonNull
    @Override
    public DuyuruHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_duyuru, parent, false);
        return new DuyuruAdapter.DuyuruHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DuyuruHolder holder, int position) {
        Duyuru duyuru = duyurular.get(position);
        holder.setData(duyuru);
    }

    @Override
    public int getItemCount() {
        return duyurular.size();
    }

    class DuyuruHolder extends RecyclerView.ViewHolder{

        ImageView duyuruMedya;
        TextView baslik, icerik;

        public DuyuruHolder(@NonNull View itemView) {
            super(itemView);
            duyuruMedya = (ImageView) itemView.findViewById(R.id.duyuruResim);
            baslik = (TextView) itemView.findViewById(R.id.duyuruBaslik);
            icerik = (TextView) itemView.findViewById(R.id.duyuruIcerik);
        }

        public void setData(Duyuru duyuru){
            this.baslik.setText(duyuru.getBaslik());
            this.icerik.setText(duyuru.getIcerik());
            if(duyuru.getMedyaLink() != null){
                Glide.with(context).load(duyuru.getMedyaLink()).into(duyuruMedya);
            }
        }
    }
}
