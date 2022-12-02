package com.example.housesforrent.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.housesforrent.MyUtilities.DownloadImageFromInternet;
import com.example.housesforrent.MyUtilities.MyToast;
import com.example.housesforrent.PostDetailActivity;
import com.example.housesforrent.R;
import com.example.housesforrent.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public List<Post> postList;
    public List<String> bookmarksList;
//    Context context;

    public PostAdapter(List<Post> postList, List<String> bookmarksList) {
        this.postList = postList;
//        this.context = context;
        this.bookmarksList = bookmarksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.tvTieuDe.setText(post.getTieuDe());
        holder.tvDienTich.setText(post.getDienTich() + "m2");
        holder.tvGia.setText(post.getGia() + " đồng/tháng");
        holder.tvDiaChi.setText(post.getDiaChi());

        //load image
        Glide.with(holder.ivThumbnail.getContext()).load(post.getThumnailURL()).into(holder.ivThumbnail);

        //to PostDetailActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, PostDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("postID", post.getID());
                bundle.putString("owner", post.getOwner());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        if (post.bookMarked)
            holder.btnBookMark.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
        else
            holder.btnBookMark.setImageResource(R.drawable.ic_outline_bookmark_add_24);
        //toggle bookmark
        holder.btnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference washingtonRef = db.collection("users").document(user.getEmail());

                if (post.bookMarked == false) {
                    ((ImageView) view).setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                    post.bookMarked = true;

                    //add new bookmark
                    washingtonRef.update("bookmarks", FieldValue.arrayUnion(post.getID()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                                    Toast.makeText(holder.itemView.getContext(), "Đã lưu phòng trọ", Toast.LENGTH_SHORT).show();
                                    MyToast.toast(holder.itemView.getContext(), "Đã thêm vào Bookmark");
                                }
                            });

                }
                else  {
                    ((ImageView) view).setImageResource(R.drawable.ic_outline_bookmark_add_24);
                    post.bookMarked = false;

                    //remove bookmark
                    washingtonRef.update("bookmarks", FieldValue.arrayRemove(post.getID()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    MyToast.toast(holder.itemView.getContext(), "Đã xóa khỏi Bookmark");
                                }
                            });
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTieuDe;
        TextView tvGia;
        TextView tvDienTich;
        TextView tvDiaChi;
        ImageView ivThumbnail;
        ImageView btnBookMark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvDienTich = itemView.findViewById(R.id.tvDienTich);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnBookMark = itemView.findViewById(R.id.btnBookMark);
        }
    }
}
