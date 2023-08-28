package com.example.mapdemoapplication


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mapdemoapplication.databinding.ActivitySearchBinding
import com.example.mapdemoapplication.helper.SQLiteHelper
import com.example.mapdemoapplication.utils.IntentKeys
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException


class SearchActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivitySearchBinding

    private lateinit var locationResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var sqLiteHelper: SQLiteHelper

    private var lat = ""
    private var long = ""
    private var city = ""

    private val permissionCode = 101

    private var googleMap: GoogleMap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        //fetchLocation()
        val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)

        setClick()
        setupPlacesSdk()
        initResultLauncher()

        sqLiteHelper = SQLiteHelper(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        googleMap = null
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
        }

        val latLng: LatLng = if (lat == "" && long == ""){
            LatLng(0.0, 0.0)
        }else{
            LatLng(lat.toDouble(), long.toDouble())
        }

        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.apply {
            animateCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
            addMarker(markerOptions)
        }
    }

    private fun focusMapOnCoordinates(latitude: Double, longitude: Double) {
        val coordinates = LatLng(latitude, longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 20f)
        googleMap?.animateCamera(cameraUpdate)
    }

    private fun setClick(){
        binding.buttonSave.setOnClickListener {
            if (binding.editTextSelectLocation.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter any address after that you can Save", Toast.LENGTH_SHORT).show()
            }else{
                val  intent =  Intent()
                intent.putExtra(IntentKeys.EDITTEXT_VALUE, binding.editTextSelectLocation.text.toString())
                intent.putExtra(IntentKeys.CITY, city)
                intent.putExtra(IntentKeys.LAT, lat)
                intent.putExtra(IntentKeys.LONG, long)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        binding.editTextSelectLocation.setOnClickListener {
            locationResultLauncher.launch(intent)
        }

        binding.buttonFocus.setOnClickListener {
            setFocus()
        }
    }

    private fun setFocus(){
        val latitudeStr = lat
        val longitudeStr = long

        if (latitudeStr.isNotEmpty() && longitudeStr.isNotEmpty()) {
            val latitude = latitudeStr.toDouble()
            val longitude = longitudeStr.toDouble()

            focusMapOnCoordinates(latitude, longitude)

            val latLng = LatLng(latitude,longitude)
            val markerOptions = MarkerOptions().position(latLng).title("I am here!")
            googleMap?.apply {
                animateCamera(CameraUpdateFactory.newLatLng(latLng))
                animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                addMarker(markerOptions)
            }
        }
    }

    // for location
    private fun setupPlacesSdk() {
        Places.initialize(
            applicationContext,
            this.getString(R.string.google_map_key)
        )
        Places.createClient(this)
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        )

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this)

        binding.editTextSelectLocation.setOnClickListener {
            locationResultLauncher.launch(intent)
        }
    }

    private fun initResultLauncher() {
        locationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.let { data ->
                        val place = Autocomplete.getPlaceFromIntent(data)
                        binding.editTextSelectLocation.setText(setAddress(place.latLng))
                        Log.d("TAG", "address is: ${setAddress(place.latLng)}")
                    }

                }
            }
    }

    private fun setAddress(it: LatLng?): String {
        it?.let {
            val address = getAddressFromLatLng(this, it.latitude, it.longitude)
            lat = it.latitude.toString()
            long = it.longitude.toString()

            if (address != null) {
                city = address.locality
            }

            //for focus on map location
            binding.buttonFocus.visibility = View.VISIBLE
            setFocus()

            return address?.getAddressLine(0) ?: (address?.thoroughfare +
                    ", " + address?.subAdminArea +
                    ", " + address?.locality +
                    ", " + address?.adminArea +
                    ", " + address?.countryName +
                    ", " + address?.postalCode)
        }
        return ""
    }

    private fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): Address? {
        val geocoder = Geocoder(context)

        try {
            val addressList = geocoder.getFromLocation(lat, lng, 1)
            addressList?.let {
                if (addressList.isNotEmpty()) {
                    return addressList[0]
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


}