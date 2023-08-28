package com.example.mapdemoapplication.model

import java.util.*

data class AddressModel(
    var id: Int = getAutoId(),
    var city: String = "",
    var addressFiled: String = "",
    var distance: String = "",
    var lat: String = "",
    var long: String = "",
    )
{

    companion object{
        fun getAutoId():Int{
            val random = Random()
            return random.nextInt(100)
        }
    }
}