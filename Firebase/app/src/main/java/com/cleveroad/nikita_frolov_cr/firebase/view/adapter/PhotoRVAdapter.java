package com.cleveroad.nikita_frolov_cr.firebase.view.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoRVAdapter extends RecyclerView.Adapter<PhotoRVAdapter.PhotoViewHolder>{
    private List<Photo> mPhotos;

    public PhotoRVAdapter() {
        this.mPhotos = new ArrayList<>();
    }

    public List<Photo> getPhotos() {
        return new ArrayList<>(mPhotos);
    }

    public void setPhotos(List<Photo> photos) {
        mPhotos.clear();
        mPhotos.addAll(photos);
        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.photo_item, parent, false);

        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = mPhotos.get(position);
        holder.bindPhoto(photo);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public Photo getItemSelected(MenuItem item) {
        return mPhotos.get(item.getOrder());
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        void bindPhoto(Photo photo) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            ivPhoto.setImageBitmap(BitmapFactory.decodeFile(photo.getPhotoPath(), options));
        }
    }
}
