package com.qsa.metallurgy_material_engineering_admin.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qsa.metallurgy_material_engineering_admin.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AddBook extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText title, authorName, pdfUrl, youtubeUrl, description;
    private ImageView bookImage, adImage;
    private Button submit;
    private String titlet, categoryt, bookImaget, authorNamet, pdfUrlt, adImaget, youtubeUrlt, spinnerText, text;

    private Bitmap bitmap;
    private String encodedImage;
    private Uri imageUri;
    private String imageurl = "";
    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref, refCat;
    private StorageReference storageReference;

    private ValueEventListener eventListener;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> spinnerDataList = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        title = findViewById(R.id.title);
        authorName = findViewById(R.id.authorName);
        bookImage = findViewById(R.id.browse);
        pdfUrl = findViewById(R.id.pdfUrl);
        youtubeUrl = findViewById(R.id.pdfVideoUrl);
        description = findViewById(R.id.description);
        spinner = (Spinner) findViewById(R.id.spinnerCategory);


        adImage = findViewById(R.id.adImage);
        submit = findViewById(R.id.continueBtn);

        //firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Books");
        refCat = firebaseDatabase.getReference("Category");

        //firebase storage (storing pictures)
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        //*******************************************For Spinner Initialization and Code Only*************************************************

        adapter = new ArrayAdapter<String>(AddBook.this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        spinner.setAdapter(adapter);
        retrieveData();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Month")) {

                } else {
                    text = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //***************************************************___________*************************************************************


        //Image get for advertisingImage...
        adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUpload();
            }
        });


    }


    //******************************Overriding Methods For Spinner Data*****************************
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerText = spinnerDataList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

            adImage.setBackgroundResource(R.drawable.whitebackground);

            Glide.with(this).load(imageUri).into(adImage);
        }
    }
    //..............................................................................


    //.................Methods for File Upload to Firebase Storage..................
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void fileUpload() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageurl = uri.toString();
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
                            Toast.makeText(AddBook.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            progressDialog.dismiss();
        }

//        if (pdfUrl.equals(""))
    }
    //......................................................................................

    public void sendData() {

        String id = ref.push().getKey();
        String categoryValue = text;

        titlet = title.getText().toString();
        authorNamet = authorName.getText().toString();
        pdfUrlt = pdfUrl.getText().toString();
        youtubeUrlt = youtubeUrl.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("bookName", titlet);
        hashMap.put("categoryName", categoryValue);
        hashMap.put("authorName", authorNamet);
        hashMap.put("pdfUrl", pdfUrlt);
        hashMap.put("youtubeUrl", youtubeUrlt);
        hashMap.put("bookImage", imageurl);
        hashMap.put("description", description.getText().toString());
        hashMap.put("liked", "0");
        hashMap.put("readers", "0");
        hashMap.put("downloads", "0");
        //hashMap.put("isNotification", "false");
        ref.child(id).setValue(hashMap);

        Toast.makeText(AddBook.this, "successfully added book", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        finish();
//        startActivity(new Intent(AddBook.this, AddBookNCategoryBySirRawal.class));

    }

    public void retrieveData() {
        eventListener = refCat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String name = item.child("categoryName").getValue(String.class);
                    spinnerDataList.add(name);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}