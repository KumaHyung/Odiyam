package com.soapclient.place.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.*
import androidx.work.*
import com.naver.maps.map.CameraPosition
import com.soapclient.place.R
import com.soapclient.place.data.Event
import com.soapclient.place.data.LocationPagingSource
import com.soapclient.place.delegate.SignInViewModelDelegate
import com.soapclient.place.domain.entity.*
import com.soapclient.place.domain.usecase.group.*
import com.soapclient.place.domain.usecase.history.AddPlaceHistoryUseCase
import com.soapclient.place.domain.usecase.history.DeletePlaceHistoryUseCase
import com.soapclient.place.domain.usecase.history.GetAllPlaceHistoryUseCase
import com.soapclient.place.domain.usecase.location.GetUsersLocationUseCase
import com.soapclient.place.domain.usecase.photo.UploadImageUseCase
import com.soapclient.place.domain.usecase.place.SearchPlaceByKeywordUseCase
import com.soapclient.place.domain.usecase.place.SearchPlaceUseCase
import com.soapclient.place.domain.usecase.user.FindUserIdByEmailUseCase
import com.soapclient.place.domain.usecase.user.GetUserProfileAwaitUseCase
import com.soapclient.place.domain.usecase.user.GetUserProfileUseCase
import com.soapclient.place.domain.usecase.user.UpdatePhotoUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getAllPlaceHistoryUseCase: GetAllPlaceHistoryUseCase,
    private val addPlaceHistoryUseCase: AddPlaceHistoryUseCase,
    private val deletePlaceHistoryUseCase: DeletePlaceHistoryUseCase,
    private val searchPlaceUseCase: SearchPlaceUseCase,
    private val searchPlaceByKeywordUseCase: SearchPlaceByKeywordUseCase,
    private val grantLocationSharingUseCase: GrantLocationSharingUseCase,
    private val denyLocationSharingUseCase: DenyLocationSharingUseCase,
    private val getUsersLocationUseCase: GetUsersLocationUseCase,
    private val getGrantedUserIdsUseCase: GetGrantedUserIdsUseCase,
    private val getGrantWaitUserIdsUseCase: GetGrantWaitUserIdsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserProfileAwaitUseCase: GetUserProfileAwaitUseCase,
    private val signInViewModelDelegate: SignInViewModelDelegate,
    private val addUserToGrantWaitGroupUseCase: AddUserToGrantWaitGroupUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val findUserIdByEmailUseCase: FindUserIdByEmailUseCase,
    private val updatePhotoUrlUseCase: UpdatePhotoUrlUseCase
) : ViewModel(),
    SignInViewModelDelegate by signInViewModelDelegate {

    private var updateUserPhotoUrlJob: Job? = null

    private val _placeList = MutableSharedFlow<List<Place>>()
    val placeList = _placeList.asSharedFlow()

    private val _place = MutableSharedFlow<Place>()
    val place = _place.asSharedFlow()

    private val _selectedId = MutableSharedFlow<String>()
    val selectedId = _selectedId.asSharedFlow()

    private val _placeSearchEvent = MutableSharedFlow<Event>()
    val placeSearchEvent = _placeSearchEvent.asSharedFlow()

    private val _requestLocationSharingEvent = MutableSharedFlow<Event>()
    val requestLocationSharingEvent = _requestLocationSharingEvent.asSharedFlow()

    private val _uploadImageEvent = MutableSharedFlow<Event>()
    val uploadImageEvent = _uploadImageEvent.asSharedFlow()

    val placeHistoryList: Flow<List<Place>> = getAllPlaceHistoryUseCase(Unit).map {
        it.data ?: emptyList()
    }

    private val grantedWaitUserIds: StateFlow<List<String>> = userInfo.transformLatest { user ->
        user?.getUid()?.let { uid ->
            getGrantWaitUserIdsUseCase(uid).collect {
                it.data?.let { data ->
                    emit(data)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<String>())

    private val grantedUserIds: StateFlow<List<String>> = userInfo.transformLatest { user ->
        user?.getUid()?.let { uid ->
            getGrantedUserIdsUseCase(uid).collect {
                it.data?.let { data ->
                    emit(data)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<String>())

    val grantedUserProfiles: StateFlow<List<GroupInfo>> = grantedUserIds.transformLatest { idList ->
        emit(idList.map { id ->
            getUserProfileAwaitUseCase(id).data?.data?.let { data ->
                GroupInfo(id,
                    User(data.name,
                        data.email,
                        data.photo_url))
            } ?: GroupInfo(id, User("Unknown", "", ""))
        })
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<GroupInfo>())

    val waitUserProfiles: StateFlow<List<GroupInfo>> = grantedWaitUserIds.transformLatest { idList ->
        emit(idList.map { id ->
            getUserProfileAwaitUseCase(id).data?.data?.let { data ->
                GroupInfo(id,
                    User(data.name,
                        data.email,
                        data.photo_url))
            } ?: GroupInfo(id, User("Unknown", "", ""))
        })
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<GroupInfo>())

    val myProfile: StateFlow<GroupInfo?> = userInfo.transformLatest { user ->
        user?.getUid()?.let { id ->
            getUserProfileUseCase(id).collect {
                it.data?.let { data ->
                    emit(GroupInfo(id, User(data.name, data.email, data.photo_url)))
                }
            }
        }

    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val groupLocation: StateFlow<Pair<GroupInfo, Location>?> = grantedUserProfiles.transformLatest { profiles ->
        getUsersLocationUseCase(profiles.map { it.id }).collect { result ->
            result.data?.let { data ->
                profiles.find {
                    it.id == data.first
                }?.let { groupInfo ->
                    emit(groupInfo to Location(
                        data.second.latitude,
                        data.second.longitude,
                        data.second.battery_percent,
                        data.second.battery_charging,
                        data.second.updateTime
                    ))
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    suspend fun setSelectedPlace(place: Place) {
        _place.emit(place)
    }

    suspend fun setSelectedId(id: String) {
        _selectedId.emit(id)
    }

    fun uploadImage(uri: Uri) {
        userInfo.value?.getUid()?.let { uid ->
            updateUserPhotoUrlJob?.cancel()
            updateUserPhotoUrlJob = viewModelScope.launch {
                _uploadImageEvent.emit(Event.Loading)
                val result = uploadImageUseCase(UploadImageUseCase.Param(uid, uri))
                if (result is Result.Success) {
                    updatePhotoUrlUseCase(UpdatePhotoUrlUseCase.Param(uid, "${uri.lastPathSegment}"))
                    _uploadImageEvent.emit(Event.Success(null))
                } else {
                    _uploadImageEvent.emit(Event.Fail(null))
                }
            }
        }
    }

    suspend fun searchPlace(category_group_code: String, cameraPosition: CameraPosition, radius: Int) {
        _placeSearchEvent.emit(Event.Loading)
        var page = 1
        val resultList = ArrayList<Place>()
        try {
            do {
                val result = searchPlaceUseCase(SearchPlaceUseCase.Param(category_group_code, cameraPosition.target.longitude.toString(), cameraPosition.target.latitude.toString(), radius, page++))
                var isEnd = true
                if (result is Result.Success) {
                    isEnd = result.data.isLastData
                    resultList.addAll(result.data.placeList)
                }
            } while (!isEnd)

            _placeList.emit(resultList)
            _placeSearchEvent.emit(Event.Success(cameraPosition))
        } catch (e: Exception) {
            _placeSearchEvent.emit(Event.Fail(e))
        }
    }

    fun searchPlaceByKeyword(keyword: String, x: String, y: String): Flow<PagingData<Place>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 5),
            pagingSourceFactory = { LocationPagingSource(searchPlaceByKeywordUseCase, keyword, x = x, y = y) }
        ).flow.cachedIn(viewModelScope)
    }

    suspend fun savePlaceHistory(place: Place) {
        place.date = Date(System.currentTimeMillis())
        addPlaceHistoryUseCase(place)
    }

    suspend fun deletePlaceHistory(place: Place) {
        deletePlaceHistoryUseCase(place)
    }

    private fun addUserToGrantWaitGroup(id: String) {
        userInfo.value?.getUid()?.let { uid ->
            viewModelScope.launch {
                addUserToGrantWaitGroupUseCase(
                    AddUserToGrantWaitGroupUseCase.Param(
                        uid,
                        id,
                        RequestInfo(System.currentTimeMillis())
                    )
                )
            }
        }
    }

    fun grantLocationSharingRequest(id: String) {
        userInfo.value?.getUid()?.let { uid ->
            viewModelScope.launch {
                grantLocationSharingUseCase(GrantLocationSharingUseCase.Param(uid, id))
            }
        }
    }

    fun denyLocationSharingRequest(id: String) {
        userInfo.value?.getUid()?.let { uid ->
            viewModelScope.launch {
                denyLocationSharingUseCase(DenyLocationSharingUseCase.Param(uid, id))
            }
        }
    }

    fun requestLocationSharingByEmail(email: String) {
        viewModelScope.launch {
            if (isMyAccountEmail(email)) {
                _requestLocationSharingEvent.emit(Event.Fail(R.string.request_share_location_wrong_id))
                return@launch
            }
            _requestLocationSharingEvent.emit(Event.Loading)
            val result = findUserIdByEmailUseCase(email)
            if (result is Result.Success) {
                result.data.data?.let { id ->
                    addUserToGrantWaitGroup(id)
                    _requestLocationSharingEvent.emit(Event.Success(R.string.request_share_location_success))
                } ?: run {
                    _requestLocationSharingEvent.emit(Event.Fail(R.string.request_share_location_not_exist))
                }
            } else {
                _requestLocationSharingEvent.emit(Event.Fail(R.string.request_share_location_cancel))
            }
        }
    }

    private fun isMyAccountEmail(email: String): Boolean {
        myProfile.value?.let {
            return it.user.email.equals(email, true)
        } ?: return false
    }

    override fun onCleared() {
        super.onCleared()
    }
}
