package com.cleveroad.nikita_frolov_cr.firebase.view.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.nikita_frolov_cr.firebase.App;
import com.cleveroad.nikita_frolov_cr.firebase.BuildConfig;
import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.provider.DataProvider;
import com.cleveroad.nikita_frolov_cr.firebase.util.ImageHelper;
import com.cleveroad.nikita_frolov_cr.firebase.util.InterfaceNotImplement;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo>>,
        OnAdapterClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;

    private static final int LOADER_MANAGER_ID = 1;

    private static final int COLUMN_COUNT = 3;

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final Uri PHOTO_UPDATE_URI = Uri.parse("content://"
            + BuildConfig.APPLICATION_ID + ".photoDB/photo");
    private static final String PHOTO_URI = "photoUri";

    private PhotoRVAdapter mPhotoRVAdapter;
    private OnFragmentPhotoListener mListener;
    private Uri mPhotoURI;
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getLoaderManager().getLoader(LOADER_MANAGER_ID).forceLoad();
        }
    };

    private Snackbar snackbar;

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
            throw new InterfaceNotImplement(context.toString()
                    + " must implement " + OnFragmentPhotoListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        view.findViewById(R.id.bAddPhoto).setOnClickListener(this);
        getActivity().setTitle("ListPhoto");

        snackbar = Snackbar.make(view.findViewById(R.id.clContainer), "", Snackbar.LENGTH_SHORT);

        setHasOptionsMenu(true);

        RecyclerView rvPhotos = view.findViewById(R.id.rvPhotos);
        mPhotoRVAdapter = new PhotoRVAdapter(this);
        rvPhotos.setLayoutManager(new GridLayoutManager(getContext(), COLUMN_COUNT));
        rvPhotos.setAdapter(mPhotoRVAdapter);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.iGoToMap) {
            mListener.goToMapFragment();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bAddPhoto) {
            makePhotoWrapper();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ImageHelper.decodeImageForDisplay(mPhotoURI.getPath(),
                    getActivity().getWindowManager().getDefaultDisplay());

            mListener.goToPreviewFragment(mPhotoURI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mPhotoURI != null){
            outState.putString(PHOTO_URI, mPhotoURI.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(PHOTO_URI)) {
            mPhotoURI = Uri.parse(savedInstanceState.getString(PHOTO_URI));
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(App.get().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageHelper.createImageFile();
            } catch (IOException ex) {
                snackbar.setText("ACTION_IMAGE_CAPTURE Denied").show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.cleveroad.nikita_frolov_cr.firebase.provider",
                        photoFile);
                mPhotoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void makePhotoWrapper() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MEDIA_CONTENT_CONTROL},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }
        dispatchTakePictureIntent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                snackbar.setText("CAMERA Denied").show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public Loader<List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotosATLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo>> loader, List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo> data) {
        mPhotoRVAdapter.setPhotos(data);
    }

    @Override
    public void onLoaderReset(Loader<List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo>> loader) {
        //Do nothing
    }

    @Override
    public void onClick(Uri imageUri, long id) {
        mListener.goToPreviewFragment(imageUri, id);
    }

    private static class PhotosATLoader extends AsyncTaskLoader<List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo>> {
        PhotosATLoader(Context context) {
            super(context);
        }

        @Override
        public List<com.cleveroad.nikita_frolov_cr.firebase.model.Photo> loadInBackground() {
            return DataProvider.getPhotoProvider().getAllPhotos();
        }
    }

    public interface OnFragmentPhotoListener {
        void goToPreviewFragment(Uri uri);

        void goToPreviewFragment(Uri uri, long id);

        void goToMapFragment();
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

}
