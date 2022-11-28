package com.example.housesforrent.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.example.housesforrent.MainActivity;
import com.example.housesforrent.MyUtilities.DownloadImageFromInternet;
import com.example.housesforrent.R;
import com.example.housesforrent.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView tvDisplayName;
    TextView tvID;
    TextView tvEmail;
    TextView tvPhoneNumber;
    ImageView ivAvatar;
    Button btnLogout;
    TextView btnEditProfile;
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
//            tvDisplayName.setText(User.getInstance().getDisplayName());
            tvDisplayName.setText(user.getDisplayName());
            tvEmail.setText(user.getEmail());
        }

        btnLogout.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);

        //get user info
        db.collection("users").document(user.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String phoneNumber = document.getString("phone");
                        if (phoneNumber == null)
                            tvPhoneNumber.setText("Chưa cập nhật");
                        else
                            tvPhoneNumber.setText(document.getString("phone"));

                        Glide.with(ProfileFragment.this).load(document.getString("pfp")).into(ivAvatar);
                    } else {
                    }
                } else {

                }
            }
        });

        return view;
    }


    //sign out
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
            startActivity(intent);
        }
    }
}