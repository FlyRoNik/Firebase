package com.cleveroad.nikita_frolov_cr.firebase.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.cleveroad.nikita_frolov_cr.firebase.App;
import com.cleveroad.nikita_frolov_cr.firebase.R;
import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.repository.PhotoProvider;
import com.cleveroad.nikita_frolov_cr.firebase.repository.firebase.DataProvider;
import com.cleveroad.nikita_frolov_cr.firebase.repository.firebase.PhotoProviderImpl;
import com.cleveroad.nikita_frolov_cr.firebase.util.ImageHelper;
import com.cleveroad.nikita_frolov_cr.firebase.util.NetworkException;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;

public class PhotoPreviewFragment extends Fragment implements LocationListener, View.OnClickListener {
    public static final String IMAGE_PATH_KEY = "imagePath";
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String PHOTO_ID_KEY = "photoKey";
    public static final String NETWORK_REQUEST_OK = "200OK";
    private static final long FLAG_ONLY_PREVIEW = -1;
    public static final String ONLY_PREVIEW_KEY = "onlyPreview";

    private enum TypeMethod {
        PROVIDER_ADD_PHOTO,
        PROVIDER_UPLOAD_PHOTO;
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
                String imagePath = getArguments().getString(IMAGE_PATH_KEY);
                mPhoto.setPhotoPath(imagePath);
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

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public static PhotoPreviewFragment newInstance(String path) {
        return newInstance(path, 0);
    }

    public static PhotoPreviewFragment newInstance(String path, long id) {
        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH_KEY, path);
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
        getActivity().setTitle("PreviewPhoto");
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

        String imagePath = getArguments().getString(IMAGE_PATH_KEY);
        ((ImageView) view.findViewById(R.id.ivPreviewPhoto)).setImageBitmap(ImageHelper.getBitMapFromPath(imagePath));

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
        switch (view.getId()) {
            case R.id.bUploadPhoto:
                new ProviderAsyncTask(TypeMethod.PROVIDER_UPLOAD_PHOTO).execute(mPhoto);
                getFragmentManager().popBackStackImmediate();
                break;
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                return true;
            default:
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
                Toast.makeText(App.get(), s, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
