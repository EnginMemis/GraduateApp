package com.example.graduateapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.util.HashMap;

public class Profile extends AppCompatActivity {


    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_PERM_CODE = 103;

    private ActivityResultLauncher activityResultLauncher;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> myAdapter;

    private String education, country, city, company, mail, phone, uid, name, surname;
    private EditText countryText, cityText, companyText, mailText, phoneText, nameText, surnameText;
    private ImageView pp;
    private Button updateButton, sifreDegistirButton, cameraButton, galleryButton;
    private FirebaseFirestore mFirestore;
    private HashMap mData;
    private ArrayList<String> arrayList;
    private Uri imageUri;
    private FirebaseUser user;

    private RadioButton radioLisans, radioYuksek, radioDoktora;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DocumentReference docRef;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        mFirestore = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();

        Intent intent = getIntent();

        uid = intent.getStringExtra("uid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        pp = (ImageView) findViewById(R.id.detayliPp);

        nameText = (EditText)findViewById(R.id.nameText);
        surnameText = (EditText)findViewById(R.id.surnameText);
        countryText = (EditText) findViewById(R.id.detayliCountry);
        cityText = (EditText) findViewById(R.id.detayliCity);
        companyText = (EditText) findViewById(R.id.detayliCompany);
        mailText = (EditText) findViewById(R.id.detayliMail);
        phoneText = (EditText) findViewById(R.id.detayliPhone);
        updateButton = (Button) findViewById(R.id.profileGuncelleButton);
        sifreDegistirButton = (Button)findViewById(R.id.sifreDegistir);
        cameraButton = (Button) findViewById(R.id.profileCameraButton);
        galleryButton = (Button) findViewById(R.id.profileGalleryButton);

        radioLisans = (RadioButton) findViewById(R.id.lisansRadio);
        radioYuksek = (RadioButton) findViewById(R.id.yuksekLisansRadio);
        radioDoktora = (RadioButton) findViewById(R.id.doktoraRadio);

        mailText.setEnabled(false);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images").child(uid);

        storageRef.getBytes(2048 * 2048)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        pp.setImageBitmap(bitmap);
                    }
                });

        docRef = mFirestore.collection("Kullanıcılar").document(uid);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            name = documentSnapshot.getData().get("KullaniciAdi").toString();
                            surname = documentSnapshot.getData().get("KullaniciSoyadi").toString();
                            education = documentSnapshot.getData().get("Eğitim").toString();
                            country = documentSnapshot.getData().get("Ülke").toString();
                            city = documentSnapshot.getData().get("Şehir").toString();
                            company = documentSnapshot.getData().get("Şirket").toString();
                            phone = documentSnapshot.getData().get("Telefon").toString();
                            mail = documentSnapshot.getData().get("Mail").toString();

                            nameText.setText(name);
                            surnameText.setText(surname);
                            countryText.setText(country);
                            cityText.setText(city);
                            companyText.setText(company);
                            mailText.setText(mail);
                            phoneText.setText(phone);

                            if(education.equals("Lisans")){
                                radioLisans.setChecked(true);
                            }
                            else if(education.equals("Yüksek Lisans")){
                                radioYuksek.setChecked(true);
                            }
                            else if(education.equals("Doktora")){
                                radioDoktora.setChecked(true);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("BAŞARISIZ");
                    }
                });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageUri != null){

                    final ProgressDialog progressDialog = new ProgressDialog(Profile.this);
                    progressDialog.setTitle("Yükleniyor...");
                    progressDialog.show();

                    StorageReference ref = storage.getReference().child("images/"+ uid);
                    ref.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Profile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Profile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                mData = new HashMap();
                if(education.equals("Eğitim Bilgisi Giriniz")){
                    education = "";
                }
                mData.put("Eğitim", education);
                mData.put("KullaniciAdi", nameText.getText().toString());
                mData.put("KullaniciSoyadi", surnameText.getText().toString());
                mData.put("Ülke", countryText.getText().toString());
                mData.put("Şehir", cityText.getText().toString());
                mData.put("Şirket", companyText.getText().toString());
                mData.put("Telefon", phoneText.getText().toString());

                if(imageUri != null){
                    storageRef.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageRef.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    mData.put("PpLink", String.valueOf(uri));
                                                    updateData(mData, uid);
                                                }
                                            });
                                }
                            });
                }
                else{
                    updateData(mData, uid);
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

        sifreDegistirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SifreDegistirme.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        radioLisans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    education = "Lisans";
                }
            }
        });

        radioYuksek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    education = "Yüksek Lisans";
                }
            }
        });

        radioDoktora.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    education = "Doktora";
                }
            }
        });

    }

    private void updateData(HashMap<String, Object> hashMap, final String uid){
        mFirestore.collection("Kullanıcılar").document(uid)
                .update(hashMap)
                .addOnCompleteListener(Profile.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Profile.this, "Veri Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Profile.this, "Veri Güncellenemedi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
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
                Toast.makeText(Profile.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Profile.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Profile.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Profile.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
