package com.example.graduateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SifreDegistirme extends AppCompatActivity {

    private EditText mail, currentPassword, newPassword;
    private Button sifreButton;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifredegistir);

        mail = (EditText)findViewById(R.id.sifreDegistirMail);
        currentPassword = (EditText)findViewById(R.id.currentPass);
        newPassword = (EditText)findViewById(R.id.newPass);
        sifreButton = (Button)findViewById(R.id.sifreDegistirButton);


        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        sifreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mail.getText().toString().equals(user.getEmail())){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mail.getText().toString(), currentPassword.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SifreDegistirme.this, "Şifre Başarıyla Değiştirildi.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(SifreDegistirme.this, "Şifre Değiştirilemedi.", Toast.LENGTH_SHORT).show();
                                                }
                                                Intent intent = new Intent(getApplicationContext(), Profile.class);
                                                intent.putExtra("uid", uid);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });

    }
}
