package com.qsa.metallurgy_material_engineering_admin.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qsa.metallurgy_material_engineering_admin.Model.postBook;
import com.qsa.metallurgy_material_engineering_admin.R;
import com.qsa.metallurgy_material_engineering_admin.activities.EditBook;

import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.myViewHolder> {


    private Context mContext;
    private ArrayList<postBook> bookList;
    private LayoutInflater layoutInflater;
    private DatabaseReference dbRefBooks, dbRefCategory;

    public BooksAdapter(Context mContext, ArrayList<postBook> bookList) {
        this.mContext = mContext;
        this.bookList = bookList;
        dbRefBooks = FirebaseDatabase.getInstance().getReference().child("Books");
//        layoutInflater = LayoutInflater.from(mContext);
//        dbRefCategory = FirebaseDatabase.getInstance().getReference().child("Category");
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
/*
        // is code sy app srash ho rhi thi
        View view = layoutInflater.inflate(R.layout.book_lisitem, parent, false);
        return new myViewHolder(view);
*/

        View view = LayoutInflater.from(mContext).inflate(R.layout.book_lisitem, parent, false);
        return new myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        postBook bookModel = bookList.get(position);

        holder.bookTitle.setText(bookModel.getBookName());
        holder.bookCategory.setText(bookModel.getCategoryName());
        Glide.with(mContext).load(bookModel.getBookImage()).fitCenter().centerCrop().into(holder.BkImageThumbnail);

        holder.editBookImgBtn.setOnClickListener(v -> {
//            Toast.makeText(mContext, "Working Process", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, EditBook.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("BookID_key", bookModel.getId());
            intent.putExtra("bookName_key", bookModel.getBookName());
            intent.putExtra("categoryName_keyy", bookModel.getCategoryName());
            intent.putExtra("authorName_key", bookModel.getAuthorName());
            intent.putExtra("description_key", bookModel.getDescription());
            intent.putExtra("pdfUrl_key", bookModel.getPdfUrl());
            intent.putExtra("youtubeUrl_key", bookModel.getYoutubeUrl());
            intent.putExtra("BookImageUrl_key", bookModel.getBookImage());
            mContext.startActivity(intent);

        });

        holder.delBookImgBtn.setOnClickListener(Btn_view -> {
            showDialougeBox(bookModel, holder.progressDialog, Btn_view);
        });


    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {


        private TextView bookTitle, bookCategory;
        private ImageView editBookImgBtn, delBookImgBtn, BkImageThumbnail;
        private ProgressDialog progressDialog = new ProgressDialog(mContext);
        private CardView bookItemLayout;
        private String videoIDThumbnail;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            bookItemLayout = itemView.findViewById(R.id.bookItemLayout);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookCategory = itemView.findViewById(R.id.bookCategory);
            editBookImgBtn = itemView.findViewById(R.id.editBookImgBtn);
            delBookImgBtn = itemView.findViewById(R.id.delBookImgBtn);
            BkImageThumbnail = itemView.findViewById(R.id.BkImageThumbnail);

        }
    }


    private void showDialougeBox(postBook uploadCurrent, ProgressDialog pd_dialog, View Btn_view) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage("Do you want to delete this Book?");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() { // BUTTON_NEUTRAL mean on the lef side
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                pd_dialog.setMessage("Deleting ... ");
                pd_dialog.show();
                new android.os.Handler().postDelayed(() -> {
                    // delete child form databse,  // .getPostId()).setValue(null) to del aal database
                    dbRefBooks.child(uploadCurrent.getId()).removeValue();


                    /** delete child/img also from storage database */
                    StorageReference mPhotoStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uploadCurrent.getBookImage());
                    mPhotoStorageRef.delete();

                    Snackbar.make(Btn_view, "Book removed successfully", Snackbar.LENGTH_LONG).show();
                    pd_dialog.dismiss();
                }, 2000); // 3000 means 3 seconds

            }
        });
        alertDialog.show(); // to show taost
    }
}
