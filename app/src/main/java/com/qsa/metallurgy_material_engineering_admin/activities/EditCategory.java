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

public class EditCategory extends AppCompatActivity {
    private TextInputEditText titleCategoryEdit;
    private ImageView imageViewEdit;
    private Button addBtnEdit;

    //firebase...
    private FirebaseDatabase firebaseDatabaseEdit;
    private DatabaseReference DB_refEdit;
    private StorageReference storageReferenceEdit;
    private StorageTask uploadtaskEdit;

    private Uri imageUriEdit;
    private String imageUrlStrEdit = "";

    private String categoryIdStr, categoryNameStr, categoryImageUrl;


    private ProgressDialog progressDialogEdit;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        titleCategoryEdit = findViewById(R.id.titleCatEdit);
        imageViewEdit = findViewById(R.id.catImageEdit);
        addBtnEdit = findViewById(R.id.addBtnEdit);

        firebaseDatabaseEdit = FirebaseDatabase.getInstance();
        DB_refEdit = firebaseDatabaseEdit.getReference("Category");
        storageReferenceEdit = FirebaseStorage.getInstance().getReference("Category");

        /** My Code:  get data by put extra bundles from Category adapter **/
        categoryIdStr = getIntent().getStringExtra("categoryID_key");
        categoryNameStr = getIntent().getStringExtra("categoryName_key");
        categoryImageUrl = getIntent().getStringExtra("categoryImageUrl_key");

        // now if the user enter into edit activity then the prevoious values should already added int edittexts and imageView
        titleCategoryEdit.setText(categoryNameStr);
        Glide.with(getApplicationContext()).load(categoryImageUrl).into(imageViewEdit);

        /** my code end **/


        //Image get for advertisingImage...
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        addBtnEdit.setOnClickListener(new View.OnClickListener() {
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
            imageUriEdit = data.getData();

            imageViewEdit.setBackgroundResource(R.drawable.whitebackground);

            Glide.with(this).load(imageUriEdit).into(imageViewEdit);
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
        progressDialogEdit = new ProgressDialog(this);
        progressDialogEdit.setMessage("Please Wait...");
        progressDialogEdit.show();


        if (imageUriEdit != null) {
            final StorageReference fileReferencePath = storageReferenceEdit.child(System.currentTimeMillis() + "." + getFileExtension(imageUriEdit));
            fileReferencePath.putFile(imageUriEdit)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUrlStrEdit = uri.toString();
                                    updateData();
                                    //clear();
                                }
                            });
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialogEdit.dismiss();
                            Toast.makeText(EditCategory.this, "prank" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialogEdit.show();
                        }
                    });


        } else {
            // agr user koi image select na kry to
            Toast.makeText(this, "No New Image", Toast.LENGTH_SHORT).show();
        }
    }
    //......................................................................................

    public void updateData() {

        String id = categoryIdStr;

        String titleCat = titleCategoryEdit.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("categoryName", titleCat);
        hashMap.put("categoryImage", imageUrlStrEdit);
        DB_refEdit.child(id).setValue(hashMap);


        Toast.makeText(EditCategory.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
        progressDialogEdit.dismiss();
        eventOnBackpressed();

//        finish();
//        startActivity(new Intent(EditCategory.this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eventOnBackpressed();
    }


    private void eventOnBackpressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}