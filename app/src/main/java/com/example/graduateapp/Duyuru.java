package com.example.graduateapp;

import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Date;

public class Duyuru{
    private String baslik, icerik;
    private Bitmap medya;
    private String medyaLink;
    private Date sonDate;

    public Duyuru(){

    }

    public Duyuru(String baslik, String icerik, Bitmap medya) {
        this.baslik = baslik;
        this.icerik = icerik;
        this.medya = medya;
    }

    public Duyuru(String baslik, String icerik) {
        this.baslik = baslik;
        this.icerik = icerik;
    }

    public Duyuru(String baslik, String icerik, String medyaLink) {
        this.baslik = baslik;
        this.icerik = icerik;
        this.medyaLink = medyaLink;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public Bitmap getMedya() {
        return medya;
    }

    public void setMedya(Bitmap medya) {
        this.medya = medya;
    }

    public String getMedyaLink() {
        return medyaLink;
    }

    public void setMedyaLink(String medyaLink) {
        this.medyaLink = medyaLink;
    }

    public Date getSonDate() {
        return sonDate;
    }

    public void setSonDate(Date sonDate) {
        this.sonDate = sonDate;
    }
}
