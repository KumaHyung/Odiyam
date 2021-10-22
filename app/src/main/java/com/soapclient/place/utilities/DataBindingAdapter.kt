package com.soapclient.place.utilities

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.soapclient.place.R
import com.soapclient.place.ext.loadImageFromFirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("chipIconResId")
fun bindIconDrawable(view: Chip, resId: Int) {
    view.setChipIconResource(resId)
}

@BindingAdapter("textDistance")
fun bindTextDistance(view: TextView, distance: String?) {
    distance?.let { str ->
        str.toFloatOrNull()?.let { dist ->
            view.text = String.format("%.2f km", dist / 1000)
        } ?: run {
            view.text = ""
        }
    }
}

@BindingAdapter("textVisibility")
fun bindTextVisibility(view: TextView, text: String?) {
    if (text.isNullOrEmpty()) view.visibility = View.GONE else view.visibility = View.VISIBLE
}

@SuppressLint("SetTextI18n")
@BindingAdapter("timeDisplay")
fun bindTimeDisplay(view: TextView, time: Long) {
    view.text = "${view.resources.getString(R.string.location_refresh_time)} : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(time))}"
}

@BindingAdapter("glideImage")
fun bindGlideImage(view: ImageView, url: String?) {
    url?.let {
        view.loadImageFromFirebaseStorage("photo/$url")
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textPercent")
fun bindTextPercent(view: TextView, percent: Int) {
    if (percent > 100) {
        view.text = "-"
    } else {
        view.text = "$percent%"
    }
}

@BindingAdapter("level", "charged")
fun bindBatteryImageLevel(view: ImageView, level: Int, charged: Boolean) {
    if (charged) {
        view.setImageResource(R.drawable.battery_charging_level)
    } else {
        view.setImageResource(R.drawable.battery_level)
    }
    view.setImageLevel(level)
}