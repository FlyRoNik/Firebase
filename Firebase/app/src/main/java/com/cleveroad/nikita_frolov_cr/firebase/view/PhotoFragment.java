package com.cleveroad.nikita_frolov_cr.firebase.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.nikita_frolov_cr.firebase.R;

public class PhotoFragment extends Fragment {
    private OnFragmentPhotoListener mListener;

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
        //TODO fix size photo
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentPhotoListener {
        void goToFragment();
    }
}
