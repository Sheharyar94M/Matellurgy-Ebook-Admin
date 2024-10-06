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
import com.qsa.metallurgy_material_engineering_admin.MainActivity;
import com.qsa.metallurgy_material_engineering_admin.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EditBook extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText titleEdit, authorNameEdit, pdfUrlEdit, youtubeUrlEdit, descriptionEdit;
    private ImageView bookImageEdit, adImageEdit;
    private Button submitEdit;
    private String titletEdit, categorytEdit, bookImagetEdit, authorNametEdit, descriptionEditT, pdfUrltEdit, adImagetEdit, youtubeUrltEdit, spinnerTextEdit, textEdit;

    private Bitmap bitmapEdit;
    private String encodedImageEdit;
    private Uri imageUriEdit;
    private String imageurlEdit = "";
    private ProgressDialog progressDialogEdit;

    private FirebaseDatabase firebaseDatabaseEdit;
    private DatabaseReference refEdit, refCatEdit;
    private StorageReference storageReferenceEdit;

    private ValueEventListener eventListenerEdit;
    private ArrayAdapter<String> adapterEdit;
    private ArrayList<String> spinnerDataListEdit = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner spinnerEditCategory;

    // update Data
    private String categoryIdStr, categoryNameStr, categoryImageUrl;
    private String bookIdStr, titleEditStr, categorytEditStrSpinner, authorNametEditStr, descriptionEditStr, pdfUrltEditStr, youtubeUrltEditStr, bookImagetEditStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        titleEdit = findViewById(R.id.titleEdit);
        spinnerEditCategory = (Spinner) findViewById(R.id.spinnerCategoryEdit);
        authorNameEdit = findViewById(R.id.authorNameEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        pdfUrlEdit = findViewById(R.id.pdfUrlEdit);
        youtubeUrlEdit = findViewById(R.id.pdfVideoUrlEdit);
//        bookImageEdit = findViewById(R.id.browseEdit);


        adImageEdit = findViewById(R.id.adImageEdit);
        submitEdit = findViewById(R.id.continueBtnEdit);


        /** My Code:  get data by put extra bundles from Books adapter **/
        bookIdStr = getIntent().getStringExtra("BookID_key");
        titleEditStr = getIntent().getStringExtra("bookName_key");
        categorytEditStrSpinner = getIntent().getStringExtra("categoryName_keyy");
        authorNametEditStr = getIntent().getStringExtra("authorName_key");
        descriptionEditStr = getIntent().getStringExtra("description_key");
        pdfUrltEditStr = getIntent().getStringExtra("pdfUrl_key");
        youtubeUrltEditStr = getIntent().getStringExtra("youtubeUrl_key");
        bookImagetEditStr = getIntent().getStringExtra("BookImageUrl_key");

        // now if the user enter into edit activity then the prevoious values should already added int edittexts and imageView
        titleEdit.setText(titleEditStr);
        authorNameEdit.setText(authorNametEditStr);
        descriptionEdit.setText(descriptionEditStr);
        pdfUrlEdit.setText(pdfUrltEditStr);
        youtubeUrlEdit.setText(youtubeUrltEditStr);
        Glide.with(getApplicationContext()).load(bookImagetEditStr).into(adImageEdit);

        /** my code end **/


        //firebase
        firebaseDatabaseEdit = FirebaseDatabase.getInstance();
        refEdit = firebaseDatabaseEdit.getReference("Books");
        refCatEdit = firebaseDatabaseEdit.getReference("Category");

        //firebase storage (storing pictures)
        storageReferenceEdit = FirebaseStorage.getInstance().getReference("uploads");


        //*******************************************For Spinner Initialization and Code Only*************************************************

        adapterEdit = new ArrayAdapter<String>(EditBook.this, android.R.layout.simple_spinner_dropdown_item, spinnerDataListEdit);
        spinnerEditCategory.setAdapter(adapterEdit);
        retrieveData();


        spinnerEditCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Month")) {

                } else {
                    textEdit = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //***************************************************___________*************************************************************


        //Image get for advertisingImage...
        adImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        submitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUpload();
            }
        });


    }


    //******************************Overriding Methods For Spinner Data*****************************
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerTextEdit = spinnerDataListEdit.get(position);
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
            imageUriEdit = data.getData();

            adImageEdit.setBackgroundResource(R.drawable.whitebackground);

            Glide.with(this).load(imageUriEdit).into(adImageEdit);
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
        progressDialogEdit = new ProgressDialog(this);
        progressDialogEdit.setMessage("Please Wait...");
        progressDialogEdit.show();

        if (imageUriEdit != null) {
            final StorageReference fileReference = storageReferenceEdit.child(System.currentTimeMillis() + "." + getFileExtension(imageUriEdit));

            fileReference.putFile(imageUriEdit)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageurlEdit = uri.toString();
                                    sendData();
                                    //clear();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialogEdit.dismiss();
                            Toast.makeText(EditBook.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialogEdit.show();
                        }
                    });

        } else {
            Toast.makeText(this, "No New Image", Toast.LENGTH_SHORT).show();
            progressDialogEdit.dismiss();
        }

//        if (pdfUrl.equals(""))
    }
    //......................................................................................

    public void sendData() {

//        String id = refEdit.push().getKey();
        String id = bookIdStr;
        String categoryValueEdit = textEdit;

        titletEdit = titleEdit.getText().toString();
        authorNametEdit = authorNameEdit.getText().toString();
        descriptionEditT = descriptionEdit.getText().toString();
        pdfUrltEdit = pdfUrlEdit.getText().toString();
        youtubeUrltEdit = youtubeUrlEdit.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("bookName", titletEdit);
        hashMap.put("categoryName", categoryValueEdit);
        hashMap.put("authorName", authorNametEdit);
        hashMap.put("description", descriptionEditT);
        hashMap.put("pdfUrl", pdfUrltEdit);
        hashMap.put("youtubeUrl", youtubeUrltEdit);
        hashMap.put("bookImage", imageurlEdit);
        hashMap.put("liked", "0");
        hashMap.put("readers", "0");
        hashMap.put("downloads", "0");
        //hashMap.put("isNotification", "false");
//        if (imageUriEdit.equals(bookImageEdit)) {
//            Toast.makeText(this, "Image shoud be change ", Toast.LENGTH_SHORT).show();
//        } else {
            refEdit.child(id).setValue(hashMap);
//        }

        Toast.makeText(EditBook.this, "Book Updated Successfully ", Toast.LENGTH_SHORT).show();
        progressDialogEdit.dismiss();
        eventOnBackpressed();
//        finish();
//        startActivity(new Intent(AddBook.this, AddBookNCategoryBySirRawal.class));

    }

    public void retrieveData() {
        eventListenerEdit = refCatEdit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String name = item.child("categoryName").getValue(String.class);
                    spinnerDataListEdit.add(name);
                }
                adapterEdit.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();
        eventOnBackpressed();
    }


    private void eventOnBackpressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}