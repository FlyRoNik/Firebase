package com.cleveroad.nikita_frolov_cr.firebase.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.firebase.DataProvider;
import com.cleveroad.nikita_frolov_cr.firebase.util.ImageHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.stream.IntStream;

public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener,
        LoaderManager.LoaderCallbacks<List<Photo>>{
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private static final int LOADER_MANAGER_ID = 2;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private List<Photo> mPhotos;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);
        getLoaderManager().getLoader(LOADER_MANAGER_ID).forceLoad();

        mMapView = rootView.findViewById(R.id.mvMapPhoto);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            mGoogleMap = mMap;

            mGoogleMap.setOnInfoWindowClickListener(MapFragment.this);
            mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapterForPhotoItems(inflater));

            setMyLocationWrapper();

            for (Photo photo :
                    mPhotos) {
                mGoogleMap.addMarker(new MarkerOptions().position(photo.getLatitude())).setTag(photo.getId());
            }
        });

        return rootView;
    }

    private void setMyLocationWrapper() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotosATLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
        mPhotos = data;
    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {

    }

    private class InfoWindowAdapterForPhotoItems implements GoogleMap.InfoWindowAdapter {

        private final View mContentsView;

        InfoWindowAdapterForPhotoItems(LayoutInflater layoutInflater) {
            mContentsView = layoutInflater.inflate(
                    R.layout.photo_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            ImageView ivPhotoInfoWindow = mContentsView.findViewById(R.id.ivPhotoInfoWindow);

            int index = IntStream.range(0, mPhotos.size())
                    .filter(i -> mPhotos.get(i).getId().equals(marker.getTag()))
                    .findFirst().orElse(-1);
            Photo photo = mPhotos.get(index);

            ivPhotoInfoWindow.setImageBitmap(ImageHelper.getBitMapFromPath(photo.getPhotoPath()));

            return mContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private static class PhotosATLoader extends AsyncTaskLoader<List<Photo>> {

        public PhotosATLoader(Context context) {
            super(context);
        }

        @Override
        public List<Photo> loadInBackground() {
            return DataProvider.getPhotoProvider().getAllPhotos();
        }
    }
}
