package com.cleveroad.nikita_frolov_cr.firebase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.nikita_frolov_cr.firebase.view.MapFragment;
import com.cleveroad.nikita_frolov_cr.firebase.view.PhotoFragment;
import com.cleveroad.nikita_frolov_cr.firebase.view.PhotoPreviewFragment;

import static com.cleveroad.nikita_frolov_cr.firebase.view.PhotoFragment.OnFragmentPhotoListener;

public class MainActivity extends AppCompatActivity implements OnFragmentPhotoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        deleteDatabase("photoDB");

        if(savedInstanceState == null){
            goToFragment(PhotoFragment.newInstance(),
                    PhotoFragment.class.getSimpleName());
        }
    }

    @Override
    public void goToPreviewFragment(String path) {
        goToFragment(PhotoPreviewFragment.newInstance(path),
                PhotoPreviewFragment.class.getSimpleName());
    }

    @Override
    public void goToPreviewFragment(String path, long id) {
        goToFragment(PhotoPreviewFragment.newInstance(path, id),
                PhotoPreviewFragment.class.getSimpleName());
    }

    @Override
    public void goToMapFragment() {
        goToFragment(MapFragment.newInstance(),
                MapFragment.class.getSimpleName());
    }

    private void goToFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.llContainer, fragment)
                .addToBackStack(tag)
                .commit();
    }
}
