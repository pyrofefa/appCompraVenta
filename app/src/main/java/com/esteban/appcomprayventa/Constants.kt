package com.esteban.appcomprayventa

import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

object Constants {

    const val adsAvailable = "available"
    const val adsSold = "Sold"

    var categories = arrayOf(
        "Mobile",
        "Computer",
        "Vehicles",
        "Book",
        "Sports"
    )

    var conditios = arrayOf(
        "New",
        "Used",
        "Renewed"
    )

    fun timeToDevice() : Long {
      return System.currentTimeMillis()
    }

    fun getDate(time: Long) : String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = time
        return android.text.format.DateFormat.format("dd/MM/yyyy",calendar).toString()
    }
}