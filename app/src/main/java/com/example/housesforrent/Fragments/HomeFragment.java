package com.example.housesforrent.Fragments;

import android.net.Uri;
import android.os.Bundle;

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
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView rvRV;
    PostAdapter postAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvRV = view.findViewById(R.id.rvRV);
        postAdapter = new PostAdapter(new ArrayList<>(), getContext());
        rvRV.setAdapter(postAdapter);
        rvRV.setLayoutManager(new LinearLayoutManager(getContext()));

        //load content
        db.collection("posts")
                .orderBy("time", Query.Direction.ASCENDING)
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

                                    postAdapter.postList.add(0, post);
                                    postAdapter.notifyItemInserted(0);

                                    Log.d("", "@@@ " + dc.getDocument().getId());
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

        return view;
    }
}