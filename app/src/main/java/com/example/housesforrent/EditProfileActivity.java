package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {
    TextView etPhoneNumber;
    TextView etDisplayName;
    Button btnSave;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chỉnh sửa thông tin");

        etDisplayName = findViewById(R.id.etDisplayName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSave = findViewById(R.id.btnSave);

//        etDisplayName.setText(getIntent().getStringExtra("displayName"));
//        etPhoneNumber.setText(getIntent().getStringExtra("phoneNumber"));

        etPhoneNumber.setText(User.getInstance().getPhoneNumber());
        etDisplayName.setText(User.getInstance().getDisplayName());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("displayName", etDisplayName.getText().toString());
                returnIntent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    private void updateUserInfo() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String displayName = etDisplayName.getText().toString().trim();

        User.getInstance().setDisplayName(displayName);
        User.getInstance().setPhoneNumber(phoneNumber);

        db.collection("users").document(user.getEmail())
                .update("phone", phoneNumber, "displayname", displayName)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}