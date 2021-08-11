package com.example.safecare.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.safecare.R
import com.example.safecare.StaticObjects
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_notifications.*

class MapaFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMapView: MapView
    lateinit var googleMap1: GoogleMap
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val ZOOM_LEVEL = 15f
    val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mMapView = mapa_seguimiento
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        googleMap1 = p0

        with(p0) {

            isMyLocationEnabled = true
            StaticObjects.alertas.forEach {
                this.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.ubicacion.lat, it.ubicacion.lng))
                        .title(it.titulo)
                )
            }

            mMapView.onResume()
//            val t = googleMap1.addMarker(
//                MarkerOptions()
//                    .position(
//                        LatLng(-16.4007783, -71.5354114)
//                    ).title("N2")
//            )
            googleMap1.moveCamera(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        -16.4125098, -71.5426488
                    ),
                    ZOOM_LEVEL
                )
            )
        }

    }
}