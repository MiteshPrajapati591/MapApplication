package com.example.mapdemoapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapdemoapplication.R
import com.example.mapdemoapplication.model.AddressModel

class AddressListItemAdapter(val onEventListener: (view: View, addressModel: AddressModel)->Unit):RecyclerView.Adapter<AddressListItemAdapter.ViewHolder>() {

    var adsList : ArrayList<AddressModel> = ArrayList()

    fun addItems(items: ArrayList<AddressModel>){
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

            if (pos == 0){
                textViewDistance.text = "Primary"
            }else{
                textViewDistance.text = addressItem.distance
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
}