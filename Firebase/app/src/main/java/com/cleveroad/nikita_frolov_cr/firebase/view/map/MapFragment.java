package com.cleveroad.nikita_frolov_cr.firebase.view.map;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.provider.DataProvider;
import com.cleveroad.nikita_frolov_cr.firebase.util.InterfaceNotImplement;
import com.cleveroad.nikita_frolov_cr.firebase.util.LOG;
import com.cleveroad.nikita_frolov_cr.firebase.view.main.PhotoFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Photo>>, ClusterManager.OnClusterItemInfoWindowClickListener<Photo> {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private static final int LOADER_MANAGER_ID = 2;
    private static final long FLAG_ONLY_PREVIEW = -1;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private List<Photo> mPhotos;
    private ClusterManager<Photo> mClusterManager;
    private Photo mClickedClusterItem;
    private PhotoFragment.OnFragmentPhotoListener mListener;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PhotoFragment.OnFragmentPhotoListener) {
            mListener = (PhotoFragment.OnFragmentPhotoListener) context;
        } else {
            throw new InterfaceNotImplement(context.toString()
                    + " must implement " + PhotoFragment.OnFragmentPhotoListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        getActivity().setTitle("Map");
        setHasOptionsMenu(true);

        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);
        getLoaderManager().getLoader(LOADER_MANAGER_ID).forceLoad();

        mMapView = rootView.findViewById(R.id.mvMapPhoto);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            LOG.e(e);
        }

        mMapView.getMapAsync(mMap -> {
            mGoogleMap = mMap;


            mClusterManager = new ClusterManager<>(getContext(), mGoogleMap);
            mGoogleMap.setOnCameraIdleListener(mClusterManager);

            mGoogleMap.setOnMarkerClickListener(mClusterManager);

            mGoogleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            mGoogleMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);

            mClusterManager
                    .setOnClusterItemClickListener(photo -> {
                        mClickedClusterItem = photo;
                        return false;
                    });

            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                    new InfoWindowAdapterForPhotoItems(inflater));


            setMyLocationWrapper();

            for (Photo photo : mPhotos) {
                photo.setTitle("Title");
                photo.setSnippet("Snippet");
                mClusterManager.addItem(photo);
            }

            if (!mPhotos.isEmpty()) {
                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(mPhotos.get(mPhotos.size() - 1).getPosition()).zoom(5).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setMyLocationWrapper() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotosATLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
        mPhotos = data;
    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {
        // Do nothing
    }

    @Override
    public void onClusterItemInfoWindowClick(Photo photo) {
        mListener.goToPreviewFragment(photo, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStackImmediate();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

            Glide.with(getContext())
                    .load(mClickedClusterItem.getLink())
                    .placeholder(R.drawable.places_ic_clear)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            if (e != null) {
                                LOG.e(e);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            if(!isFromMemoryCache) marker.showInfoWindow();
                            return false;
                        }
                    })
                    .into(ivPhotoInfoWindow);

            TextView tvTitle = mContentsView.findViewById(R.id.tvTitle);
            tvTitle.setText(mClickedClusterItem.getTitle());

            TextView tvSnippet = mContentsView.findViewById(R.id.tvSnippet);
            tvSnippet.setText(mClickedClusterItem.getSnippet());

            return mContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private static class PhotosATLoader extends AsyncTaskLoader<List<Photo>> {

        PhotosATLoader(Context context) {
            super(context);
        }

        @Override
        public List<Photo> loadInBackground() {
            return DataProvider.getPhotoProvider().getAllPhotos();
        }
    }
}
