package com.esteban.appcomprayventa

import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

object Constants {

    fun timeToDevice() : Long {
      return System.currentTimeMillis()
    }

    fun getDate(time: Long) : String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = time
        return android.text.format.DateFormat.format("dd/MM/yyyy",calendar).toString()
    }
}