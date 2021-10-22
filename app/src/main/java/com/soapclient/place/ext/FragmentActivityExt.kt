package com.soapclient.place.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.addFragment(containerViewId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().add(containerViewId, fragment, fragment::class.java.name).addToBackStack(fragment::class.java.name).commit()
}

fun FragmentActivity.replaceFragment(containerViewId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(containerViewId, fragment, fragment::class.java.name).commit()
}

fun FragmentActivity.popBackStack() {
    supportFragmentManager.popBackStack()
}