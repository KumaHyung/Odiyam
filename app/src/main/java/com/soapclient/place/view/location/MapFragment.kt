package com.soapclient.place.view.location

import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.NaviOption
import com.kakao.sdk.navi.model.RpOption
import com.kakao.sdk.navi.model.VehicleType
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.FusedLocationSource
import com.soapclient.place.R
import com.soapclient.place.data.Event
import com.soapclient.place.data.PlaceCategory
import com.soapclient.place.databinding.*
import com.soapclient.place.domain.entity.Location
import com.soapclient.place.domain.entity.PersonalData
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.entity.User
import com.soapclient.place.ext.addFragment
import com.soapclient.place.ext.loadDrawableFromFirebaseStorage
import com.soapclient.place.ext.showNeutralButtonDialog
import com.soapclient.place.ext.showTwoButtonDialog
import com.soapclient.place.service.LocationUpdateService
import com.soapclient.place.utilities.LauncherIcons
import com.soapclient.place.utilities.PermissionInfo
import com.soapclient.place.view.adapter.InfoTextViewAdapter
import com.soapclient.place.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private var searchJob: Job? = null
    private val markerSet = HashSet<Marker>()
    private val groupMarkerMap = HashMap<String, Marker>()
    private val infoWindowSet = HashSet<InfoWindow>()
    private val circleOverlay = CircleOverlay()
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private val locationSource: FusedLocationSource by lazy {
        FusedLocationSource(this, REQ_CODE_PERMISSION_REQUEST)
    }

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

    private val launcherIcons by lazy(LazyThreadSafetyMode.NONE) {
        LauncherIcons(requireContext())
    }

    private val viewModel: LocationViewModel by viewModels({ requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMapView(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentMapBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        context ?: return binding.root
        //setHasOptionsMenu(true)

        observeSearchedPlaceList()
        observeLocationSearchEvent()
        observeSelectedPlace()
        addAllCategoryChip(PlaceCategory.values())
        initRefreshButton()
        initSearchButton()
        initEditGroupButton()
        initMyLocationButton()
        observeSelectedId()
        observeGroupLocation()

        return binding.root
    }

    private fun toggleHeaderSlideState() {
        when (binding.headerCategorySearch.root.alpha) {
            0.0f -> {
                binding.headerCategorySearch.root.apply {
                    animate().translationY(0.0f).alpha(1.0f)
                }

                binding.sideBarLeft.root.apply {
                    animate().translationX(0.0f).alpha(1.0f)
                }

                binding.sideBarRight.root.apply {
                    animate().translationX(0.0f).alpha(1.0f)
                }
            }
            else -> {
                binding.headerCategorySearch.root.apply {
                    animate().translationY(-height.toFloat()).alpha(0.0f)
                }

                binding.sideBarLeft.root.apply {
                    animate().translationX(-width.toFloat()).alpha(0.0f)
                }

                binding.sideBarRight.root.apply {
                    animate().translationX(width.toFloat()).alpha(0.0f)
                }
            }
        }
    }

    private fun initSearchButton() {
        binding.headerCategorySearch.search.setOnClickListener {
            var latitude = 0.0
            var longitude = 0.0

            locationSource.lastLocation?.let {
                latitude = it.latitude
                longitude = it.longitude
            }

            moveToSearchFragment(latitude.toString(), longitude.toString())
        }
    }

    private fun initRefreshButton() {
        with(binding) {
            refresh.setOnClickListener {
                setRefreshButtonVisibility(false)
                searchPlace(PlaceCategory.values()[headerCategorySearch.categoryGroup.indexOfChild(headerCategorySearch.categoryGroup.findViewById<Chip>(headerCategorySearch.categoryGroup.checkedChipId))].code)
            }
        }
    }

    private fun initEditGroupButton() {
        binding.sideBarLeft.addUser.setOnClickListener {
            moveToGroupViewPagerFragment()
        }
    }

    private fun initMyLocationButton() {
        binding.sideBarRight.myLocation.setOnClickListener {
            locationSource.lastLocation?.let {
                moveCameraToInputPosition(it.latitude, it.longitude)
            }
        }
    }

    private fun observeSelectedId() {
        viewModel.selectedId.asLiveData().observe(viewLifecycleOwner) {
            groupMarkerMap[it]?.let { marker ->
                moveCameraToInputPosition(marker.position.latitude, marker.position.longitude)
            }
        }
    }

    private fun addAllCategoryChip(placeCategoryList: Array<PlaceCategory>) {
        placeCategoryList.forEach { item ->
            val bind = ListItemCategoryBinding.inflate(
                    LayoutInflater.from(binding.headerCategorySearch.categoryGroup.context),
                    binding.headerCategorySearch.categoryGroup,
                    true
            ).apply {
                category = item
                executePendingBindings()
            }
            bind.root.setOnClickListener {
                setRefreshButtonVisibility(false)
                searchPlace(item.code)
            }
        }
    }

    private fun clearCategoryGroup() {
        binding.headerCategorySearch.categoryGroup.clearCheck()
    }

    private fun moveCameraToInputPosition(latitude: Double, longitude: Double) {
        if (::naverMap.isInitialized) {
            val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(latitude, longitude)
            ).animate(CameraAnimation.Linear)
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun drawCircleOverlay(cameraPosition: CameraPosition) {
        circleOverlay.center = LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)
        circleOverlay.radius = 1000.0
        circleOverlay.color = Color.parseColor("#66A186BE")
        circleOverlay.outlineWidth = 5
        circleOverlay.outlineColor = Color.parseColor("#EEA186BE")
        circleOverlay.map = naverMap
    }

    private fun observeSearchedPlaceList() {
        viewModel.placeList.asLiveData().observe(viewLifecycleOwner) {
            clearAllMarker()
            clearAllWindow()
            it.forEach { location ->
                addLocationMarkerToMap(location, false)
            }
        }
    }

    private fun observeSelectedPlace() {
        viewModel.place.asLiveData().observe(viewLifecycleOwner) {
            clearCategoryGroup()
            clearAllMarker()
            clearAllWindow()
            addLocationMarkerToMap(it, true)
            moveCameraToInputPosition(it.y.toDouble(), it.x.toDouble())
        }
    }

    private fun observeLocationSearchEvent() {
        viewModel.placeSearchEvent.asLiveData().observe(viewLifecycleOwner) { event ->
            binding.progressBar.isVisible = event is Event.Loading
            when (event) {
                is Event.Loading -> {
                    hideBottomSheet(BottomSheetState.LOCATION)
                    hideBottomSheet(BottomSheetState.PLACE)
                }
                is Event.Success<*> -> {
                    when (event.data) {
                        is CameraPosition -> {
                            drawCircleOverlay(event.data)
                        }
                    }
                }
                is Event.Fail<*> -> {
                    when (event.error) {
                        is String -> {
                        }
                    }
                }
            }
        }
    }

    private fun clearAllMarker() {
        if (markerSet.isNotEmpty()) {
            markerSet.forEach { marker ->
                marker.map = null
            }
            markerSet.clear()
        }
    }

    private fun clearAllWindow() {
        if (infoWindowSet.isNotEmpty()) {
            infoWindowSet.forEach { infoWindow ->
                infoWindow.close()
            }
            infoWindowSet.clear()
        }
    }

    private fun setMapView(savedInstanceState: Bundle?) {
        mapView = binding.mapView.also {
            it.onCreate(savedInstanceState)
            it.getMapAsync(this)
        }
    }

    private fun setRefreshButtonVisibility(visible: Boolean) {
        binding.refresh.isVisible = visible
    }

    private fun searchPlace(groupCode: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchPlace(
                    groupCode,
                    naverMap.cameraPosition, 1000
            )
        }
    }

    private fun addGroupUserMarkerToMap(id: String, location: Location, user: User?) {
        if (::naverMap.isInitialized) {
            val personalData = user?.let {
                PersonalData(location, it)
            } ?: PersonalData(location, User())

            groupMarkerMap[id]?.let { marker ->
                marker.position = LatLng(location.latitude, location.longitude)
                marker.onClickListener = Overlay.OnClickListener {
                    moveCameraToInputPosition(marker.position.latitude, marker.position.longitude)
                    setBottomSheetData(personalData)
                    hideBottomSheet(BottomSheetState.PLACE)
                    showBottomSheet(BottomSheetState.LOCATION)
                    true
                }

            } ?: run {
                val infoWindow = InfoWindow()
                infoWindow.tag = false
                infoWindow.adapter = object : InfoTextViewAdapter(requireContext()) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return infoWindow.marker?.tag as CharSequence? ?: ""
                    }

                    override fun getTextColor(infoWindow: InfoWindow): Int {
                        return when (infoWindow.tag) {
                            true -> Color.WHITE
                            false -> Color.BLACK
                            else -> Color.BLACK
                        }
                    }

                    override fun getBackgroundResource(infoWindow: InfoWindow): Int {
                        return when (infoWindow.tag) {
                            true -> R.drawable.focus_window_background
                            false -> R.drawable.default_window_background
                            else -> R.drawable.default_window_background
                        }
                    }
                }
                val marker = Marker().apply {
                    position = LatLng(location.latitude, location.longitude)

                    val view = ItemLocationBinding.inflate(LayoutInflater.from(requireContext()))
                    view.personalData = personalData
                    view.image.loadDrawableFromFirebaseStorage("photo/${personalData.user.photo_url}") {
                        icon = OverlayImage.fromView(view.root)
                        map = naverMap
                    }

                    tag = id
//            minZoom = 1.0
//            maxZoom = 16.0
                    anchor = PointF(0.5f, 0.5f)
                    onClickListener = Overlay.OnClickListener {
                        moveCameraToInputPosition(position.latitude, position.longitude)
                        setBottomSheetData(personalData)
                        hideBottomSheet(BottomSheetState.PLACE)
                        showBottomSheet(BottomSheetState.LOCATION)
                        true
                    }
                }
                // TODO: 디자인 확정 후 마커 사용 여부 결정
                //infoWindow.open(marker)
                groupMarkerMap[id] = marker
            }
        }
    }

    private fun addLocationMarkerToMap(place: Place, performClick: Boolean) {
        if (::naverMap.isInitialized) {
            val infoWindow = InfoWindow()

            infoWindow.onClickListener = Overlay.OnClickListener { overlay ->
                val window = overlay as InfoWindow
                infoWindowSet.forEach {
                    it.zIndex = 0
                    it.tag = false
                    it.invalidate()
                }
                window.zIndex = 1
                window.tag = true
                window.invalidate()
                window.marker?.let {
                    moveCameraToInputPosition(it.position.latitude, it.position.longitude)
                    setBottomSheetData(place)
                    setNavigationButton(Navigation.TMAP, place)
                    setNavigationButton(Navigation.KAKAO, place)
                    setNavigationButton(Navigation.ONE, place)
                    setNavigationContainerVisibility()
                    hideBottomSheet(BottomSheetState.LOCATION)
                    showBottomSheet(BottomSheetState.PLACE)
                }
                true
            }

            infoWindow.adapter = object : InfoTextViewAdapter(requireContext()) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return infoWindow.marker?.tag as CharSequence? ?: ""
                }

                override fun getTextColor(infoWindow: InfoWindow): Int {
                    return when (infoWindow.tag) {
                        true -> Color.WHITE
                        false -> Color.BLACK
                        else -> Color.BLACK
                    }
                }

                override fun getBackgroundResource(infoWindow: InfoWindow): Int {
                    return when (infoWindow.tag) {
                        true -> R.drawable.focus_window_background
                        false -> R.drawable.default_window_background
                        else -> R.drawable.default_window_background
                    }
                }
            }

            val marker = Marker().apply {
                position = LatLng(place.y.toDouble(), place.x.toDouble())
                PlaceCategory.values().find {
                    it.code == place.category_group_code
                }?.let {
                    icon = OverlayImage.fromResource(it.iconResId)
                } ?: run {
                    icon = OverlayImage.fromResource(R.drawable.btn_location)
                }
                tag = place.place_name
//            minZoom = 1.0
//            maxZoom = 16.0
                anchor = PointF(0.5f, 0.5f)
                map = naverMap
            }
            infoWindow.open(marker)
            markerSet.add(marker)
            infoWindowSet.add(infoWindow)

            if (performClick) {
                infoWindow.performClick()
            }
        }
    }

    private fun setBottomSheetData(value: Any) {
        when (value) {
            is PersonalData -> binding.footerLocationInfo.personalData = value
            is Place -> binding.footerPlaceInfo.place = value
        }
    }

    private fun hideBottomSheet(state: BottomSheetState) {
        when (state) {
            BottomSheetState.PLACE -> {
                BottomSheetBehavior.from(binding.footerPlaceInfo.root).let {
                    if (it.state != BottomSheetBehavior.STATE_COLLAPSED) {
                        it.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
            BottomSheetState.LOCATION -> {
                BottomSheetBehavior.from(binding.footerLocationInfo.root).let {
                    if (it.state != BottomSheetBehavior.STATE_COLLAPSED) {
                        it.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }
        }
    }

    private fun showBottomSheet(state: BottomSheetState) {
        when (state) {
            BottomSheetState.PLACE -> {
                BottomSheetBehavior.from(binding.footerPlaceInfo.root).let {
                    if (it.state != BottomSheetBehavior.STATE_EXPANDED) {
                        it.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
            BottomSheetState.LOCATION -> {
                BottomSheetBehavior.from(binding.footerLocationInfo.root).let {
                    if (it.state != BottomSheetBehavior.STATE_EXPANDED) {
                        it.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
    }

    private fun getBottomSheetState(state: BottomSheetState): Int {
        return when (state) {
            BottomSheetState.PLACE -> BottomSheetBehavior.from(binding.footerPlaceInfo.root).state
            BottomSheetState.LOCATION -> BottomSheetBehavior.from(binding.footerLocationInfo.root).state
        }
    }

    override fun onMapReady(naverMap: NaverMap) {

        this.naverMap = naverMap.also {
            it.locationSource = locationSource
            it.locationTrackingMode = LocationTrackingMode.Follow
            it.addOnCameraChangeListener { _, _ ->
                setRefreshButtonVisibility(binding.headerCategorySearch.categoryGroup.checkedChipId != View.NO_ID)
            }
            it.setOnMapClickListener { _, _ ->
                when {
                    getBottomSheetState(BottomSheetState.PLACE) == BottomSheetBehavior.STATE_EXPANDED -> {
                        hideBottomSheet(BottomSheetState.PLACE)
                    }
                    getBottomSheetState(BottomSheetState.LOCATION) == BottomSheetBehavior.STATE_EXPANDED -> {
                        hideBottomSheet(BottomSheetState.LOCATION)
                    }
                    else -> {
                        toggleHeaderSlideState()
                    }
                }
            }

            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return infoWindow.marker?.tag as CharSequence? ?: ""
                }
            }
        }

        val uiSettings = naverMap.uiSettings
        uiSettings.isCompassEnabled = false
        uiSettings.isLocationButtonEnabled = false

    }

    private fun observeGroupLocation() {
        viewModel.groupLocation.asLiveData().observe(viewLifecycleOwner) { groupLocation ->
            groupLocation?.let {
                val (groupInfo, location) = groupLocation
                addGroupUserMarkerToMap(groupInfo.id, location, groupInfo.user)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            REQ_CODE_PERMISSION_REQUEST -> {
                if (locationSource.onRequestPermissionsResult(
                                requestCode, permissions,
                                grantResults
                        )
                ) {
                    if (!locationSource.isActivated) {
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
                    } else {
                        permissionGranted()
                    }
                    return
                }
            }
        }
    }

    private fun isLocationServiceEnabled(): Boolean {
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager?
        return locationManager?.let {
            it.isProviderEnabled(LocationManager.GPS_PROVIDER) || it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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

    private fun startNavigationActivity(navigation: Navigation, x: String, y: String, placeName: String) {
        when (navigation) {
            Navigation.TMAP -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tmap://route?goalx=${x}&goaly=${y}&goalname=${placeName}")).apply {
                    `package` = navigation.packageName
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }

            Navigation.KAKAO -> {
                startActivity(NaviClient.instance.navigateIntent(com.kakao.sdk.navi.model.Location(placeName, x, y), NaviOption(coordType = CoordType.WGS84, vehicleType = VehicleType.FIRST, rpOption = RpOption.FAST)))
            }

            Navigation.ONE -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${y},${x}?q=${Uri.encode(placeName)}")).apply {
                    `package` = navigation.packageName
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }
    }

    private fun setNavigationButton(navigation: Navigation, place: Place) {
        when (navigation) {
            Navigation.TMAP -> binding.footerPlaceInfo.tmapNavi
            Navigation.KAKAO -> binding.footerPlaceInfo.kakaoNavi
            Navigation.ONE -> binding.footerPlaceInfo.oneNavi
        }.apply {
            val appIcon = launcherIcons.getAppIcon(requireContext(), navigation.packageName)
            setImageDrawable(appIcon)
            isVisible = appIcon != null
            setOnClickListener {
                startNavigationActivity(navigation, place.x, place.y, place.place_name)
            }
        }
    }

    private fun setNavigationContainerVisibility() {
        binding.footerPlaceInfo.naviContainer.isVisible = binding.footerPlaceInfo.tmapNavi.isVisible
                || binding.footerPlaceInfo.kakaoNavi.isVisible
                || binding.footerPlaceInfo.oneNavi.isVisible
    }

    private fun checkPermissionAfterSetting() = checkPermissions(false)

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
        startLocationUpdateService()
    }

    private fun startLocationUpdateService() {
        val serviceIntent = Intent(requireContext(), LocationUpdateService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }

    private fun permissionDenied() {
        activity?.finish()
    }

    private fun moveToSearchFragment(latitude: String, longitude: String) {
        addFragment(R.id.fragment_container, SearchFragment().apply {
            arguments = bundleOf("latitude" to latitude, "longitude" to longitude)
        })
    }

    private fun moveToGroupViewPagerFragment() {
        addFragment(R.id.fragment_container, GroupViewPagerFragment())
    }

    private enum class BottomSheetState {
        PLACE,
        LOCATION;
    }

    private enum class Navigation(val packageName: String) {
        TMAP("com.skt.tmap.ku"),
        KAKAO("com.locnall.KimGiSa"),
        ONE("kt.navi");
    }

    companion object {
        private const val REQ_CODE_PERMISSION_REQUEST = 1000
    }
}