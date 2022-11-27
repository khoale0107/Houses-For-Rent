package com.example.housesforrent.ViewPagers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.housesforrent.MyUtilities.DownloadImageFromInternet;
import com.example.housesforrent.R;

import java.util.List;
import java.util.Objects;

public class PostDetailImagesViewPagerAdapter extends PagerAdapter {

        // Context object
        Context context;

        // Array of images
        public List<String> imageURLs;
//        int[] imageURLs;

        // Layout Inflater
        LayoutInflater mLayoutInflater;


        // Viewpager Constructor
        public PostDetailImagesViewPagerAdapter(Context context, List<String> images) {
            this.context = context;
            this.imageURLs = images;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // return the number of images
//            return imageURLs.length;
            return imageURLs.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            // inflating the item.xml
            View itemView = mLayoutInflater.inflate(R.layout.item_post_detail_image, container, false);

            // referencing the image view from the item.xml file
            ImageView imageView = (ImageView) itemView.findViewById(R.id.ivImage);

            // setting the image in the imageView
//            new DownloadImageFromInternet(imageView).execute(imageURLs.get(position));
            Glide.with(imageView.getContext()).load(imageURLs.get(position)).into(imageView);

//            imageView.setImageResource(imageURLs[position]);

            // Adding the View
            Objects.requireNonNull(container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((LinearLayout) object);
        }
    }
