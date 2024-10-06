package com.qsa.metallurgy_material_engineering_admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qsa.metallurgy_material_engineering_admin.databinding.ActivityMainBinding;
import com.qsa.metallurgy_material_engineering_admin.fragments.AllBooks_Frag;
import com.qsa.metallurgy_material_engineering_admin.fragments.AllCategories_Frag;

public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
//    private View view;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;
    private TabLayout tabLayout;



    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }  // this is constructor of above class SectionsPagerAdapter Q k class or adapter ka name same hota hai isi zrurt to nai thi but error deta tha is loye likha

        @Override
        public Fragment getItem(int position) {
            switch (position) {                                                // getItem is called to instantiate the fragment for the given page.// Return a PlaceholderFragment (defined as a static inner class below)
                case 0:
                    return new AllBooks_Frag();                             // switch me agr return lgaye to break nahi lgaty
                case 1:
                    return new AllCategories_Frag();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {  // y function short key ALT+Insert enter kr k liya ha Pages yani fragments ko title deny loye
            switch (position) {
                case 0:
                    return "BOOKS"; // ye titles han pages/fragments k
                case 1:
                    return "CATEGORIES";
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show/return 2 total pages.
            return 2;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*
        view = binding.getRoot();
        setContentView(view);
*/


        /***************** tab layout code *****************************/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

}