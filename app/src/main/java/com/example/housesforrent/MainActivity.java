package com.example.housesforrent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.housesforrent.Fragments.BookMarkFragment;
import com.example.housesforrent.Fragments.HomeFragment;
import com.example.housesforrent.Fragments.MessageFragment;
import com.example.housesforrent.Fragments.ProfileFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mBottomNavigationView;
    Boolean isHomeFragment;

    public final static int POST_ACTIVITY_REQUEST = 1;


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
                    Intent intent = new Intent(MainActivity.this, PostActivity.class);
                    startActivityForResult(intent, POST_ACTIVITY_REQUEST);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == 1000){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//
//            try {
//                task.getResult(ApiException.class);
//                navigateToSecondActivity();
//            } catch (ApiException e) {
//                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                Log.e("@@@", e.getStatus().getStatusMessage());
//            }
//        }
    }

//    void navigateToSecondActivity(){
//        finish();
//        Intent intent = new Intent(MainActivity.this,SecondActivity.class);
//        startActivity(intent);
//    }




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