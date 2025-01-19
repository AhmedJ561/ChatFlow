package com.example.chatflow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatflow.R;

public class WallpaperPagerAdapter extends RecyclerView.Adapter<WallpaperPagerAdapter.ViewHolder> {

    private final Context context;
    private final int[] wallpaperResIds;

    public WallpaperPagerAdapter(Context context, int[] wallpaperResIds) {
        this.context = context;
        this.wallpaperResIds = wallpaperResIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(wallpaperResIds[position]);
    }

    @Override
    public int getItemCount() {
        return wallpaperResIds.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_wallpaper);
        }
    }
}
