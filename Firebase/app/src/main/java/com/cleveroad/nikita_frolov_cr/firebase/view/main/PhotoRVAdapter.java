package com.cleveroad.nikita_frolov_cr.firebase.view.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class PhotoRVAdapter extends RecyclerView.Adapter<PhotoRVAdapter.PhotoViewHolder> implements
        PhotoFragment.OnItemClickListener {
    private static final long FLAG_ONLY_PREVIEW = -1;

    private List<Photo> mPhotos;
    private WeakReference<OnAdapterClickListener> mListenerReference;
    private Context context;

    PhotoRVAdapter(@NonNull OnAdapterClickListener listener, Context context) {
        this.mPhotos = new ArrayList<>();
        this.context = context;
        mListenerReference = new WeakReference<>(listener);
    }

    public List<Photo> getPhotos() {
        return new ArrayList<>(mPhotos);
    }

    void setPhotos(List<Photo> photos) {
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
        if (!TextUtils.isEmpty(photo.getLink())) {
            Glide.with(context)
                    .load(photo.getLink())
                    .placeholder(R.drawable.places_ic_clear)
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageDrawable(ContextCompat.
                    getDrawable(context, R.drawable.places_ic_clear));
        }
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
            if (TextUtils.isEmpty(photo.getLink())) {
                mListenerReference.get().onClick(photo.getPhotoUri(), photo.getId());
            } else {
                mListenerReference.get().onClick(photo.getPhotoUri(), FLAG_ONLY_PREVIEW);
            }
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPhoto;
        private PhotoFragment.OnItemClickListener mListener;

        PhotoViewHolder(View itemView, PhotoFragment.OnItemClickListener listener) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onClick(getAdapterPosition());
            }
        }
    }

}
