package com.cleveroad.nikita_frolov_cr.firebase.view.main;


import com.cleveroad.nikita_frolov_cr.firebase.model.Photo;

interface OnAdapterClickListener {
    void onClick(Photo photo, boolean isPreview);
}
