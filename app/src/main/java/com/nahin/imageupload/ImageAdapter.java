package com.nahin.imageupload;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter {

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {androidx.constraintlayout.widget.ConstraintLayout
            super( itemView );
        }
    }
}
