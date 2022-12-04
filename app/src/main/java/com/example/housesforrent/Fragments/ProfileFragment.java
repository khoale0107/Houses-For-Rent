package com.example.housesforrent.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.housesforrent.EditProfileActivity;
import com.example.housesforrent.LoginActivity;
import com.example.housesforrent.MyPostsActivity;
import com.example.housesforrent.R;
import com.example.housesforrent.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView tvDisplayName;
    TextView tvID;
    TextView tvEmail;
    TextView tvPhoneNumber;
    ImageView ivAvatar;
    Button btnLogout;
    TextView btnEditProfile;
    Button btnMyPosts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnMyPosts = view.findViewById(R.id.btnMyPosts);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            tvDisplayName.setText(User.getInstance().getDisplayName());
            tvEmail.setText(User.getInstance().getEmail());
            Glide.with(ProfileFragment.this).load(User.getInstance().getAvatarURL()).into(ivAvatar);
            tvPhoneNumber.setText(User.getInstance().getPhoneNumber());
        }

        btnLogout.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnMyPosts.setOnClickListener(this);

        return view;
    }


    //############################################ sign out ###############################
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogout) {
            AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        }
        else if (view.getId() == R.id.btnEditProfile) {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("displayName", tvDisplayName.getText().toString());
            bundle.putString("phoneNumber", tvPhoneNumber.getText().toString());
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }

        else if (view.getId() == R.id.btnMyPosts) {
            Intent intent = new Intent(getContext(), MyPostsActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String displayName = User.getInstance().getDisplayName();
                String phoneNumber = User.getInstance().getPhoneNumber();

                tvPhoneNumber.setText(phoneNumber);
                tvDisplayName.setText(displayName);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}