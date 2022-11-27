package com.example.housesforrent.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.housesforrent.MyUtilities.DownloadImageFromInternet;
import com.example.housesforrent.PostDetailActivity;
import com.example.housesforrent.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public List<Post> postList;
    Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
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
//        holder.ivThumbnail.setImageResource(R.mipmap.ic_launcher);

        Glide.with(holder.ivThumbnail.getContext()).load(post.getThumnailURL()).into(holder.ivThumbnail);

//        new MainActivity.DownloadImageFromInternet(holder.ivImage).execute(nhaTro.getUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("postID", post.getID());
                intent.putExtras(bundle);
                context.startActivity(intent);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvDienTich = itemView.findViewById(R.id.tvDienTich);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }
    }
}
