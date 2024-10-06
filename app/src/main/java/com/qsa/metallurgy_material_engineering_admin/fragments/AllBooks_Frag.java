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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsa.metallurgy_material_engineering_admin.Model.postBook;
import com.qsa.metallurgy_material_engineering_admin.activities.AddBook;
import com.qsa.metallurgy_material_engineering_admin.adapters.BooksAdapter;
import com.qsa.metallurgy_material_engineering_admin.databinding.FragmentAllBooksBinding;

import java.util.ArrayList;
import java.util.Collections;


public class AllBooks_Frag extends Fragment {

    private View view;
    private FragmentAllBooksBinding bindingAllBksFragment;

    private ArrayList<postBook> postBookList = new ArrayList<>();
    private BooksAdapter adapter;
    private DatabaseReference dbRefbook, dbRefCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_all_books, container, false);

        bindingAllBksFragment = FragmentAllBooksBinding.inflate(getLayoutInflater());
        view = bindingAllBksFragment.getRoot();

        dbRefbook = FirebaseDatabase.getInstance().getReference().child("Books");
        dbRefCategory = FirebaseDatabase.getInstance().getReference().child("Category");

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            bindingAllBksFragment.booksRecylcer.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.videoRecyclerView.setHasFixedSize(true);
            adapter = new BooksAdapter(getContext(), postBookList);
            getUploaded_Book();

            bindingAllBksFragment.addBookBtn.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), AddBook.class));
            });


        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private void getUploaded_Book() {

        dbRefbook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postBookList.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        postBook upload = postSnapshot.getValue(postBook.class);
                        postBookList.add(upload);
                    }
                } else {
//                    Toast.makeText(Home.this, "No Post Exist!", Toast.LENGTH_SHORT).show();
                }

                adapter = new BooksAdapter(getContext(), postBookList);
                Collections.reverse(postBookList);
                bindingAllBksFragment.booksRecylcer.setAdapter(adapter);
//                adapter.notifyDataSetChanged();  // this line was written to remove refernce of database when ad once retrieved from database, otherwise it will be load again again


//                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // this line written to remove refernce of database when ad once retrieved from database, otherwise it will be load again again
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
            }
        });
    }
}