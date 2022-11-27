package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housesforrent.Fragments.MessageFragment;
import com.example.housesforrent.MyUtilities.DownloadImageFromInternet;
import com.example.housesforrent.ViewPagers.PostDetailImagesViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    TextView tvTieuDe;
    TextView tvMoTa;
    TextView tvDienTich;
    TextView tvDiaChi;
    TextView tvGia;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ViewPager mViewPager;
    List<String> imageURLs = new ArrayList<>();
    PostDetailImagesViewPagerAdapter postDetailImagesViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTieuDe = findViewById(R.id.tvTieuDe);
        tvMoTa = findViewById(R.id.tvMoTa);
        tvDienTich = findViewById(R.id.tvDienTich);
        tvDiaChi = findViewById(R.id.tvDiaChi);
        tvGia = findViewById(R.id.tvGia);

        Intent intent = getIntent();
        String postID = intent.getStringExtra("postID");

        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.vpViewPager);

        // Initializing the ViewPagerAdapter
        postDetailImagesViewPagerAdapter = new PostDetailImagesViewPagerAdapter(PostDetailActivity.this, imageURLs);

        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(postDetailImagesViewPagerAdapter);

        //load content
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
                        tvGia.setText(document.getLong("gia").toString() + " đồng/tháng");

                        List<Integer> imageList = (List<Integer>) document.get("images");

                        for (int i = 0; i < imageList.size(); i++) {
                            StorageReference imageRef = storageRef.child("post-resources/" + postID + "/" + i);

                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    new DownloadImageFromInternet(ivImage).execute(uri.toString());
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

        //load images
//        StorageReference imageRef = storageRef.child("post-resources/" + postID + "/" + 0);
//        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                new DownloadImageFromInternet(ivImage).execute(uri.toString());
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//            }
//        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}