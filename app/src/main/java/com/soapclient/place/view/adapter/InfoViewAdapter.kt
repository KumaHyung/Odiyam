package com.soapclient.place.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.naver.maps.map.overlay.InfoWindow
import com.soapclient.place.R

abstract class InfoTextViewAdapter(context: Context) : InfoViewAdapter(context) {
    private val viewGroup: ViewGroup
    private val textView: TextView

    init {
        this.viewGroup = LinearLayout(context)
        this.textView = TextView(context)
        this.textView.setTextColor(context.getColor(R.color.colorBlack))
    }

    override fun getContentView(infoWindow: InfoWindow): View {
        this.textView.text = getText(infoWindow)
        this.textView.setTextColor(getTextColor(infoWindow))
        return this.textView
    }

    override fun getContentViewGroup(infoWindow: InfoWindow): ViewGroup {
        this.viewGroup.setBackgroundResource(getBackgroundResource(infoWindow))
        return viewGroup
    }

    abstract fun getText(infoWindow: InfoWindow): CharSequence

    abstract fun getBackgroundResource(infoWindow: InfoWindow): Int

    abstract fun getTextColor(infoWindow: InfoWindow): Int
}

abstract class InfoViewAdapter(context: Context) : InfoWindow.ViewAdapter() {
    private var view: View? = null
    private var viewGroup: ViewGroup? = null

    override fun getView(infoWindow: InfoWindow): View {
        val view = getContentView(infoWindow)
        val viewGroup = getContentViewGroup(infoWindow)

        if (this.view != view) {
            this.view = view
            viewGroup.removeAllViews()
            viewGroup.addView(view)
        }
        this.viewGroup = viewGroup

        return viewGroup
    }

    abstract fun getContentView(infoWindow: InfoWindow): View
    abstract fun getContentViewGroup(infoWindow: InfoWindow): ViewGroup
}
