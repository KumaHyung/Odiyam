package com.soapclient.place.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.soapclient.place.utilities.GlideApp

inline fun ImageView.loadDrawableFromFirebaseStorage(url: String, crossinline endCalledBlock: () -> Unit) {
    GlideApp.with(context)
        .load(FirebaseStorage.getInstance().getReference(url))
        .centerCrop()
        .priority(Priority.IMMEDIATE)
        .into(object: CustomTarget<Drawable>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                setImageDrawable(resource)
                endCalledBlock()
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
}

fun ImageView.loadImageFromFirebaseStorage(url: String) {
    GlideApp.with(context)
        .load(FirebaseStorage.getInstance().getReference(url))
        .centerCrop()
        .into(this)
}