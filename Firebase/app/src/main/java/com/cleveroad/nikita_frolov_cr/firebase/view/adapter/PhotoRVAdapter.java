package com.cleveroad.nikita_frolov_cr.firebase.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.util.ImageHelper;
import com.cleveroad.nikita_frolov_cr.firebase.view.OnAdapterClickListener;
import com.cleveroad.nikita_frolov_cr.firebase.view.PhotoFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PhotoRVAdapter extends RecyclerView.Adapter<PhotoRVAdapter.PhotoViewHolder> implements
        PhotoFragment.OnItemClickListener {
    private List<Photo> mPhotos;
    private WeakReference<OnAdapterClickListener> mListenerReference;

    public PhotoRVAdapter(@NonNull OnAdapterClickListener listener) {
        this.mPhotos = new ArrayList<>();
        mListenerReference = new WeakReference<>(listener);
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

        return new PhotoViewHolder(v, this);
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

    @Override
    public void onClick(int position) {
        if (mListenerReference != null && mListenerReference.get() != null) {
            Photo photo = mPhotos.get(position);
            mListenerReference.get().onClick(photo.getPhotoPath(), photo.getId());
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivPhoto;
        private PhotoFragment.OnItemClickListener mListener;

        PhotoViewHolder(View itemView, PhotoFragment.OnItemClickListener listener) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        void bindPhoto(Photo photo) {
            ivPhoto.setImageBitmap(ImageHelper.getBitMapFromPath(photo.getPhotoPath()));
            if(TextUtils.isEmpty(photo.getLink())){
                ivPhoto.setAlpha(0.5f);
            }else {
                ivPhoto.setAlpha(1f);
            }
        }

        @Override
        public void onClick(View view) {
            if (mListener != null && ivPhoto.getAlpha() != 1f) {
                mListener.onClick(getAdapterPosition());
            }
        }
    }

}
