package com.example.graduateapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DuyuruEkleme extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_PERM_CODE = 103;

    private ActivityResultLauncher activityResultLauncher;

    private ImageView medya;
    private Uri imageUri;

    private EditText baslikText, IcerikText;
    private DatePickerDialog datePickerDialog;
    private Button dateButton, cameraButton, galleryButton, duyuruEkleButton;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DocumentReference docRef;

    private HashMap<String, String> duyuruHash;

    private ArrayList<HashMap<String, String>> duyuruList;
    private HashMap<String, Object> genelHash;

    private String uid;
    private String date;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_duyuruekleme);

        mFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        duyuruHash = new HashMap<>();
        genelHash = new HashMap<>();
        duyuruList = new ArrayList<>();

        initDatePicker();


        dateButton = (Button) findViewById(R.id.tarihButton);
        dateButton.setText(getTodayDate());
        medya = (ImageView)findViewById(R.id.gonderiMedyaEkle);
        cameraButton = (Button)findViewById(R.id.gonderiCameraButton);
        galleryButton = (Button)findViewById(R.id.gonderiGalleryButton);
        duyuruEkleButton = (Button)findViewById(R.id.gonderiEkle);
        baslikText = (EditText)findViewById(R.id.duyuruBaslikEkle);
        IcerikText = (EditText)findViewById(R.id.gonderiIcerikEkle);



        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    if(result.getData().getExtras() == null){
                        imageUri = result.getData().getData();
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            medya.setImageBitmap(bitmap);
                        }

                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        medya.setImageBitmap(bitmap);

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

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        docRef = mFirestore.collection("Kullanıcılar").document(uid);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            System.out.println("BURADAA");
                            if(documentSnapshot.getData().get("DuyuruLinkleri") != null)
                                duyuruList = (ArrayList<HashMap<String, String>>) documentSnapshot.getData().get("DuyuruLinkleri");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("BAŞARISIZ");
                    }
                });

        duyuruEkleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){

                    final ProgressDialog progressDialog = new ProgressDialog(DuyuruEkleme.this);
                    progressDialog.setTitle("Yükleniyor...");
                    progressDialog.show();

                    StorageReference ref = storageRef.child("duyuruResimleri/"+ baslikText.getText().toString());
                    ref.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(DuyuruEkleme.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                    ref.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    duyuruHash.put("MedyaLink", String.valueOf(uri));
                                                    duyuruHash.put("Başlık", baslikText.getText().toString());
                                                    duyuruHash.put("İçerik", IcerikText.getText().toString());
                                                    duyuruHash.put("SonGün", date);
                                                    duyuruList.add(duyuruHash);

                                                    genelHash.put("DuyuruLinkleri", duyuruList);

                                                    mFirestore.collection("Kullanıcılar").document(uid)
                                                            .update(genelHash)
                                                            .addOnCompleteListener(DuyuruEkleme.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(DuyuruEkleme.this, "Veri Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else{
                                                                        Toast.makeText(DuyuruEkleme.this, "Veri Güncellenemedi.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
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
                                    Toast.makeText(DuyuruEkleme.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(DuyuruEkleme.this, "Resim Gereklidir!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DuyuruEkleme.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DuyuruEkleme.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DuyuruEkleme.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DuyuruEkleme.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        date = year + "-" + month + "-" + day;
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        if(month == 1)
            return "JAN";
        else if(month == 2)
            return "FEB";
        else if(month == 3)
            return "MAR";
        else if(month == 4)
            return "APR";
        else if(month == 5)
            return "MAY";
        else if(month == 6)
            return "JUN";
        else if(month == 7)
            return "JUL";
        else if(month == 8)
            return "AUG";
        else if(month == 9)
            return "SEP";
        else if(month == 10)
            return "OCT";
        else if(month == 11)
            return "NOV";
        else if(month == 12)
            return "DEC";

        return "JAN";
    }
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}
