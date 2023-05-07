package com.example.graduateapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MezunDetaylıBilgi extends AppCompatActivity {

    private TextView education, country, city, company, mail, phone, name, surname;
    private ImageView pp;
    private String uid;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mezundetaylibilgi);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mFirestore = FirebaseFirestore.getInstance();

        name = (TextView)findViewById(R.id.detayliAd);
        surname = (TextView)findViewById(R.id.detayliSoyad);
        education = (TextView) findViewById(R.id.detayliEgitim);
        country = (TextView) findViewById(R.id.detayliCountry);
        city = (TextView) findViewById(R.id.detayliCity);
        company = (TextView) findViewById(R.id.detayliCompany);
        mail = (TextView) findViewById(R.id.detayliMail);
        phone = (TextView) findViewById(R.id.detayliPhone);
        pp = (ImageView) findViewById(R.id.detayliPp);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        storage = FirebaseStorage.getInstance();


        docRef = mFirestore.collection("Kullanıcılar").document(uid);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            name.setText(documentSnapshot.getData().get("KullaniciAdi").toString());
                            surname.setText(documentSnapshot.getData().get("KullaniciSoyadi").toString());
                            education.setText(documentSnapshot.getData().get("Eğitim").toString());
                            country.setText(documentSnapshot.getData().get("Ülke").toString());
                            city.setText(documentSnapshot.getData().get("Şehir").toString());
                            company.setText(documentSnapshot.getData().get("Şirket").toString());
                            phone.setText(documentSnapshot.getData().get("Telefon").toString());
                            mail.setText(documentSnapshot.getData().get("Mail").toString());
                            Glide.with(MezunDetaylıBilgi.this).load(documentSnapshot.getData().get("PpLink")).into(pp);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
