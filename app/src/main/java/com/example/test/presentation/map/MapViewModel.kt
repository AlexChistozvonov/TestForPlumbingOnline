package com.example.test.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _getUserLocation = MutableLiveData<Point>()
    val showSearchResults: LiveData<Point> get() = _getUserLocation

    private val _getAddress = MutableLiveData<String>()
    val getAddress: LiveData<String> get() = _getAddress

    fun getUserLocation() {
        val locationManager: LocationManager = MapKitFactory.getInstance().createLocationManager()
        locationManager.requestSingleUpdate(object : LocationListener {
            override fun onLocationStatusUpdated(p0: LocationStatus) {
            }

            override fun onLocationUpdated(p0: com.yandex.mapkit.location.Location) {
                viewModelScope.launch(Dispatchers.IO)
                {
                    val lat = p0.position.latitude
                    val lon = p0.position.longitude
                    _getUserLocation.postValue(Point(lat, lon))
                }
            }
        })
    }

    fun getAddress(point: Point, zoom: Int) {
        SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
            .submit(point, zoom, SearchOptions(), object : Session.SearchListener {
                override fun onSearchResponse(p0: Response) {
                    viewModelScope.launch(Dispatchers.IO) {
                        _getAddress.postValue(p0.collection.children[0].obj?.name.toString())
                    }
                }

                override fun onSearchError(p0: Error) {}
            })
    }
}