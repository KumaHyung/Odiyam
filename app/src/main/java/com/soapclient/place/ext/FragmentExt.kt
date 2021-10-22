package com.soapclient.place.ext

import android.app.Dialog
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soapclient.place.R

fun Fragment.addFragment(containerViewId: Int, fragment: Fragment) {
    parentFragmentManager.beginTransaction().add(containerViewId, fragment, fragment::class.java.name).addToBackStack(fragment::class.java.name).commit()
}

fun Fragment.replaceFragment(containerViewId: Int, fragment: Fragment) {
    parentFragmentManager.beginTransaction().replace(containerViewId, fragment, fragment::class.java.name).commit()
}

fun Fragment.popBackStack() {
    parentFragmentManager.popBackStack()
}

fun Fragment.popBackStackWhenChild() {
    parentFragment?.parentFragmentManager?.popBackStack()
}

fun Fragment.showToastShort(text: CharSequence) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

fun Fragment.showToastLong(text: CharSequence) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

inline fun Fragment.showTwoButtonDialog(title: String, message: String, resId: Int, negativeButtonText: CharSequence, positiveButtonText: CharSequence, crossinline onNegativeClicked: (dialog: Dialog)-> Unit, crossinline onPositiveClicked: (dialog: Dialog)-> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegativeClicked(dialog as Dialog)
        }
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClicked(dialog as Dialog)
        }
        .setView(resId)
        .setCancelable(false)
        .show()
}

inline fun Fragment.showTwoButtonDialog(title: String, message: String, negativeButtonText: CharSequence, positiveButtonText: CharSequence, crossinline onNegativeClicked: (dialog: Dialog)-> Unit, crossinline onPositiveClicked: (dialog: Dialog)-> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegativeClicked(dialog as Dialog)
        }
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClicked(dialog as Dialog)
        }
        .setCancelable(false)
        .show()
}

inline fun Fragment.showNeutralButtonDialog(title: String, message: String, neutralButtonText: CharSequence, crossinline onNeutralButtonClicked: (dialog: Dialog) -> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton(neutralButtonText) { dialog, _ ->
            onNeutralButtonClicked(dialog as Dialog)
        }
        .setCancelable(false)
        .show()
}
