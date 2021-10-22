package com.soapclient.place.view.intro

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.soapclient.place.R
import androidx.fragment.app.Fragment
import com.soapclient.place.databinding.FragmentPermissionCheckBinding
import com.soapclient.place.ext.replaceFragment
import com.soapclient.place.ext.showNeutralButtonDialog
import com.soapclient.place.ext.showTwoButtonDialog
import com.soapclient.place.utilities.PermissionInfo
import com.soapclient.place.view.location.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionCheckFragment : Fragment() {
    private lateinit var binding: FragmentPermissionCheckBinding

    private val requestStartActivityForLocationService = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkLocationServiceAfterSetting()
    }

    private val requestStartActivityForDetailSettings = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermissionAfterSetting()
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        checkPermissionsAfterRequest(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionCheckBinding.inflate(inflater, container, false)
        context ?: return binding.root
        initProgressContinueButton()
        return binding.root
    }

    private fun initProgressContinueButton() {
        binding.progressContinue.setOnClickListener {
            if (isLocationServiceEnabled()) {
                checkPermissions(true)
            } else {
                showNeutralButtonDialog(
                    getString(R.string.guidance),
                    getString(R.string.location_setting_description),
                    getString(R.string.confirm)
                ) {
                    it.dismiss()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    requestStartActivityForLocationService.launch(intent)
                }
            }
        }
    }

    private fun checkPermissionAfterSetting() = checkPermissions(false)

    private fun isLocationServiceEnabled(): Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager?.let {
            it.isProviderEnabled(LocationManager.GPS_PROVIDER) || it.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        } ?: false
    }

    private fun checkLocationServiceAfterSetting() {
        if (isLocationServiceEnabled()) {
            checkPermissions(true)
        } else {
            showNeutralButtonDialog(
                getString(R.string.guidance),
                getString(R.string.location_setting_deny),
                getString(R.string.confirm)
            ) {
                it.dismiss()
                activity?.finish()
            }
        }
    }

    private fun moveToMapFragment() {
        replaceFragment(R.id.fragment_container, MapFragment())
    }

    private fun checkPermissionsAfterRequest(map: Map<String, Boolean>) {
        val deniedPermissions = ArrayList<String>()
        for (entry in map.entries) {
            if (!entry.value) {
                deniedPermissions.add(entry.key)
            }
        }

        if (deniedPermissions.isEmpty()) {
            permissionGranted()
        } else {
            showTwoButtonDialog(
                getString(R.string.guidance),
                getString(R.string.check_setting_permission),
                getString(R.string.no),
                getString(R.string.yes),
                {
                    it.dismiss()
                    permissionDenied()
                },
                {
                    it.dismiss()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:${requireContext().packageName}"))
                    requestStartActivityForDetailSettings.launch(intent)
                })
        }
    }

    private fun checkPermissions(isNeedPermissionRequest: Boolean) {
        val notGrantedPermissions = getNotGrantedPermissions()

        if (notGrantedPermissions.isEmpty()) {
            permissionGranted()
        } else if (!isNeedPermissionRequest) {
            permissionDenied()
        } else {
            notGrantedPermissions.toArray(
                arrayOfNulls<String>(
                    notGrantedPermissions.size
                )
            )
            requestMultiplePermissions.launch(
                notGrantedPermissions.toArray(
                    arrayOfNulls<String>(
                        notGrantedPermissions.size
                    )
                )
            )
        }
    }

    private fun getNotGrantedPermissions(): ArrayList<String> {
        val notGrantedPermissions = ArrayList<String>()

        for (permission in PermissionInfo.LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission)
            }
        }

        return notGrantedPermissions
    }

    private fun permissionGranted() {
        moveToMapFragment()
    }

    private fun permissionDenied() {

    }
}