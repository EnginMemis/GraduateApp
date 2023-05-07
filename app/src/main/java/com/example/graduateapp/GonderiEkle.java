package com.example.graduateapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GonderiEkle extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_PERM_CODE = 103;

    private ActivityResultLauncher activityResultLauncher;
    private ImageView medya;
    private Uri imageUri;
    private String uid;
    private Button cameraButton, galleryButton, gonderiEkleButton;

    private DocumentReference docRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore mFirestore;
    private HashMap<String, String> gonderiHash;
    private EditText icerikText;

    private ArrayList<HashMap<String, String>> gonderiList;
    private HashMap<String, Object> genelHash;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_gonderiekle);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mFirestore = FirebaseFirestore.getInstance();
        gonderiList = new ArrayList<>();
        gonderiHash = new HashMap<>();
        genelHash = new HashMap<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        cameraButton = (Button)findViewById(R.id.gonderiCameraButton);
        galleryButton = (Button)findViewById(R.id.gonderiGalleryButton);
        medya = (ImageView)findViewById(R.id.gonderiMedyaEkle);
        gonderiEkleButton = (Button)findViewById(R.id.gonderiEkle);
        icerikText = (EditText)findViewById(R.id.gonderiIcerikEkle);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    if(result.getData().getExtras() == null){
                        imageUri = result.getData().getData();
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(width / 10), (int)(width * 1.33 / 10), true);
                            medya.setImageBitmap(resized);
                        }

                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(width / 10), (int)(width * 1.33 / 10), true);
                        medya.setImageBitmap(resized);

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                        imageUri = Uri.parse(path);
                    }
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askGalleryPermissions();
            }
        });


        docRef = mFirestore.collection("Kullanıcılar").document(uid);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            System.out.println("BURADAA");
                            if(documentSnapshot.getData().get("GonderiLinkleri") != null)
                                gonderiList = (ArrayList<HashMap<String, String>>) documentSnapshot.getData().get("GonderiLinkleri");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("BAŞARISIZ");
                    }
                });

        gonderiEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){

                    final ProgressDialog progressDialog = new ProgressDialog(GonderiEkle.this);
                    progressDialog.setTitle("Yükleniyor...");
                    progressDialog.show();

                    Date date = new Date();
                    StorageReference ref = storageRef.child("gonderiResimleri/"+ date.toString());
                    ref.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(GonderiEkle.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                    ref.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    gonderiHash.put("MedyaLink", String.valueOf(uri));
                                                    gonderiHash.put("İçerik", icerikText.getText().toString());
                                                    gonderiList.add(gonderiHash);

                                                    genelHash.put("GonderiLinkleri", gonderiList);

                                                    mFirestore.collection("Kullanıcılar").document(uid)
                                                            .update(genelHash)
                                                            .addOnCompleteListener(GonderiEkle.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(GonderiEkle.this, "Veri Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(GonderiEkle.this, "Veri Güncellenemedi.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    Intent intent = new Intent(getApplicationContext(), GonderiScreen.class);
                                                                    intent.putExtra("uid", uid);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(GonderiEkle.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(GonderiEkle.this, "Resim Gereklidir!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(GonderiEkle.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askGalleryPermissions(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if(intent.resolveActivity(getPackageManager()) != null){
                activityResultLauncher.launch(intent);
            }
            else{
                Toast.makeText(GonderiEkle.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(GonderiEkle.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(GonderiEkle.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
