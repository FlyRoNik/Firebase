package com.cleveroad.nikita_frolov_cr.firebase.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;
import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.data.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoProvider;
import com.cleveroad.nikita_frolov_cr.firebase.repository.firebase.PhotoProviderImpl;
import com.cleveroad.nikita_frolov_cr.firebase.view.adapter.PhotoRVAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<Photo>>{
    private static final int PROVIDER_ADD_PHOTO = 1;

    private static final int CAMERA_RESULT = 1;

    private static final int LOADER_MANAGER_ID = 1;

    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final Uri PHOTO_UPDATE_URI = Uri.parse("content://"
            + BuildConfig.APPLICATION_ID + ".photoDB/photo");

    private RecyclerView rvPhotos;
    private PhotoRVAdapter mPhotoRVAdapter;
    private OnFragmentPhotoListener mListener;
    private PhotoProvider mPhotoProvider;
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().getLoader(LOADER_MANAGER_ID).forceLoad();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    };

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentPhotoListener) {
            mListener = (OnFragmentPhotoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentPhotoListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        view.findViewById(R.id.bAddPhoto).setOnClickListener(this);

        rvPhotos = view.findViewById(R.id.rvPhotos);
        mPhotoRVAdapter = new PhotoRVAdapter();
        rvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvPhotos.setAdapter(mPhotoRVAdapter);

        mPhotoProvider = new PhotoProviderImpl(getContext().getContentResolver());
        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);
        getActivity().getContentResolver()
                .registerContentObserver(PHOTO_UPDATE_URI, true, mContentObserver);
        getLoaderManager().getLoader(LOADER_MANAGER_ID).forceLoad();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getContentResolver()
                .unregisterContentObserver(mContentObserver);
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bAddPhoto:
                makePhotoWrapper();
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO mListener.goToFragment();
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            //TODO fix size photo
            Bitmap image = (Bitmap) data.getExtras().get("data");

            Photo photo = new Photo();
            Uri tempUri = getImageUri(getActivity().getApplicationContext(), image);
            String imagePath = getRealPathFromURI(tempUri);
            photo.setPhotoPath(imagePath);
            //TODO get location
            photo.setLatitude(new LatLng(2.3,4.4));
            new ProviderAsyncTask(PROVIDER_ADD_PHOTO, getActivity().getContentResolver()).execute(photo);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        String result = "";
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void makePhotoWrapper() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }
        makePhoto();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoto();
                } else {
                    Toast.makeText(getContext(), "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void makePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    @Override
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotosATLoader(getContext(), getActivity().getContentResolver());
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
        mPhotoRVAdapter.setPhotos(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {

    }

    private static class PhotosATLoader extends AsyncTaskLoader<List<Photo>> {
        private PhotoProvider mPhotoProvider;

        PhotosATLoader(Context context, ContentResolver contentResolver) {
            super(context);
            mPhotoProvider = new PhotoProviderImpl(contentResolver);
        }

        @Override
        public List<Photo> loadInBackground() {
            return mPhotoProvider.getAllPhotos();
        }
    }

    private static class ProviderAsyncTask extends AsyncTask<Photo, Void, Void> {
        private int mTypeCommand;
        private PhotoProvider mPhotoProvider;

        ProviderAsyncTask(int typeCommand, ContentResolver contentResolver) {
            mTypeCommand = typeCommand;
            mPhotoProvider = new PhotoProviderImpl(contentResolver);
        }

        @Override
        protected Void doInBackground(Photo... photos) {
            switch (mTypeCommand) {
                case PROVIDER_ADD_PHOTO:
                    mPhotoProvider.addPhoto(photos[0]);
                    break;
                default:
            }
            return null;
        }
    }

    public interface OnFragmentPhotoListener {
        void goToFragment();
    }
}
