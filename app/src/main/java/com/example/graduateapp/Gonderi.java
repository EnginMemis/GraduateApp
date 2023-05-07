package com.example.graduateapp;

import android.graphics.Bitmap;

public class Gonderi {
    private String name, surname, icerik;
    private Bitmap medya;
    private String medyaLinki;

    public Gonderi(){

    }

    public Gonderi(String name, String surname, String icerik, Bitmap medya) {
        this.name = name;
        this.surname = surname;
        this.icerik = icerik;
        this.medya = medya;
    }

    public Gonderi(String name, String surname, String icerik, String medyaLinki) {
        this.name = name;
        this.surname = surname;
        this.icerik = icerik;
        this.medyaLinki = medyaLinki;
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

    public String getMedyaLinki() {
        return medyaLinki;
    }

    public void setMedyaLinki(String medyaLinki) {
        this.medyaLinki = medyaLinki;
    }
}
