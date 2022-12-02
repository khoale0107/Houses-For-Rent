package com.example.housesforrent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.housesforrent.Adapters.MyPostAdapter;
import com.example.housesforrent.Adapters.Post;
import com.example.housesforrent.Adapters.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    RecyclerView rvRV;
    MyPostAdapter postAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        getSupportActionBar().setTitle("Phòng trọ của tôi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvRV = findViewById(R.id.rvRV);
        postAdapter = new MyPostAdapter(new ArrayList<>(), this);
        rvRV.setAdapter(postAdapter);
        rvRV.setLayoutManager(new LinearLayoutManager(this));

        //load content
        db.collection("posts")
                .whereEqualTo("owner", user.getEmail())
//                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        //load posts
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    QueryDocumentSnapshot doc = dc.getDocument();

                                    Post post = new Post(
                                            doc.getId(),
                                            doc.getString("tieude"),
                                            String.format("%,d", doc.getLong("gia")),
                                            doc.getLong("dientich").toString(),
                                            doc.getString("quan") + ", " + doc.getString("thanhpho"),
                                            doc.getString("owner")
                                    );

                                    post.setTrangThai(Integer.parseInt(doc.getLong("trangthai").toString()));

                                    postAdapter.postList.add(0, post);
                                    postAdapter.notifyItemInserted(0);

                                    Log.d("", "@@@ " + dc.getDocument().getId());
                                    break;

                                case REMOVED:
                                    String ID = dc.getDocument().getId();
                                    for (int i = 0; i < postAdapter.postList.size(); i++) {
                                        if (postAdapter.postList.get(i).getID().equals(ID)) {
                                            postAdapter.postList.remove(i);
                                            postAdapter.notifyItemRemoved(i);
                                        }
                                    }
                            }
                        }

                        //load Thumnail
                        for (Post p : postAdapter.postList) {
                            StorageReference imageRef = storageRef.child("post-resources/" + p.getID() + "/" + 0);
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    p.setThumnailURL(uri.toString());
                                    postAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}