package com.example.adminattendanceapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SavedLocationsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations)

        listView = findViewById(R.id.listLocations)

        // 🔥 REAL-TIME LOAD (FAST UI)
        db.collection("locations")
            .addSnapshotListener { docs, error ->

                if (error != null || docs == null) {
                    Toast.makeText(this, "Failed to load locations", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val locationNames = mutableListOf<String>()

                for (doc in docs) {
                    locationNames.add(doc.id)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    locationNames
                )

                listView.adapter = adapter

                // 🔥 CLICK TO ACTIVATE LOCATION
                listView.setOnItemClickListener { _, _, position, _ ->

                    val selectedLocation = locationNames[position]

                    activateLocation(selectedLocation)
                }
            }
    }

    // 🔥 CORE FUNCTION (MOST IMPORTANT)
    private fun activateLocation(locationName: String) {

        db.collection("locations")
            .document(locationName)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val latitude = doc.getDouble("latitude")
                val longitude = doc.getDouble("longitude")
                val radius = doc.getLong("radius")

                if (latitude == null || longitude == null || radius == null) {
                    Toast.makeText(this, "Invalid location data", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 🔥 WRITE ACTIVE LOCATION (MAIN FIX)
                val activeData = hashMapOf(
                    "name" to locationName,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "radius" to radius
                )

                db.collection("attendance_config")
                    .document("active_location")
                    .set(activeData)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Location Activated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Activation Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }
}