package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.housesforrent.Adapters.PostImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    Button btnSelectImages;
    ImageView ivImage;
    RecyclerView rvRV;
    PostImageAdapter postImageAdapter;
    EditText etTieuDe;
    EditText etGia;
    EditText etMota;
    EditText etDiaChi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cho thuê trọ");

        btnSelectImages = findViewById(R.id.btnSelectImages);
        rvRV = findViewById(R.id.rvRV);
        etTieuDe = findViewById(R.id.etTieuDe);
        etGia = findViewById(R.id.etGia);
        etMota = findViewById(R.id.etMota);
        etDiaChi = findViewById(R.id.etDiaChi);

        List<Uri> imageList = new ArrayList<>();
        postImageAdapter = new PostImageAdapter(new ArrayList<Uri>());
        rvRV.setAdapter(postImageAdapter);
        rvRV.setLayoutManager(new GridLayoutManager(this, 3));

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                pickFromGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                pickFromGallery.setType("image/*");
                startActivityForResult(pickFromGallery, 1);
            }
        });
    }

    //############################# OPTION MENU #####################################################################################################################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //############################# OPTION MENU SELECTED #################################################
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_new_post) {
            insertNewPost();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && data != null) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    postImageAdapter.imagesList.add(imageUri);
                }
                postImageAdapter.notifyDataSetChanged();
            }
            else {
                Uri imageUri = data.getData();
                postImageAdapter.imagesList.add(imageUri);
                postImageAdapter.notifyItemInserted(postImageAdapter.imagesList.size());
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void insertNewPost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        List<Integer> imageList = new ArrayList<>();
        for (int i = 0; i < postImageAdapter.imagesList.size(); i++) {
            imageList.add(i);
        }



        Map<String, Object> newUser = new HashMap<>();
        newUser.put("tieude", etTieuDe.getText().toString());
        newUser.put("gia", Integer.parseInt(etGia.getText().toString()));
        newUser.put("mota", etMota.getText().toString());
        newUser.put("diachi", etDiaChi.getText().toString());
        newUser.put("author", user.getEmail());
        newUser.put("images", imageList);
        newUser.put("time", Timestamp.now());

        String postId;
        db.collection("posts")
                .add(newUser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        for (int i = 0; i < postImageAdapter.imagesList.size(); i++) {
                            StorageReference imageRef = storageRef.child("post-resources/" + documentReference.getId() + "/" + i);
                            imageRef.putFile(postImageAdapter.imagesList.get(i));
                        }

                        finish();
                        Toast.makeText(PostActivity.this, "Đã thêm phòng trọ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("", "Error adding document", e);
                    }
                });


    }

    private void uploadImagesToPost() {


    }

}