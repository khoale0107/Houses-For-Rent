package com.example.housesforrent.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.housesforrent.MainActivity;
import com.example.housesforrent.R;

import java.util.List;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {
    public List<Uri> imagesList;
//    Context context;

    public PostImageAdapter(List<Uri> imagesList) {
        this.imagesList = imagesList;
//        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri currentItem = imagesList.get(position);
        holder.ivImageView.setImageURI(currentItem);

        holder.btnRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, String.valueOf(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                imagesList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImageView;
        TextView btnRemoveImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImageView = itemView.findViewById(R.id.ivImage);
            btnRemoveImage = itemView.findViewById(R.id.btnRemoveImage);
        }

    }

}
