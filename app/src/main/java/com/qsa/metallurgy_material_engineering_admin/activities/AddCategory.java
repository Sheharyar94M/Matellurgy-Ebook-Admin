package com.qsa.metallurgy_material_engineering_admin.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.qsa.metallurgy_material_engineering_admin.MainActivity;
import com.qsa.metallurgy_material_engineering_admin.R;

import java.util.HashMap;

public class AddCategory extends AppCompatActivity {

    private TextInputEditText titleCategory;
    private ImageView imageView;
    private Button addBtn;

    //firebase...
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference DB_ref;
    private StorageReference storageReference;
    private StorageTask uploadtask;

    private Uri imageUri;
    private String imageUrlStr = "";


    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        titleCategory = findViewById(R.id.titleCat);
        imageView = findViewById(R.id.catImage);
        addBtn = findViewById(R.id.addBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DB_ref = firebaseDatabase.getReference("Category");
        storageReference = FirebaseStorage.getInstance().getReference("Category");

        //Image get for advertisingImage...
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUpload();
            }
        });
    }

    //..................Methods for File Chooser.................
    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            imageView.setBackgroundResource(R.drawable.whitebackground);

            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);
        }
    }
    //..............................................................................
    //.................Methods for File Upload to Firebase Storage..................
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
//        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    public void fileUpload() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        if (imageUri != null) {
            final StorageReference fileReferencePath = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
           fileReferencePath.putFile(imageUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUrlStr = uri.toString();
                                    sendData();
                                    //clear();
                                }
                            });
                        }

                    })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                           Toast.makeText(AddCategory.this, "prank" + e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   })
                   .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.show();
                }
            });




        } else {
            Toast.makeText(this, "no Image", Toast.LENGTH_SHORT).show();
        }
    }
    //......................................................................................

    public void sendData() {

        String id = DB_ref.push().getKey();

        String titleCat = titleCategory.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("categoryName", titleCat);
        hashMap.put("categoryImage", imageUrlStr);
        DB_ref.child(id).setValue(hashMap);

        Toast.makeText(AddCategory.this, "Successfully added category", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        finish();
//        eventOnBackpressed();
//        startActivity(new Intent(AddCategory.this, AddBookNCategoryBySirRawal.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void eventOnBackpressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}