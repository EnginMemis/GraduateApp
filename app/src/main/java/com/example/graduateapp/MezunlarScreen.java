package com.example.graduateapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MezunlarScreen extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MezunlarAdapter adapter;
    private ArrayList<MezunKisi> arrayList;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String uid;

    private ArrayList<String> uidList;
    private Bitmap bitmap;

    private String filter, firebaseString, firebaseValue, ad, soyad;
    private EditText filterText;
    private RadioButton isimRadio, mezunYiliRadio;
    private Button filterButton;

    private  ArrayList<HashMap<String, String>> datas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mezunlarekrani);

        filterText = (EditText)findViewById(R.id.filterText);
        isimRadio = (RadioButton)findViewById(R.id.isimFilterRadio);
        mezunYiliRadio = (RadioButton)findViewById(R.id.mezunFilterRadio);
        filterButton = (Button)findViewById(R.id.mezunFilterButton);

        mFirestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        uidList = new ArrayList<>();
        datas = new ArrayList<>();

        filter = "";

        isimRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    filter = "isim";
                }
            }
        });


        mezunYiliRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    filter = "mezunYili";
                }
            }
        });

        datas.clear();
        arrayList.clear();
        mFirestore.collection("Kullanıcılar")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ad = document.getData().get("KullaniciAdi").toString();
                                soyad = document.getData().get("KullaniciSoyadi").toString();
                                String link = "";
                                if(document.getData().get("PpLink") != null){
                                    link = document.getData().get("PpLink").toString();
                                }

                                uid = document.getId();

                                HashMap<String, String> hash = new HashMap<>();
                                hash.put("Ad", ad);
                                hash.put("Soyad", soyad);
                                hash.put("PpLink", link);
                                hash.put("uid", uid);
                                datas.add(hash);
                            }
                            for(int i = 0; i < datas.size(); ++i){
                                storage = FirebaseStorage.getInstance();
                                storageRef = storage.getReference().child("images").child(datas.get(i).get("uid"));
                                MezunKisi mezun = new MezunKisi(datas.get(i).get("Ad").toString(), datas.get(i).get("Soyad"), datas.get(i).get("PpLink"), datas.get(i).get("uid"));

                                System.out.println("LINK");
                                System.out.println(mezun.getMedyaLink());

                                arrayList.add(mezun);

                            }
                            mRecyclerView = (RecyclerView) findViewById(R.id.mezunEkraniRecyclerView);
                            adapter = new MezunlarAdapter(arrayList, MezunlarScreen.this);
                            mRecyclerView.setAdapter(adapter);

                            adapter.setOnItemClickListener(new MezunlarAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(MezunKisi mezunKisi, int position) {
                                    callIntent(mezunKisi.getUid());
                                }
                            });

                            mRecyclerView.setHasFixedSize(true);
                            LinearLayoutManager manager = new LinearLayoutManager(MezunlarScreen.this, LinearLayoutManager.VERTICAL, false);
                            mRecyclerView.setLayoutManager(manager);
                        }
                    }
                });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datas.clear();
                arrayList.clear();
                if(!filter.equals("") && !filterText.getText().toString().equals("")){
                    if(filter.equals("isim")){
                        firebaseString = "KullaniciAdi";
                        firebaseValue = filterText.getText().toString();
                    }
                    else if(filter.equals("mezunYili")){
                        firebaseString = "MezunYili";
                        firebaseValue = filterText.getText().toString();
                    }

                    mFirestore.collection("Kullanıcılar").whereEqualTo(firebaseString, firebaseValue)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            ad = document.getData().get("KullaniciAdi").toString();
                                            soyad = document.getData().get("KullaniciSoyadi").toString();
                                            String link = "";
                                            if(document.getData().get("PpLink") != null){
                                                link = document.getData().get("PpLink").toString();
                                            }

                                            uid = document.getId();

                                            HashMap<String, String> hash = new HashMap<>();
                                            hash.put("Ad", ad);
                                            hash.put("Soyad", soyad);
                                            hash.put("uid", uid);
                                            hash.put("PpLink", link);
                                            datas.add(hash);
                                        }
                                        for(int i = 0; i < datas.size(); ++i){
                                            storage = FirebaseStorage.getInstance();
                                            storageRef = storage.getReference().child("images").child(datas.get(i).get("uid"));
                                            MezunKisi mezun = new MezunKisi(datas.get(i).get("Ad"), datas.get(i).get("Soyad"), datas.get(i).get("PpLink"), datas.get(i).get("uid"));
                                            arrayList.add(mezun);
                                        }
                                        mRecyclerView = (RecyclerView) findViewById(R.id.mezunEkraniRecyclerView);
                                        adapter = new MezunlarAdapter(arrayList, MezunlarScreen.this);
                                        mRecyclerView.setAdapter(adapter);

                                        adapter.setOnItemClickListener(new MezunlarAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(MezunKisi mezunKisi, int position) {
                                                callIntent(mezunKisi.getUid());
                                            }
                                        });

                                        mRecyclerView.setHasFixedSize(true);
                                        LinearLayoutManager manager = new LinearLayoutManager(MezunlarScreen.this, LinearLayoutManager.VERTICAL, false);
                                        mRecyclerView.setLayoutManager(manager);
                                    }
                                    else{
                                        if(task.isSuccessful()){
                                            mRecyclerView = (RecyclerView) findViewById(R.id.mezunEkraniRecyclerView);
                                            adapter = new MezunlarAdapter(arrayList, MezunlarScreen.this);
                                            mRecyclerView.setAdapter(adapter);

                                            mRecyclerView.setHasFixedSize(true);
                                            LinearLayoutManager manager = new LinearLayoutManager(MezunlarScreen.this, LinearLayoutManager.VERTICAL, false);
                                            mRecyclerView.setLayoutManager(manager);
                                        }
                                    }
                                }
                            });
                }
                else{
                    System.out.println("Başarılı5");
                    mFirestore.collection("Kullanıcılar")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    System.out.println("Başarılı");
                                    if(task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            ad = document.getData().get("KullaniciAdi").toString();
                                            soyad = document.getData().get("KullaniciSoyadi").toString();
                                            String link = "";
                                            if(document.getData().get("PpLink") != null){
                                                link = document.getData().get("PpLink").toString();
                                            }

                                            uid = document.getId();

                                            HashMap<String, String> hash = new HashMap<>();
                                            hash.put("Ad", ad);
                                            hash.put("Soyad", soyad);
                                            hash.put("uid", uid);
                                            hash.put("PpLink", link);

                                            datas.add(hash);

                                        }
                                        for(int i = 0; i < datas.size(); ++i){
                                            storage = FirebaseStorage.getInstance();
                                            storageRef = storage.getReference().child("images").child(datas.get(i).get("uid"));
                                            MezunKisi mezun = new MezunKisi(datas.get(i).get("Ad"), datas.get(i).get("Soyad"), datas.get(i).get("PpLink"), datas.get(i).get("uid"));
                                            arrayList.add(mezun);
                                        }
                                        mRecyclerView = (RecyclerView) findViewById(R.id.mezunEkraniRecyclerView);
                                        adapter = new MezunlarAdapter(arrayList, MezunlarScreen.this);
                                        mRecyclerView.setAdapter(adapter);

                                        adapter.setOnItemClickListener(new MezunlarAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(MezunKisi mezunKisi, int position) {
                                                callIntent(mezunKisi.getUid());
                                            }
                                        });

                                        mRecyclerView.setHasFixedSize(true);
                                        LinearLayoutManager manager = new LinearLayoutManager(MezunlarScreen.this, LinearLayoutManager.VERTICAL, false);
                                        mRecyclerView.setLayoutManager(manager);
                                    }
                                }
                            });
                }
            }
        });
    }

    public void callIntent(String uid){
        Intent intent = new Intent(getApplicationContext(), MezunDetaylıBilgi.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }
}
