package com.example.mapdemoapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapdemoapplication.R
import com.example.mapdemoapplication.firstName
import com.example.mapdemoapplication.model.AddressModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AddressListItemAdapter(var adsList : ArrayList<AddressModel>,val onEventListener: (view: View, addressModel: AddressModel)->Unit):RecyclerView.Adapter<AddressListItemAdapter.ViewHolder>() {

    fun addItems(items: ArrayList<AddressModel>){
        this.adsList.clear()
        this.adsList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressListItemAdapter.ViewHolder {
         val itemView = LayoutInflater.from(parent.context)
                         .inflate(R.layout.address_list_item, parent, false)
                 return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddressListItemAdapter.ViewHolder, position: Int) {
        holder.bind(adsList[position],position)

    }

    override fun getItemCount(): Int {
        return adsList.size
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view.rootView){

        private val textViewCity = view.findViewById<AppCompatTextView>(R.id.textViewCity)
        private val textViewAddress = view.findViewById<AppCompatTextView>(R.id.textViewAddress)
        private val textViewDistance = view.findViewById<AppCompatTextView>(R.id.textViewDistance)
        private var imageViewDelete = view.findViewById<AppCompatImageView>(R.id.imageViewDelete)
        private var imageViewEdit = view.findViewById<AppCompatImageView>(R.id.imageViewEdit)

        fun bind(addressItem: AddressModel,pos: Int) {
            textViewCity.text = addressItem.city
            textViewAddress.text = addressItem.addressFiled

            if (firstName == adsList[pos].city){
                textViewDistance.text = "Primary"
            }else{
                //textViewDistance.text = "Distance is :".plus(addressItem.distance).plus(" KM")
                for (i in 0 until adsList.size){
                    textViewDistance.text = "Distance is :".plus(String.format("%.2f", haversine(addressItem.lat[0].toDouble(), addressItem.long[0].toDouble(),addressItem.lat[i].toDouble(),addressItem.long[i].toDouble()))).toString().plus(" KM")
                }
            }
        }

        init {

            imageViewDelete.setOnClickListener {
                onEventListener(it,adsList[adapterPosition])
            }
            imageViewEdit.setOnClickListener {
                onEventListener(it,adsList[adapterPosition])
            }
        }
    }

    //find distance
    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }
}