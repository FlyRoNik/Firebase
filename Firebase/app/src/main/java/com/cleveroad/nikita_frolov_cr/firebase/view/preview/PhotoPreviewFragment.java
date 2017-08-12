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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
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
import com.cleveroad.nikita_frolov_cr.firebase.provider.PhotoProviderImpl;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;

public class PhotoPreviewFragment extends Fragment implements LocationListener, View.OnClickListener {
    private static final String IMAGE_URI_KEY = "imagePath";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String PHOTO_ID_KEY = "photoKey";
    private static final String NETWORK_REQUEST_OK = "200OK";
    private static final long FLAG_ONLY_PREVIEW = -1;
    private static final String ONLY_PREVIEW_KEY = "onlyPreview";
    private static final String PREVIEW_PHOTO_NAME = "PreviewPhoto";

    private enum TypeMethod {
        PROVIDER_ADD_PHOTO,
        PROVIDER_UPLOAD_PHOTO
    }

    private Button bUploadPhoto;

    private LocationManager mLocationManager;
    private boolean mLoadLocation;
    private Photo mPhoto;

    @Override
    public void onLocationChanged(Location location) {
        if (mLoadLocation) {
            if (!getArguments().containsKey(PHOTO_ID_KEY)) {
                mPhoto = new Photo();
                Uri photoUri = Uri.parse(getArguments().getString(IMAGE_URI_KEY));
                mPhoto.setPhotoUri(photoUri);
            } else {
                mPhoto = DataProvider.getPhotoProvider().getPhoto(getArguments().getLong(PHOTO_ID_KEY));
            }
            mPhoto.setLatitude(new LatLng(location.getLatitude(), location.getLongitude()));
            new ProviderAsyncTask(TypeMethod.PROVIDER_ADD_PHOTO).execute(mPhoto);
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

    public static PhotoPreviewFragment newInstance(Uri uri) {
        return newInstance(uri, 0);
    }

    public static PhotoPreviewFragment newInstance(Uri uri, long id) {
        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URI_KEY, uri.toString());
        if (id > 0) {
            args.putLong(PHOTO_ID_KEY, id);
        }else {
            if (id == FLAG_ONLY_PREVIEW) {
                args.putString(ONLY_PREVIEW_KEY, "onlyPreview");
            }
        }
        fragment.setArguments(args);
        return fragment;
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


        if (!getArguments().containsKey(ONLY_PREVIEW_KEY)) {
            mLocationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
            uploadLocation();
        } else {
            bUploadPhoto.setVisibility(View.GONE);
        }

        Uri imageUri = Uri.parse(getArguments().getString(IMAGE_URI_KEY));

        ImageView ivPreviewPhoto = view.findViewById(R.id.ivPreviewPhoto);

        Glide.with(getContext())
                .load(imageUri)
                .placeholder(R.drawable.places_ic_clear)
                .into(ivPreviewPhoto);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoadLocation = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!getArguments().containsKey(ONLY_PREVIEW_KEY)) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bUploadPhoto) {
            new ProviderAsyncTask(TypeMethod.PROVIDER_UPLOAD_PHOTO).execute(mPhoto);
            getFragmentManager().popBackStackImmediate();
        }
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

    private static class ProviderAsyncTask extends AsyncTask<Photo, Void, String> {
        private TypeMethod mTypeMethod;
        private PhotoProvider mPhotoProvider;

        ProviderAsyncTask(TypeMethod typeMethod) {
            mTypeMethod = typeMethod;
            mPhotoProvider = new PhotoProviderImpl();
        }

        @Override
        protected String doInBackground(Photo... photos) {
            try {
                switch (mTypeMethod) {
                    case PROVIDER_ADD_PHOTO:
                        mPhotoProvider.addPhoto(photos[0]);
                        break;
                    case PROVIDER_UPLOAD_PHOTO:
                        mPhotoProvider.uploadPhoto(photos[0]);
                        break;
                    default:
                }
            } catch (NetworkException | JSONException | IOException e) {
                return e.getMessage();
            }
            return NETWORK_REQUEST_OK;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals(NETWORK_REQUEST_OK)) {
                Toast.makeText(App.get(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
