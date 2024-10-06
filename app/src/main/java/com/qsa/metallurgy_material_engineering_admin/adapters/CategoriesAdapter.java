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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qsa.metallurgy_material_engineering_admin.Model.Category;
import com.qsa.metallurgy_material_engineering_admin.R;
import com.qsa.metallurgy_material_engineering_admin.activities.EditCategory;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.myViewHolder> {

    private Context mContext;
    private ArrayList<Category> categoriesList;
    private LayoutInflater layoutInflater;
    private DatabaseReference dbRefCategory;

    public CategoriesAdapter(Context mContext, ArrayList<Category> categoriesList) {
        this.mContext = mContext;
        this.categoriesList = categoriesList;
//        layoutInflater = LayoutInflater.from(mContext);
        dbRefCategory = FirebaseDatabase.getInstance().getReference().child("Category");

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
/*
        // is code sy app srash ho rhi thi
        View view = layoutInflater.inflate(R.layout.category_listitem, parent, false);
        return new myViewHolder(view);
*/

        View view = LayoutInflater.from(mContext).inflate(R.layout.category_listitem, parent, false);
        return new myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Category categoryModel = categoriesList.get(position);

        holder.bookCategoryCt.setText(categoryModel.getCategoryName());
        Glide.with(mContext).load(categoryModel.getCategoryImage()).fitCenter().centerCrop().into(holder.CTThumbnail);


        holder.editCTImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditCategory.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("categoryID_key", categoryModel.getId());
            intent.putExtra("categoryName_key", categoryModel.getCategoryName());
            intent.putExtra("categoryImageUrl_key", categoryModel.getCategoryImage());
            mContext.startActivity(intent);
        });

        holder.delCtImgBtn.setOnClickListener(Btn_view -> {
            showDialougeBox(categoryModel, holder.progressDialog, Btn_view);
        });

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private TextView bookCategoryCt;
        private ImageView editCTImgBtn, delCtImgBtn, CTThumbnail;
        private ProgressDialog progressDialog = new ProgressDialog(mContext);

        private String videoIDThumbnail;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            bookCategoryCt = itemView.findViewById(R.id.bookCategoryCt);
            editCTImgBtn = itemView.findViewById(R.id.editCtImgBtn);
            delCtImgBtn = itemView.findViewById(R.id.delCtBookImgBtn);
            CTThumbnail = itemView.findViewById(R.id.CtImageThumbnail);
        }
    }


    private void showDialougeBox(Category uploadCurrent, ProgressDialog pd_dialog, View Btn_view) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage("Do you want to delete this Category?");

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
                    dbRefCategory.child(uploadCurrent.getId()).removeValue();


                    /** delete child/img also from storage database */
                    StorageReference mPhotoStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uploadCurrent.getCategoryImage());
                    mPhotoStorageRef.delete();

                    Snackbar.make(Btn_view, "Category removed successfully", Snackbar.LENGTH_LONG).show();
                    pd_dialog.dismiss();
                }, 2000); // 3000 means 3 seconds

            }
        });
        alertDialog.show(); // to show taost
    }
}
