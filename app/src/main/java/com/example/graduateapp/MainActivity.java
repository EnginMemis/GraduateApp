package com.example.graduateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText usernameText, passwordText;
    private Button loginButton, registerButton;
    private String username, password;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;
    private HashMap mData;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameText = (EditText)findViewById(R.id.inputUsername);
        passwordText = (EditText)findViewById(R.id.inputPassword);

        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnSuccessListener(MainActivity.this, new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    mUser = mAuth.getCurrentUser();

                                    arrayList = new ArrayList<>();
                                    docRef = mFirestore.collection("Kullanıcılar").document(mUser.getUid());
                                    docRef.get()
                                            .addOnSuccessListener(MainActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(documentSnapshot.exists()){
                                                        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                                                        intent.putExtra("uid", mUser.getUid());
                                                        startActivity(intent);
                                                    }
                                                }
                                            }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(MainActivity.this, "Username ve Şifre Boş Olamaz.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private ArrayList<String> verileriGetir(DocumentReference docRef, ArrayList<String> arrayList, String uid){

        return arrayList;
    }

    private void veriGuncelle(HashMap<String, Object> hashMap, final String uid){
        mFirestore.collection("Kullanıcılar").document(uid)
                .update(hashMap)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Veri Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}