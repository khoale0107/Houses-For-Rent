package com.example.housesforrent.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.housesforrent.MyUtilities.MyToast;
import com.example.housesforrent.PostDetailActivity;
import com.example.housesforrent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {
    public List<Post> postList;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final int STATUS_WAITING = 0;
    private final int STATUS_EMPTY = 1;
    private final int STATUS_RENTED = 2;


    public MyPostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_post, parent, false);
        return new MyPostAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.ViewHolder holder, int position) {
        Post post = postList.get(position);

        //load image
        Glide.with(holder.ivThumbnail.getContext()).load(post.getThumnailURL()).into(holder.ivThumbnail);

        holder.tvTieuDe.setText(post.getTieuDe());
        holder.tvDienTich.setText(post.getDienTich() + "m2");
        holder.tvGia.setText(post.getGia() + " đồng/tháng");
        holder.tvDiaChi.setText(post.getDiaChi());

        //set status
        if (post.getTrangThai() == STATUS_EMPTY) {
            holder.tvStatus.setText("Còn trống");
            holder.tvStatus.setTextColor(Color.parseColor("#469E42"));
            holder.btnCancelRent.setVisibility(View.GONE);
            holder.btnDeletePost.setVisibility(View.VISIBLE);
            holder.btnSetRented.setVisibility(View.VISIBLE);
        }
        else if (post.getTrangThai() == STATUS_RENTED) {
            holder.tvStatus.setText("Đã cho thuê");
            holder.tvStatus.setTextColor(Color.RED);
            holder.btnCancelRent.setVisibility(View.VISIBLE);
            holder.btnDeletePost.setVisibility(View.GONE);
            holder.btnSetRented.setVisibility(View.GONE);
        }
        else if (post.getTrangThai() == STATUS_WAITING) {
            holder.tvStatus.setText("Đang chờ duyệt");
            holder.tvStatus.setTextColor(Color.parseColor("#de7214"));
            holder.btnCancelRent.setVisibility(View.GONE);
            holder.btnDeletePost.setVisibility(View.GONE);
            holder.btnSetRented.setVisibility(View.GONE);
        }



        //to PostDetail Activity
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

        //delete post
        holder.btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                removePost(post.getID());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Bạn muốn xóa phòng trọ này?").setPositiveButton("Xóa", dialogClickListener)
                        .setNegativeButton("Không", dialogClickListener).show();
            }
        });

        //set status rented
        holder.btnSetRented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post.setTrangThai(2);
                notifyItemChanged(holder.getAdapterPosition());
                db.collection("posts").document(post.getID())
                        .update("trangthai", 2)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });

        //set status empty
        holder.btnCancelRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post.setTrangThai(1);
                notifyItemChanged(holder.getAdapterPosition());
                db.collection("posts").document(post.getID())
                        .update("trangthai", 1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });

    }

    //########################################## Xóa phòng trọ ##############################################
    private void removePost(String postID) {
        db.collection("posts").document(postID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Đã xóa phòng trọ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
        Button btnDeletePost;
        Button btnCancelRent;
        Button btnSetRented;
        TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvDienTich = itemView.findViewById(R.id.tvDienTich);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnBookMark = itemView.findViewById(R.id.btnBookMark);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
            btnCancelRent = itemView.findViewById(R.id.btnCancelRent);
            btnSetRented = itemView.findViewById(R.id.btnSetRented);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
