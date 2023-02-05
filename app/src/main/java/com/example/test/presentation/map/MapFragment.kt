package com.example.test.presentation.map

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.test.R
import com.example.test.databinding.MapFragmentBinding
import com.example.test.util.extantion.observe
import com.example.test.util.getBitmapFromDrawable
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.map_fragment), InputListener {

    private val viewModel by viewModels<MapViewModel>()

    private val binding by viewBinding(MapFragmentBinding::bind)
    private var mapView: MapView? = null
    private var initUserLocation = false
    private var address = ""
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        requestLocationPermission()
        getUserLocation()
        initView()
    }

    private fun initView() = with(binding) {
        requestLocationPermission()
        mapView.map.addInputListener(this@MapFragment)

        btnConfirmPoint.setOnClickListener {
            if (lat == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.pin_not_selected),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                findNavController().navigate(
                    MapFragmentDirections.openInformationFragment(
                        address,
                        lat.toString(),
                        lon.toString()
                    )
                )
            }
        }
    }

    private fun moveCamera(point: Point) {
        mapView?.map?.move(
            CameraPosition(point, 15f, 0.0f, 0.0f)
        )
    }

    private fun getAddress() {
        observe(viewModel.getAddress) {
            address = it
        }
    }

    private fun getUserLocation() {
        viewModel.getUserLocation()
        observe(viewModel.showSearchResults) {
            moveUserLocation(it.latitude, it.longitude)
        }
    }

    private fun moveUserLocation(lat: Double?, lon: Double?) {
        if (lat != null && lon != null) {
            Point(lat, lon)
            moveCamera(Point(lat, lon))
            initUserLocation = true
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                PERMISSION_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(PERMISSION_FINE_LOCATION),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    override fun onMapTap(p0: Map, p1: Point) {
        addPlacemark(p1)
        val zoom = p0.cameraPosition.zoom.toInt()
        viewModel.getAddress(p1, zoom)
        getAddress()
        lat = p1.latitude
        lon = p1.longitude
    }

    private fun addPlacemark(point: Point) {
        mapView?.map?.mapObjects?.clear()
        val imageProvider = ImageProvider.fromBitmap(
            getBitmapFromDrawable(requireContext(), R.drawable.ic_pin)
        )

        mapView?.map?.mapObjects?.addPlacemark(point, imageProvider)
    }

    override fun onMapLongTap(p0: Map, p1: Point) {}

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {
        const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
        const val PERMISSION_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
    }
}