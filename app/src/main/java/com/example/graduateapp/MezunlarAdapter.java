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

public class MezunlarAdapter extends RecyclerView.Adapter<MezunlarAdapter.MezunlarHolder> {

    private ArrayList<MezunKisi> mezunlar;
    private Context context;
    private OnItemClickListener listener;

    public MezunlarAdapter(ArrayList<MezunKisi> mezunlar, Context context) {
        this.mezunlar = mezunlar;
        this.context = context;
    }

    @NonNull
    @Override
    public MezunlarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_mezunlar, parent, false);
        return new MezunlarHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MezunlarHolder holder, int position) {
        MezunKisi mezunKisi = mezunlar.get(position);
        holder.setData(mezunKisi);
    }

    @Override
    public int getItemCount() {
        return mezunlar.size();
    }

    class MezunlarHolder extends RecyclerView.ViewHolder{

        TextView mezunIsmi, mezunSoyismi;
        ImageView mezunPp;

        public MezunlarHolder(@NonNull View itemView) {
            super(itemView);
            mezunIsmi = (TextView) itemView.findViewById(R.id.duyuruBaslik);
            mezunSoyismi = (TextView) itemView.findViewById(R.id.duyuruIcerik);
            mezunPp = (ImageView) itemView.findViewById(R.id.duyuruResim);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(mezunlar.get(position), position);
                    }
                }
            });
        }

        public void setData(MezunKisi mezunKisi){
            this.mezunIsmi.setText(mezunKisi.getName());
            this.mezunSoyismi.setText(mezunKisi.getSurname());
            //this.mezunPp.setImageBitmap(mezunKisi.getPp());
            if(mezunKisi.getMedyaLink() != null){
                Glide.with(context).load(mezunKisi.getMedyaLink()).into(mezunPp);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(MezunKisi mezunKisi, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
