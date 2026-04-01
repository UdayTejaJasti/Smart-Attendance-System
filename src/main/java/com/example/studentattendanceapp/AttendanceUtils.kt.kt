package com.example.studentattendanceapp

import android.location.Location

object AttendanceUtils {

    fun isInsideLocation(
        userLat: Double,
        userLng: Double,
        targetLat: Double,
        targetLng: Double,
        radius: Long
    ): Boolean {

        val user = Location("user")
        user.latitude = userLat
        user.longitude = userLng

        val target = Location("target")
        target.latitude = targetLat
        target.longitude = targetLng

        val distance = user.distanceTo(target)

        return distance <= radius
    }
}