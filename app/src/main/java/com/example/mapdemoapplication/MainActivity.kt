package com.example.mapdemoapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapdemoapplication.adapter.AddressListItemAdapter
import com.example.mapdemoapplication.model.AddressModel
import com.example.mapdemoapplication.helper.SQLiteHelper
import com.example.mapdemoapplication.databinding.ActivityMainBinding
import com.example.mapdemoapplication.utils.IntentKeys
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

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
    private var valuesUpdate = ""
    private var cityUpdate = ""
    private var distanceUpdate = ""

    private var isUpdate = false

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

        //setupPlacesSdk()
        //initResultLauncher()
    }

    private fun setClick(){
        binding.buttonAddPOIBottom.setOnClickListener {
            isUpdate = false
            val i = Intent(this, SearchActivity::class.java)
            startActivityForResult(i, 1)
        }
        binding.buttonAddPOI.setOnClickListener {
            isUpdate = false
            val i = Intent(this, SearchActivity::class.java)
            startActivityForResult(i, 1)
        }
    }

    private fun getAddress() {
        val adsList = sqLiteHelper.getAllAddress()
        Log.e("pppp","${adsList.size}")

        addressListItemAdapter.addItems(adsList)

        if (adsList.isEmpty()){
            binding.recyclerViewData.visibility = View.GONE
            binding.buttonAddPOIBottom.visibility = View.GONE
            binding.buttonAddPOI.visibility = View.VISIBLE
            binding.textViewMessage.visibility = View.VISIBLE
        }else{
            binding.recyclerViewData.visibility = View.VISIBLE
            binding.buttonAddPOIBottom.visibility = View.VISIBLE
            binding.buttonAddPOI.visibility = View.GONE
            binding.textViewMessage.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {
                values = data?.getStringExtra(IntentKeys.EDITTEXT_VALUE)!!
                city = data.getStringExtra(IntentKeys.CITY)!!
                distance = data.getStringExtra(IntentKeys.DISTANCE)!!
                lat = data.getStringExtra(IntentKeys.LAT)!!
                long = data.getStringExtra(IntentKeys.LONG)!!
                if (values.isNotEmpty()){
                    //binding.textViewAddress.text = values
                    addAddress(city,values,distance,lat,long)
                }

            }
        }
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
                //notify recyclerView here
                getAddress()
            }else{
                Toast.makeText(this, "record not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerViewAddress() {
        binding.recyclerViewData.setHasFixedSize(true)
        binding.recyclerViewData.layoutManager = LinearLayoutManager(this)
        addressListItemAdapter = AddressListItemAdapter(onEventListener = {view, addressModel ->
            when(view.id){
                R.id.imageViewEdit->{

                    /*isUpdate = true
                    val i = Intent(this, SearchActivity::class.java)
                    i.putExtra("UPDATE",true)
                    i.putExtra("ADDRESS",addressModel.addressFiled)
                    i.putExtra("CITY",addressModel.city)
                    i.putExtra("ID",addressModel.id)
                    startActivity(i)*/

                    routeDialog()
                    ads = addressModel
                }
                R.id.imageViewDelete->{
                    deleteAddress(addressModel.id)
                }
            }
        })
        binding.recyclerViewData.adapter = addressListItemAdapter
    }

    private fun updateAddress(mCity: String, mAddressFiled: String, mDistance: String){
        val city = mCity
        val addressFiled = mAddressFiled
        val distance = mDistance

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

    private fun routeDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogViewToken = layoutInflater.inflate(R.layout.update_address_dialog_box, null)
        val buttonUpdate: MaterialButton = dialogViewToken.findViewById(R.id.buttonUpdate) as MaterialButton
        val imageViewClose: AppCompatImageView = dialogViewToken.findViewById(R.id.imageViewDialogClose) as AppCompatImageView
        val editTextSelectLocation: TextInputEditText = dialogViewToken.findViewById(R.id.editTextSelectLocation) as TextInputEditText
        val editTextCity: TextInputEditText = dialogViewToken.findViewById(R.id.editTextCity) as TextInputEditText
        val editTextDistance: TextInputEditText = dialogViewToken.findViewById(R.id.editTextDistance) as TextInputEditText
        dialog.setView(dialogViewToken)
        dialog.setCancelable(true)

        val customDialog = dialog.create()
        customDialog.show()
        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        buttonUpdate.setOnClickListener {
            //updateAddress("cityUpdate",editTextSelectLocation.text.toString(),"distanceUpdate")
            updateAddress(editTextCity.text.toString(),editTextSelectLocation.text.toString(),editTextDistance.text.toString())
            customDialog.dismiss()
        }

        imageViewClose.setOnClickListener {
            customDialog.dismiss()
        }
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


    //find distance
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }


    // for location
    /*private fun setupPlacesSdk(editTextSelectLocation: TextInputEditText) {
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

        editTextSelectLocation.setOnClickListener {
            locationResultLauncher.launch(intent)
        }
    }

    private fun initResultLauncher() {
        locationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.let { data ->
                        val place = Autocomplete.getPlaceFromIntent(data)
                        valuesUpdate = setAddress(place.latLng)
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
                cityUpdate = address.locality
            }
            distanceUpdate = "350km"

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
    }*/

}