package com.example.mapdemoapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapdemoapplication.adapter.AddressListItemAdapter
import com.example.mapdemoapplication.databinding.ActivityMainBinding
import com.example.mapdemoapplication.helper.SQLiteHelper
import com.example.mapdemoapplication.model.AddressModel
import com.example.mapdemoapplication.utils.IntentKeys
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

var firstName = ""

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var locationResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var addressListItemAdapter: AddressListItemAdapter
    private var ads: AddressModel?=null

    private var values=""
    private var city=""
    private var distance=""

    private var lat = ""
    private var long = ""

    private var firstLat = ""
    private var firstLong = ""

    var adsList = ArrayList<AddressModel>()

    private var teamData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
        setRecyclerViewAddress()

        sqLiteHelper = SQLiteHelper(this)
        getAddress()
        setClick()

        setTheAlertData()
        setupPlacesSdk(false)
    }

    @SuppressLint("Range")
    private fun setClick(){
        binding.buttonAddPOIBottom.setOnClickListener {
            val i = Intent(this, SearchActivity::class.java)
            startActivityForResult(i, 1)
        }
        binding.buttonAddPOI.setOnClickListener {
            val i = Intent(this, SearchActivity::class.java)
            startActivityForResult(i, 1)
        }
        binding.buttonAscending.setOnClickListener {
            val data = addressListItemAdapter.adsList.subList(1, addressListItemAdapter.adsList.size)
            data.sortBy {
                it.distance
            }
            addressListItemAdapter.notifyDataSetChanged()
        }
        binding.buttonDescending.setOnClickListener {
            val data = addressListItemAdapter.adsList.subList(1, addressListItemAdapter.adsList.size)
            data.sortByDescending {
                it.distance
            }
            addressListItemAdapter.notifyDataSetChanged()
        }
    }

    private fun getAddress() {
        val adsList = sqLiteHelper.getAllAddress()
        Log.e("pppp","${adsList.size}")
        Log.e("pppp","$adsList")

        addressListItemAdapter.addItems(adsList)

        if (addressListItemAdapter.adsList.isNotEmpty()){
            addressListItemAdapter.adsList.first {
                firstName = it.city
                true
            }
        }
        addressListItemAdapter.notifyDataSetChanged()

        if (adsList.isEmpty()){
            binding.recyclerViewData.visibility = View.GONE
            binding.buttonAddPOIBottom.visibility = View.GONE
            binding.buttonAddPOI.visibility = View.VISIBLE
            binding.textViewMessage.visibility = View.VISIBLE
            binding.buttonAscending.visibility = View.GONE
            binding.buttonDescending.visibility = View.GONE
        }else{
            binding.recyclerViewData.visibility = View.VISIBLE
            binding.buttonAddPOIBottom.visibility = View.VISIBLE
            binding.buttonAddPOI.visibility = View.GONE
            binding.textViewMessage.visibility = View.GONE
            binding.buttonAscending.visibility = View.VISIBLE
            binding.buttonDescending.visibility = View.VISIBLE

            firstLat = adsList[0].lat
            firstLong = adsList[0].long

            Log.e("pppp","first lat is $firstLat")
            Log.e("pppp","first long is $firstLong")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {
                values = data?.getStringExtra(IntentKeys.EDITTEXT_VALUE)!!
                city = data.getStringExtra(IntentKeys.CITY)!!
                lat = data.getStringExtra(IntentKeys.LAT)!!
                long = data.getStringExtra(IntentKeys.LONG)!!
                if (values.isNotEmpty()){
                    distance = if (addressListItemAdapter.adsList.isNotEmpty()){
                        String.format("%.2f", haversine(addressListItemAdapter.adsList[0].lat.toDouble(), addressListItemAdapter.adsList[0].long.toDouble(),lat.toDouble(),long.toDouble()))
                    }else{
                        "345"
                    }
                    addAddress(city,values,distance,lat,long)
                }
            }
        }
    }

    //find distance
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

    private fun addAddress(mCity: String, mAddress: String, mDistance: String, mLat: String, mLong: String) {
        val city = mCity
        val addressFiled = mAddress
        val distance = mDistance
        val lat = mLat
        val long = mLong

        if (city.isEmpty() || addressFiled.isEmpty() || distance.isEmpty()){
            Toast.makeText(this, "Please enter required field", Toast.LENGTH_SHORT).show()
        }else{
            val ads = AddressModel(city = city, addressFiled = addressFiled, distance = distance, lat = lat, long = long)
            val status = sqLiteHelper.insertAddress(ads)

            //check insert success or not
            if (status > -1){
                Toast.makeText(this, "Address Added...", Toast.LENGTH_SHORT).show()
                getAddress()
            }else{
                Toast.makeText(this, "record not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerViewAddress() {
        binding.recyclerViewData.setHasFixedSize(true)
        binding.recyclerViewData.layoutManager = LinearLayoutManager(this)
        addressListItemAdapter = AddressListItemAdapter(adsList,onEventListener = {view, addressModel ->
           // firstLat = addressModel.lat
            when(view.id){
                R.id.imageViewEdit->{
                    teamData = "123"
                    setupPlacesSdk(true)
                    ads = addressModel
                }
                R.id.imageViewDelete->{
                    firstName = " "
                    deleteAddress(addressModel.id)
                }
            }
        })
        binding.recyclerViewData.adapter = addressListItemAdapter
    }

    private fun updateAddress(mCity: String, mAddressFiled: String, mDistance: String, mLat: String, mLong: String){
        val city = mCity
        val addressFiled = mAddressFiled
        val distance = mDistance
        val lat = mLat
        val long = mLong

        //check record not change
        if (city == ads?.city && addressFiled == ads?.addressFiled){
            Toast.makeText(this, "Record not changed...", Toast.LENGTH_SHORT).show()
            return
        }

        if (ads == null) return

        val ads = AddressModel(id = ads!!.id, city = city, addressFiled = addressFiled, distance = distance, lat = lat?:"", long = long?:"")
        val status = sqLiteHelper.updateAddress(ads)

        if (status > -1){
            getAddress()
        }else{
            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPlacesSdk(isCurrent: Boolean) {
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

        if (isCurrent){
            locationResultLauncher.launch(intent)
        }
    }

    /**
     * here i set the functiion
     * */
    private fun setTheAlertData() {
        locationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.let { data ->
                        if (teamData != ""){
                            val place = Autocomplete.getPlaceFromIntent(data)
                            setAddress(place.latLng)
                            distance = if (addressListItemAdapter.adsList.isNotEmpty()) {
                                String.format(
                                    "%.2f",
                                    haversine(addressListItemAdapter.adsList[0].lat.toDouble(), addressListItemAdapter.adsList[0].long.toDouble(), place.latLng.latitude,place.latLng.longitude
                                    )
                                )
                            } else {
                                "345"
                            }
                            updateAddress(city, setAddress(place.latLng), distance, lat, long)
                        }
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


    private fun deleteAddress(id: Int){
        if (id == null) return

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete address?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialoag,_->
            sqLiteHelper.deleteAddressById(id)
            getAddress()
            dialoag.dismiss()
        }
        builder.setNegativeButton("No"){dialoag,_->
            dialoag.dismiss()
        }

        val alert = builder.create()
        alert.show()

    }

}