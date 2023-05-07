package com.example.graduateapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainMenu extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DuyuruAdapter adapter;
    private ArrayList<Duyuru> arrayList;
    private Button mezunAraButton, profileButton, duyuruEkleButton, gonderiButton;
    private FirebaseFirestore mFirestore;

    private ArrayList<HashMap<String, String>> duyuruList;

    private String uid, baslik, icerik, link;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        mFirestore = FirebaseFirestore.getInstance();

        arrayList = new ArrayList<>();
        duyuruList = new ArrayList<>();

        mezunAraButton = (Button) findViewById(R.id.mezunAraButton);
        profileButton = (Button) findViewById(R.id.profileButton);
        duyuruEkleButton = (Button) findViewById(R.id.duyuruEkleButton);
        gonderiButton = (Button)findViewById(R.id.gonderiButton);

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        mFirestore.collection("Kullanıcılar")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println("Başarılı");
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                uid = document.getId();
                                if(document.get("DuyuruLinkleri") != null)
                                    duyuruList = (ArrayList<HashMap<String, String>>) document.get("DuyuruLinkleri");

                                for(int i = 0; i < duyuruList.size(); ++i){

                                    try {
                                        Date d1 = sdformat.parse(sdformat.format(today));
                                        Date d2 = sdformat.parse(duyuruList.get(i).get("SonGün"));

                                        if(d1.compareTo(d2) <= 0){
                                            arrayList.add(new Duyuru(duyuruList.get(i).get("Başlık"), duyuruList.get(i).get("İçerik"), duyuruList.get(i).get("MedyaLink")));
                                        }

                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            mRecyclerView = (RecyclerView) findViewById(R.id.duyuruRecycler);
                            adapter = new DuyuruAdapter(arrayList, MainMenu.this);
                            mRecyclerView.setAdapter(adapter);

                            mRecyclerView.setHasFixedSize(true);
                            LinearLayoutManager manager = new LinearLayoutManager(MainMenu.this, LinearLayoutManager.VERTICAL, false);
                            mRecyclerView.setLayoutManager(manager);
                        }
                        else{
                            System.out.println("Başarısız");
                        }
                    }
                });


        mezunAraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MezunlarScreen.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        duyuruEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DuyuruEkleme.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        gonderiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GonderiScreen.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
    }
}
