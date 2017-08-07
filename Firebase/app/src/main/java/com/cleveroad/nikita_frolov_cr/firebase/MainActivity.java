package com.cleveroad.nikita_frolov_cr.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.cleveroad.nikita_frolov_cr.firebase.view.PhotoFragment.OnFragmentPhotoListener;
import static com.cleveroad.nikita_frolov_cr.firebase.view.PhotoFragment.newInstance;

public class MainActivity extends AppCompatActivity implements OnFragmentPhotoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.llContainer, newInstance())
                    .commit();
        }
    }

    @Override
    public void goToFragment() {
        //TODO go to map
    }
}
