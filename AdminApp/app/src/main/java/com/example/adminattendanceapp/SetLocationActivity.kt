package com.example.adminattendanceapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SetLocationActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etLat: EditText
    private lateinit var etLng: EditText
    private lateinit var etRadius: EditText
    private lateinit var btnSave: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_location)

        etName = findViewById(R.id.etLocationName)
        etLat = findViewById(R.id.etLatitude)
        etLng = findViewById(R.id.etLongitude)
        etRadius = findViewById(R.id.etRadius)
        btnSave = findViewById(R.id.btnSaveLocation)

        btnSave.setOnClickListener {

            val name = etName.text.toString()
            val lat = etLat.text.toString().toDouble()
            val lng = etLng.text.toString().toDouble()
            val radius = etRadius.text.toString().toLong()

            val data = hashMapOf(
                "name" to name,
                "latitude" to lat,
                "longitude" to lng,
                "radius" to radius
            )

            db.collection("locations")
                .document(name)
                .set(data)
                .addOnSuccessListener {

                    Toast.makeText(this,"Location Saved",Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}