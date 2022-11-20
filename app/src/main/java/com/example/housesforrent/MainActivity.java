package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.housesforrent.Fragments.BookMarkFragment;
import com.example.housesforrent.Fragments.HomeFragment;
import com.example.housesforrent.Fragments.MessageFragment;
import com.example.housesforrent.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mBottomNavigationView;
    Boolean isHomeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isHomeFragment = true;
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        mBottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.menu_home);


    }

    NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_home:
//                        mViewPager2.setCurrentItem(0);
                    loadFragment(new HomeFragment());
                    isHomeFragment = true;
                    break;
                case R.id.menu_bookmarks:
                    loadFragment(new BookMarkFragment());
                    break;
                case R.id.menu_post:
//                        loadFragment(new ProfileFragment());
                    break;
                case R.id.menu_message:
                    loadFragment(new MessageFragment());
                    break;
                case R.id.menu_user:
                    loadFragment(new ProfileFragment());
                    break;
            }
            return true;
        }
    };

    //############################# OPTION MENU #####################################################################################################################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //############################# OPTION MENU SELECTED #################################################
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
//            Toast.makeText(this, "menu add worked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();

        isHomeFragment = false;
    }


    @Override
    public void onBackPressed() {
        if (isHomeFragment)
            super.onBackPressed();
        mBottomNavigationView.setSelectedItemId(R.id.menu_home);
    }
}