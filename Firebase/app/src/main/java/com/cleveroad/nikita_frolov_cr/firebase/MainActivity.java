package com.cleveroad.nikita_frolov_cr.firebase;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;
import com.cleveroad.nikita_frolov_cr.firebase.view.main.PhotoFragment;
import com.cleveroad.nikita_frolov_cr.firebase.view.map.MapFragment;
import com.cleveroad.nikita_frolov_cr.firebase.view.preview.PhotoPreviewFragment;

import static com.cleveroad.nikita_frolov_cr.firebase.view.main.PhotoFragment.OnFragmentPhotoListener;

public class MainActivity extends AppCompatActivity implements OnFragmentPhotoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null){
            goToFragment(PhotoFragment.newInstance());
        }
    }

    @Override
    public void goToPreviewFragment(Uri uri) {
        goToFragment(PhotoPreviewFragment.newInstance(uri));
    }

    @Override
    public void goToPreviewFragment(Photo photo, boolean isPreview) {
        goToFragment(PhotoPreviewFragment.newInstance(photo, isPreview));
    }

    @Override
    public void goToMapFragment() {
        goToFragment(MapFragment.newInstance());
    }

    private void goToFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.llContainer, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
