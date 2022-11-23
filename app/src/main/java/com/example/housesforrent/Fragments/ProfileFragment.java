package com.example.housesforrent.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housesforrent.LoginActivity;
import com.example.housesforrent.MainActivity;
import com.example.housesforrent.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView tvDisplayName;
    TextView tvID;
    ImageView ivAvatar;
    Button btnLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvID = view.findViewById(R.id.tvID);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        btnLogout = view.findViewById(R.id.btnLogout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            ivAvatar.setImageURI(null);
            ivAvatar.setImageURI(user.getPhotoUrl());
            tvDisplayName.setText(user.getDisplayName());
//            tvID.setText(user.getUid());
            tvID.setText(user.getEmail());


        }

        btnLogout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogout) {
            AuthUI.getInstance()
                    .signOut(getContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
        }

        else if (1 == 1) {

        }
    }
}