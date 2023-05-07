package com.example.graduateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GonderiScreen extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private ArrayList<HashMap<String, String>> gonderiList;
    private ArrayList<Gonderi> arrayList;
    private GonderiAdapter adapter;
    private Button gonderiEkleButton;
    private String name, surname;
    private String uid;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonderiekrani);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        gonderiList = new ArrayList<>();
        arrayList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();

        gonderiEkleButton = (Button)findViewById(R.id.gonderiEkleButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.gonderiRecyclerView);

        mFirestore.collection("Kullanıcılar")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                name = document.get("KullaniciAdi").toString();
                                surname = document.get("KullaniciSoyadi").toString();

                                System.out.println(name);
                                System.out.println(surname);

                                uid = document.getId();
                                if(document.get("GonderiLinkleri") != null){
                                    gonderiList = (ArrayList<HashMap<String, String>>) document.get("GonderiLinkleri");

                                    for(int i = 0; i < gonderiList.size(); ++i){
                                        arrayList.add(new Gonderi(name, surname, gonderiList.get(i).get("İçerik"), gonderiList.get(i).get("MedyaLink")));
                                    }
                                }
                            }

                            adapter = new GonderiAdapter(arrayList, GonderiScreen.this);
                            mRecyclerView.setAdapter(adapter);

                            mRecyclerView.setHasFixedSize(true);
                            LinearLayoutManager manager = new LinearLayoutManager(GonderiScreen.this, LinearLayoutManager.VERTICAL, false);
                            mRecyclerView.setLayoutManager(manager);

                        }
                        else{
                            System.out.println("Başarısız");
                        }
                    }
                });
        gonderiEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GonderiEkle.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

    }
}
