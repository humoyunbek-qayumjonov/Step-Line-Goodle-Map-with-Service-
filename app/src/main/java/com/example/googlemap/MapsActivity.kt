package com.example.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast

//import com.github.florent37.runtimepermission.kotlin.askPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.googlemap.database.MyLocation
import com.example.googlemap.database.MyDatabase
import com.example.googlemap.databinding.ActivityMapsBinding
import com.example.googlemap.service.MyService
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var appDatabase: MyDatabase
    lateinit var list: ArrayList<MyLocation>
    private var myService: MyService? = null
    private var foregroundOnlyLocationServiceBound = false

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MyService.LocalBinder
            myService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            myService = null
            foregroundOnlyLocationServiceBound = false
        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageMap.setOnClickListener{
            if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL){
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }else{
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        appDatabase = MyDatabase.getInstance(this)

        list = ArrayList()

        list.addAll(appDatabase.myDao().getAllTime())

        if (list.isEmpty()) {
            askPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) {
                //

                val fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this)
                val locationTask: Task<Location> = fusedLocationProviderClient.lastLocation
                locationTask.addOnSuccessListener { it: Location ->
                    if (it != null) {
                        mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("It's me"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                        mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("Siz turgan joy"))
                        mMap.isMyLocationEnabled = true
                        mMap.uiSettings.isMyLocationButtonEnabled = true
                        Toast.makeText(this, "Siz turgan joy", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(
                            TAG,
                            "getLastLocation: location was null,,,,,,,,,,,,,,,,,,,..............."
                        )
                    }
                }

                //


                val serviceIntent = Intent(this,MyService::class.java)
                bindService(serviceIntent,foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)

                Toast.makeText(
                    this,
                    "Dastur to`liq holda ishlashligi uchun GPS ni o`chirmang",
                    Toast.LENGTH_LONG
                ).show()
            }.onDeclined { e ->
                if (e.hasDenied())

                {

                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setMessage("Please accept our permissions")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain();
                        } //ask again
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss();
                        }
                        .show();
                }

                if (e.hasForeverDenied()) {
                    e.goToSettings();
                }
            }
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (list.isNotEmpty()) {
            val serviceIntent = Intent(this,MyService::class.java)
            bindService(serviceIntent,foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)

            val latLngList = ArrayList<LatLng>()
            for (myLocation in list) {
                latLngList.add(LatLng(myLocation.latitude!!, myLocation.longtitude!!))
            }

            // Add polylines to the map.
            // Polylines are useful to show a route or some other connection between points.
            val polyline1 = googleMap.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .addAll(latLngList)
            )

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.last(), 15.0f))
            mMap.addMarker(MarkerOptions().position(latLngList.last()).title("Oxirgi yozilgan location"))
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.setOnMapClickListener {
                try {
                    val geocoder = Geocoder(this)
                    var addressList: List<Address> =
                        geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val address = addressList.get(0)
                    Toast.makeText(this, "${address.getAddressLine(0)}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Addresni ola olmadik. Birozdan so'ng qayta urining...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onPolylineClick(p0: Polyline) {

    }

}