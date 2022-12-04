package com.example.housesforrent.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.housesforrent.Adapters.Post;
import com.example.housesforrent.Adapters.PostAdapter;
import com.example.housesforrent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView rvRV;
    PostAdapter postAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    List<String> bookmarksList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvRV = view.findViewById(R.id.rvRV);
        rvRV.setLayoutManager(new LinearLayoutManager(getContext()));


        //load bookmarks
        db.collection("users").document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            bookmarksList = (List<String>) document.get("bookmarks");

                            postAdapter = new PostAdapter(new ArrayList<>(), bookmarksList);
                            rvRV.setAdapter(postAdapter);

                            //load content
                            loadPosts();
                        } else {

                        }
                    } else {

                    }
                }
        });





        return view;
    }

    private void loadPosts() {
        //load content
        db.collection("posts")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {

                        //load posts
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            QueryDocumentSnapshot doc = dc.getDocument();
                            String postID = doc.getId();

                            switch (dc.getType()) {
                                case ADDED:
                                    if (Integer.parseInt(doc.get("trangthai").toString()) != 1) {
                                        break;
                                    }

                                    Post post = new Post(
                                            doc.getId(),
                                            doc.getString("tieude"),
                                            String.format("%,d", doc.getLong("gia")),
                                            doc.getLong("dientich").toString(),
                                            doc.getString("quan") + ", " + doc.getString("thanhpho"),
                                            doc.getString("owner")
                                    );

                                    if (bookmarksList != null) {
                                        post.setBookMarked(bookmarksList.contains(doc.getId()));
                                    }

                                    postAdapter.postList.add(0, post);
                                    postAdapter.notifyItemInserted(0);

                                    break;


                                case REMOVED:
                                    for (int i = 0; i < postAdapter.postList.size(); i++) {
                                        if (postAdapter.postList.get(i).getID().equals(postID)) {
                                            postAdapter.postList.remove(i);
                                            postAdapter.notifyItemRemoved(i);
                                        }
                                    }
                                    break;
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
}