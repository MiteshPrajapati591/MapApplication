package com.example.mapdemoapplication.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mapdemoapplication.model.AddressModel

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "address.db"
        const val TBL_ADDRESS = "tbl_address"
        private const val ID = "id"
        private const val CITY = "city"
        private const val ADDRESS_FILED = "addressFiled"
        private const val DISTANCE = "distance"
        private const val LAT = "lat"
        private const val LONG = "long"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableAddress = ("CREATE TABLE " + TBL_ADDRESS + "("
                + ID + " INTEGER PRIMARY KEY," + CITY + " TEXT," + ADDRESS_FILED + " TEXT,"
                + DISTANCE + " TEXT," + LAT + " TEXT," + LONG + " TEXT" + ")")

        db?.execSQL(createTableAddress)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_ADDRESS")
        onCreate(db)
    }

    fun insertAddress(ads: AddressModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID,ads.id)
        contentValues.put(CITY,ads.city)
        contentValues.put(ADDRESS_FILED,ads.addressFiled)
        contentValues.put(DISTANCE,ads.distance)
        contentValues.put(LAT,ads.lat)
        contentValues.put(LONG,ads.long)

        val success = db.insert(TBL_ADDRESS, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllAddress(): ArrayList<AddressModel>{
        val adsList : ArrayList<AddressModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_ADDRESS"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id:Int
        var city:String
        var addressFiled:String
        var distance:String
        var lat:String
        var long:String

        if (cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                city = cursor.getString(cursor.getColumnIndex("city"))
                addressFiled = cursor.getString(cursor.getColumnIndex("addressFiled"))
                distance = cursor.getString(cursor.getColumnIndex("distance"))
                lat = cursor.getString(cursor.getColumnIndex("lat"))
                long = cursor.getString(cursor.getColumnIndex("long"))

                val ads = AddressModel(id = id, city = city, addressFiled = addressFiled, distance = distance, lat = lat?:"", long = long?:"")
                adsList.add(ads)

            }while (cursor.moveToNext())
        }
        return adsList
    }


    fun updateAddress(ads: AddressModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID,ads.id)
        contentValues.put(CITY,ads.city)
        contentValues.put(ADDRESS_FILED,ads.addressFiled)
        contentValues.put(DISTANCE,ads.distance)
        contentValues.put(LAT,ads.lat)
        contentValues.put(LONG,ads.long)

        val success = db.update(TBL_ADDRESS, contentValues,"id=" + ads.id, null)
        db.close()
        return success
    }

    fun deleteAddressById(id: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_ADDRESS, "id=$id", null)
        db.close()

        return success
    }
}