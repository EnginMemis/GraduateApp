package com.example.graduateapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.jar.JarInputStream;

public class Register extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_PERM_CODE = 103;

    private ActivityResultLauncher activityResultLauncher;
    private ImageView pp;
    private Uri imageUri;
    private Button ppCamera, ppGallery, registerButton;

    private EditText mailText, passwordText, nameText, surnameText, entranceYearText, graduateYearText;
    private String mail, password, name, surname, entrance, graduate, country, city, company, phone, education;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ArrayList<HashMap<String, String>> duyuruList;

    private HashMap mData;
    private HashMap<String, String> duyuruHash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pp = (ImageView) findViewById(R.id.ppImage);
        ppCamera = (Button) findViewById(R.id.ppCameraButton);
        ppGallery = (Button) findViewById(R.id.ppGalleryButton);
        registerButton = (Button) findViewById(R.id.registerScreenButton);
        mailText = (EditText) findViewById(R.id.registerMail);
        passwordText = (EditText) findViewById(R.id.registerPassword);
        nameText = (EditText)findViewById(R.id.registerName);
        surnameText = (EditText)findViewById(R.id.registerSurname);
        entranceYearText = (EditText)findViewById(R.id.entranceYear);
        graduateYearText = (EditText)findViewById(R.id.graduatedYear);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        duyuruList = new ArrayList<>();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    if(result.getData().getExtras() == null){
                        imageUri = result.getData().getData();
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            pp.setImageBitmap(bitmap);
                        }

                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        pp.setImageBitmap(bitmap);

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                        imageUri = Uri.parse(path);
                    }
                }
            }
        });

        ppCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        ppGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askGalleryPermissions();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail = mailText.getText().toString();
                password = passwordText.getText().toString();
                name = nameText.getText().toString();
                surname = surnameText.getText().toString();
                entrance = entranceYearText.getText().toString();
                graduate = graduateYearText.getText().toString();
                country = "";
                city = "";
                company = "";
                phone = "";
                education = "";


                if (!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(password)){
                    mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                mUser = mAuth.getCurrentUser();

                                mData = new HashMap<>();
                                mData.put("KullaniciAdi", name);
                                mData.put("KullaniciSoyadi", surname);
                                mData.put("KullaniciSifre", password);
                                mData.put("GirisYili", entrance);
                                mData.put("MezunYili", graduate);
                                mData.put("Ülke", country);
                                mData.put("Şehir", city);
                                mData.put("Şirket", company);
                                mData.put("Telefon", phone);
                                mData.put("Eğitim", education);
                                mData.put("Mail", mail);
                                mData.put("DuyuruLinkleri", duyuruList);

                                if(imageUri != null){

                                    final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                                    progressDialog.setTitle("Yükleniyor...");
                                    progressDialog.show();

                                    StorageReference ref = storageReference.child("images/"+ mUser.getUid());
                                    ref.putFile(imageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Register.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Register.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                if(imageUri != null){
                                    StorageReference ref = storageReference.child("images/"+ mUser.getUid());
                                    ref.putFile(imageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    ref.getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    mData.put("PpLink", String.valueOf(uri));

                                                                    mFirestore.collection("Kullanıcılar").document(mUser.getUid())
                                                                            .set(mData)
                                                                            .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        Toast.makeText(Register.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                    else{
                                                                                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                                    startActivity(intent);
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                                else{
                                    mFirestore.collection("Kullanıcılar").document(mUser.getUid())
                                            .set(mData)
                                            .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(Register.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                }
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(Register.this, "Email ve Şifre Boş Olamaz.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                activityResultLauncher.launch(intent);
            }
            else{
                Toast.makeText(Register.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askGalleryPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if(intent.resolveActivity(getPackageManager()) != null){
                activityResultLauncher.launch(intent);
            }
            else{
                Toast.makeText(Register.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    activityResultLauncher.launch(intent);
                }
                else{
                    Toast.makeText(Register.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Camera Permission is Required to Use camera", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == GALLERY_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){
                    activityResultLauncher.launch(intent);
                }
                else{
                    Toast.makeText(Register.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
