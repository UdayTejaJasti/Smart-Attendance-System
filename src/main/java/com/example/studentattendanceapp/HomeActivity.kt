package com.example.studentattendanceapp

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var txtWelcome: TextView
    private lateinit var btnMarkAttendance: Button
    private lateinit var btnLogout: Button

    private val db = FirebaseFirestore.getInstance()

    private var currentRegNo: String? = null
    private var currentSubject: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txtWelcome = findViewById(R.id.txtWelcome)
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance)
        btnLogout = findViewById(R.id.btnLogout)

        currentRegNo = intent.getStringExtra("regNo")
        currentSubject = intent.getStringExtra("subject")

        txtWelcome.text = "Welcome $currentRegNo"

        btnMarkAttendance.setOnClickListener {

            if (currentRegNo == null || currentSubject == null) {
                Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkAttendanceSession(currentRegNo!!, currentSubject!!)
        }

        btnLogout.setOnClickListener {

            val i = Intent(this, LoginActivity::class.java)

            // 🔥 Clear all previous screens
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(i)
        }
    }

    // 🔥 FAST + REALTIME SESSION CHECK
    private fun checkAttendanceSession(regNo: String, subject: String) {

        db.collection("attendance_session")
            .document("session")
            .get()   // ⚡ keep GET to avoid multiple triggers
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    Toast.makeText(this, "Attendance not started", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val status = doc.getString("status")
                val activeSubject = doc.getString("subject")

                if (status != "started" || activeSubject != subject) {
                    Toast.makeText(this, "Attendance not started for this subject", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val lat = doc.getDouble("latitude")
                val lng = doc.getDouble("longitude")
                val radius = doc.getLong("radius")

                if (lat == null || lng == null || radius == null) {
                    Toast.makeText(this, "Location not configured", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                getStudentLocation(regNo, subject, lat, lng, radius)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔥 FAST LOCATION FIX (IMPORTANT)
    private fun getStudentLocation(
        regNo: String,
        subject: String,
        adminLat: Double,
        adminLng: Double,
        radius: Long
    ) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        // 🚀 BEST METHOD (FASTER + ACCURATE)
        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location == null) {
                Toast.makeText(this, "Turn ON GPS", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val studentLocation = Location("student")
            studentLocation.latitude = location.latitude
            studentLocation.longitude = location.longitude

            val adminLocation = Location("admin")
            adminLocation.latitude = adminLat
            adminLocation.longitude = adminLng

            val distance = studentLocation.distanceTo(adminLocation)

            if (distance <= radius) {

                markAttendance(regNo, subject)

            } else {

                Toast.makeText(
                    this,
                    "Outside location (${distance.toInt()} m)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // 🔥 SAFE ATTENDANCE MARKING
    private fun markAttendance(regNo: String, subject: String) {

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val docId = subject + "_" + regNo + "_" + today

        val data = hashMapOf(
            "regNo" to regNo,
            "subject" to subject,
            "date" to today
        )

        db.collection("attendance")
            .document(docId)
            .get()
            .addOnSuccessListener { existing ->

                if (existing.exists()) {

                    Toast.makeText(
                        this,
                        "Already marked today",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    db.collection("attendance")
                        .document(docId)
                        .set(data)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Attendance marked successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {

                            Toast.makeText(
                                this,
                                "Failed to mark attendance",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }
}