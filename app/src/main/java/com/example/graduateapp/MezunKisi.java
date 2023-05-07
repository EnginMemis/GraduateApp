package com.example.graduateapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.util.ArrayList;

public class MezunKisi {
    private String name;
    private String surname;
    private String uid;
    private Bitmap pp;
    private String medyaLink;

    public MezunKisi(){

    }

    public MezunKisi(String name, String surname, Bitmap pp, String uid) {
        this.name = name;
        this.surname = surname;
        this.pp = pp;
        this.uid = uid;
    }

    /*public MezunKisi(String name, String surname, String uid) {
        this.name = name;
        this.surname = surname;
        this.uid = uid;
    }*/

    public MezunKisi(String name, String surname, String medyaLink) {
        this.name = name;
        this.surname = surname;
        this.medyaLink = medyaLink;
    }

    public MezunKisi(String name, String surname, String medyaLink, String uid) {
        this.name = name;
        this.surname = surname;
        this.medyaLink = medyaLink;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Bitmap getPp() {
        return pp;
    }

    public void setPp(Bitmap pp) {
        this.pp = pp;
    }

    public String getMedyaLink() {
        return medyaLink;
    }

    public void setMedyaLink(String medyaLink) {
        this.medyaLink = medyaLink;
    }
}
