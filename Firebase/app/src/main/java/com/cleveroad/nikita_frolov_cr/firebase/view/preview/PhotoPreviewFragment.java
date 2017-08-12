package com.cleveroad.nikita_frolov_cr.firebase.view.preview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cleveroad.nikita_frolov_cr.firebase.App;
import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.provider.DataProvider;
import com.cleveroad.nikita_frolov_cr.firebase.provider.PhotoProvider;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;

public class PhotoPreviewFragment extends Fragment implements LocationListener, View.OnClickListener {
    private static final String IMAGE_URI_KEY = "imagePath";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String NETWORK_REQUEST_OK = "200OK";
    private static final String ONLY_PREVIEW_KEY = "onlyPreview";
    private static final String PREVIEW_PHOTO_NAME = "PreviewPhoto";
    private static final String PHOTO_KEY = "photo";
    private static final String NETWORK_REQUEST_KEY = "networkRequestKey";
    private static final String PHOTO_ID_KEY = "photoId";

    private enum TypeMethod {
        PROVIDER_ADD_PHOTO,
        PROVIDER_UPLOAD_PHOTO,
        PROVIDER_DELETE_PHOTO,
    }

    private Button bUploadPhoto;

    private LocationManager mLocationManager;
    private boolean mLoadLocation;
    private Photo mPhoto;

    @Override
    public void onLocationChanged(Location location) {
        if (mLoadLocation) {
            if (!getArguments().containsKey(PHOTO_KEY)) {
                mPhoto = new Photo();
                mPhoto.setPhotoUri(Uri.parse(getArguments().getString(IMAGE_URI_KEY)));
            } else {
                mPhoto = getArguments().getParcelable(PHOTO_KEY);
            }
            mPhoto.setLatitude(new LatLng(location.getLatitude(), location.getLongitude()));
            Bundle bundle = new Bundle();
            bundle.putParcelable(PHOTO_KEY, mPhoto);
            new ProviderAsyncTask(TypeMethod.PROVIDER_ADD_PHOTO).execute(bundle);
            mLoadLocation = false;
            bUploadPhoto.setEnabled(true);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String s) {
        // Do nothing
    }

    @Override
    public void onProviderDisabled(String s) {
        // Do nothing
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPhoto != null) {
            outState.putParcelable(PHOTO_KEY, mPhoto);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(PHOTO_KEY)) {
            mPhoto = savedInstanceState.getParcelable(PHOTO_KEY);
        }
    }

    public static PhotoPreviewFragment newInstance(Uri uri) {
        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URI_KEY, uri.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public static PhotoPreviewFragment newInstance(Photo photo, boolean isPreView) {
        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        Bundle args = new Bundle();
        args.putParcelable(PHOTO_KEY, photo);
        args.putBoolean(ONLY_PREVIEW_KEY, isPreView);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoadLocation = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        getActivity().setTitle(PREVIEW_PHOTO_NAME);
        setHasOptionsMenu(true);

        bUploadPhoto = view.findViewById(R.id.bUploadPhoto);
        bUploadPhoto.setOnClickListener(this);
        bUploadPhoto.setEnabled(false);

        ImageView ivPreviewPhoto = view.findViewById(R.id.ivPreviewPhoto);

        if (getArguments().getBoolean(ONLY_PREVIEW_KEY)) {
            bUploadPhoto.setVisibility(View.GONE);
        } else {
            mLocationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
            uploadLocation();
        }

        if (getArguments().getBoolean(ONLY_PREVIEW_KEY)) {
            Photo photo = getArguments().getParcelable(PHOTO_KEY);
            if (photo != null) {
                Glide.with(getContext())
                        .load(photo.getLink())
                        .placeholder(R.drawable.places_ic_clear)
                        .into(ivPreviewPhoto);
            }
        } else {
            Uri imageUri;
            if (getArguments().containsKey(IMAGE_URI_KEY)) {
                imageUri = Uri.parse(getArguments().getString(IMAGE_URI_KEY));
            } else {
                Photo photo = getArguments().getParcelable(PHOTO_KEY);
                if (photo != null) {
                    imageUri = photo.getPhotoUri();
                } else {
                    return view;
                }
            }
            Glide.with(getContext())
                    .load(imageUri)
                    .placeholder(R.drawable.places_ic_clear)
                    .into(ivPreviewPhoto);
        }

        return view;
    }

    private void uploadLocation() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mLoadLocation = true;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!getArguments().getBoolean(ONLY_PREVIEW_KEY)) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bUploadPhoto) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PHOTO_KEY, mPhoto);
            new ProviderAsyncTask(TypeMethod.PROVIDER_UPLOAD_PHOTO).execute(bundle);
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments().containsKey(PHOTO_KEY)) {
            inflater.inflate(R.menu.menu_preview, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Bundle bundle = new Bundle();
                Photo photo = getArguments().getParcelable(PHOTO_KEY);
                if (photo != null) {
                    bundle.putLong(PHOTO_ID_KEY, photo.getId());
                }
                new ProviderAsyncTask(TypeMethod.PROVIDER_DELETE_PHOTO).execute(bundle);
                getFragmentManager().popBackStackImmediate();
                return true;
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ProviderAsyncTask extends AsyncTask<Bundle, Void, Bundle> {
        private TypeMethod mTypeMethod;
        private PhotoProvider mPhotoProvider;

        ProviderAsyncTask(TypeMethod typeMethod) {
            mTypeMethod = typeMethod;
            mPhotoProvider = DataProvider.getPhotoProvider();
        }

        @Override
        protected Bundle doInBackground(Bundle... bundles) {
            Bundle bundle = new Bundle();
            if (bundles[0] != null) {
                switch (mTypeMethod) {
                    case PROVIDER_ADD_PHOTO:
                        mPhotoProvider.addPhoto(bundles[0].getParcelable(PHOTO_KEY));
                        return Bundle.EMPTY;
                    case PROVIDER_UPLOAD_PHOTO:
                        try {
                            mPhotoProvider.uploadPhoto(bundles[0].getParcelable(PHOTO_KEY));
                            bundle.putString(NETWORK_REQUEST_KEY, NETWORK_REQUEST_OK);
                        } catch (NetworkException | JSONException | IOException e) {
                            bundle.putString(NETWORK_REQUEST_KEY, e.getMessage());
                        }
                        return bundle;
                    case PROVIDER_DELETE_PHOTO:
                        mPhotoProvider.removePhoto(bundles[0].getLong(PHOTO_ID_KEY));
                        return Bundle.EMPTY;
                    default:
                        return Bundle.EMPTY;
                }
            }
            return Bundle.EMPTY;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            if (bundle != Bundle.EMPTY) {
                switch (mTypeMethod) {
                    case PROVIDER_UPLOAD_PHOTO:
                        String request = bundle.getString(NETWORK_REQUEST_KEY);
                        if (request != null && !request.equals(NETWORK_REQUEST_OK)) {
                            Toast.makeText(App.get(), request, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
            }
        }
    }
}
