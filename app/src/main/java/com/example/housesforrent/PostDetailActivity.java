package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.housesforrent.ViewPagers.PostDetailImagesViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvTieuDe;
    TextView tvMoTa;
    TextView tvDienTich;
    TextView tvDiaChi;
    TextView tvGia;
    TextView tvUserDisplayName;
    TextView tvPhoneNumber;
    Button btnPhoneCall;
    ImageView ivUserAvatar;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ViewPager mViewPager;
    List<String> imageURLs = new ArrayList<>();
    PostDetailImagesViewPagerAdapter postDetailImagesViewPagerAdapter;

    String ownerEmail;
    String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chi tiết phòng trọ");

        tvTieuDe = findViewById(R.id.tvTieuDe);
        tvMoTa = findViewById(R.id.tvMoTa);
        tvDienTich = findViewById(R.id.tvDienTich);
        tvDiaChi = findViewById(R.id.tvDiaChi);
        tvGia = findViewById(R.id.tvGia);
        tvUserDisplayName = findViewById(R.id.tvUserDisplayName);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        btnPhoneCall = findViewById(R.id.btnCall);

        btnPhoneCall.setOnClickListener(this);

        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        ownerEmail = intent.getStringExtra("owner");

        postDetailImagesViewPagerAdapter = new PostDetailImagesViewPagerAdapter(PostDetailActivity.this, imageURLs);
        mViewPager = findViewById(R.id.vpViewPager);
        mViewPager.setAdapter(postDetailImagesViewPagerAdapter);

        loadPostContent();
        loadOwnerInfo();
    }

    private void loadPostContent() {
        db.collection("posts").document(postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d("TAG1", "DocumentSnapshot data: " + document.getData());

                        tvTieuDe.setText(document.getString("tieude"));
                        tvMoTa.setText(document.getString("mota"));
                        tvDiaChi.setText(document.getString("quan") + ", " + document.getString("thanhpho"));
                        tvDienTich.setText(document.getLong("dientich").toString() + "m2");
                        tvGia.setText(String.format("%,d", document.getLong("gia")) + " đồng/tháng");

                        List<Integer> imageList = (List<Integer>) document.get("images");

                        for (int i = 0; i < imageList.size(); i++) {
                            StorageReference imageRef = storageRef.child("post-resources/" + postID + "/" + i);

                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    postDetailImagesViewPagerAdapter.imageURLs.add(uri.toString());
                                    postDetailImagesViewPagerAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    } else {
                        Log.d("TAG2", "No such document");
                    }
                } else {
                    Log.d("TAG3", "get failed with ", task.getException());
                }
            }
        });

    }

    //load owner info
    private void loadOwnerInfo() {
        DocumentReference docRef = db.collection("users").document(ownerEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tvUserDisplayName.setText(document.getString("displayname"));
                        tvPhoneNumber.setText(document.getString("phone"));
                        Glide.with(PostDetailActivity.this)
                                .load(document.getString("pfp"))
                                .into(ivUserAvatar);
                    } else {

                    }
                } else {

                }
            }
        });
    }

    //################################### CREATE OPTION MENU ###########################################################3
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String ownerEmail = getIntent().getStringExtra("owner");
        if (user.getEmail().equals(ownerEmail)) {
            getMenuInflater().inflate(R.menu.menu_post_detail_activity, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //################################### OPTION MENU SELECTED #############################################################
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_post) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            removePost();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
            builder.setMessage("Bạn muốn xóa phòng trọ này?").setPositiveButton("Xóa", dialogClickListener)
                    .setNegativeButton("Không", dialogClickListener).show();

        }

        return super.onOptionsItemSelected(item);
    }


    private void removePost() {
        db.collection("posts").document(postID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PostDetailActivity.this, "Đã xóa phòng trọ", Toast.LENGTH_SHORT).show();
                        finish();
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCall) {
            String phoneNumber = tvPhoneNumber.getText().toString();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }
}