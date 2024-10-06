package com.qsa.metallurgy_material_engineering_admin.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsa.metallurgy_material_engineering_admin.Model.Category;
import com.qsa.metallurgy_material_engineering_admin.activities.AddCategory;
import com.qsa.metallurgy_material_engineering_admin.adapters.CategoriesAdapter;
import com.qsa.metallurgy_material_engineering_admin.databinding.FragmentAllCategoriesBinding;

import java.util.ArrayList;
import java.util.Collections;


public class AllCategories_Frag extends Fragment {

    private View view;
    private FragmentAllCategoriesBinding bindingAllCategoriesFragment;

    private ArrayList<Category> categoriesList = new ArrayList<>();
    private CategoriesAdapter adapter;
    private DatabaseReference dbRefCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_all_categories, container, false);

        bindingAllCategoriesFragment = FragmentAllCategoriesBinding.inflate(getLayoutInflater());
        view = bindingAllCategoriesFragment.getRoot();


        dbRefCategory = FirebaseDatabase.getInstance().getReference().child("Category");

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            bindingAllCategoriesFragment.categoryRecylcer.setLayoutManager(new GridLayoutManager(getContext(), 2));  // setting recyleviw in 2columns
//        binding.videoRecyclerView.setHasFixedSize(true);
            adapter = new CategoriesAdapter(getContext(), categoriesList);
            getUploaded_Category();

            bindingAllCategoriesFragment.addCategoryBtn.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), AddCategory.class));
            });

        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private void getUploaded_Category() {

        dbRefCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                categoriesList.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Category categoryModel = snapshot.getValue(Category.class);
                        categoriesList.add(categoryModel);

                    }
                }


                adapter = new CategoriesAdapter(getContext(), categoriesList);
                Collections.reverse(categoriesList);
                bindingAllCategoriesFragment.categoryRecylcer.setAdapter(adapter);
//                adapter.notifyDataSetChanged();   // this line was written to remove refernce of database when ad once retrieved from database, otherwise it will be load again again

                //                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // this line written to remove refernce of database when ad once retrieved from database, otherwise it will be load again again
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);

            }
        });

    }
}