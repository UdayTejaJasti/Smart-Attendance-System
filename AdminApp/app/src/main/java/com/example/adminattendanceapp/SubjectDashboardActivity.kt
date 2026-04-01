package com.example.adminattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SubjectDashboardActivity : AppCompatActivity() {

    lateinit var txtTotal: TextView
    lateinit var txtPresent: TextView
    lateinit var txtAbsent: TextView

    lateinit var btnRegisterStudents: Button
    lateinit var btnSetLocation: Button
    lateinit var btnSavedLocations: Button
    lateinit var btnStartAttendance: Button
    lateinit var btnStopAttendance: Button
    lateinit var btnAbsentees: Button
    lateinit var btnLogout: Button

    val db = FirebaseFirestore.getInstance()
    lateinit var subject: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_dashboard)

        subject = intent.getStringExtra("subject")!!

        txtTotal = findViewById(R.id.txtTotal)
        txtPresent = findViewById(R.id.txtPresent)
        txtAbsent = findViewById(R.id.txtAbsent)

        btnRegisterStudents = findViewById(R.id.btnRegisterStudents)
        btnSetLocation = findViewById(R.id.btnSetLocation)
        btnSavedLocations = findViewById(R.id.btnSavedLocations)
        btnStartAttendance = findViewById(R.id.btnStartAttendance)
        btnStopAttendance = findViewById(R.id.btnStopAttendance)
        btnAbsentees = findViewById(R.id.btnAbsentees)
        btnLogout = findViewById(R.id.btnLogout)

        // 🔥 REAL-TIME STATISTICS
        loadStatisticsRealtime()

        btnRegisterStudents.setOnClickListener {
            startActivity(Intent(this, RegisterStudentActivity::class.java)
                .putExtra("subject", subject))
        }

        btnSetLocation.setOnClickListener {
            startActivity(Intent(this, SetLocationActivity::class.java))
        }

        btnSavedLocations.setOnClickListener {
            startActivity(Intent(this, SavedLocationsActivity::class.java))
        }

        btnAbsentees.setOnClickListener {
            startActivity(Intent(this, AbsenteesList::class.java)
                .putExtra("subject", subject))
        }

        // START ATTENDANCE
        btnStartAttendance.setOnClickListener {

            db.collection("attendance_config")
                .document("active_location")
                .get()
                .addOnSuccessListener { doc ->

                    if (!doc.exists()) {
                        Toast.makeText(this, "Activate location first", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val lat = doc.getDouble("latitude")
                    val lng = doc.getDouble("longitude")
                    val radius = doc.getLong("radius")

                    val data = hashMapOf(
                        "status" to "started",
                        "subject" to subject,
                        "latitude" to lat,
                        "longitude" to lng,
                        "radius" to radius
                    )

                    db.collection("attendance_session")
                        .document("session")
                        .set(data)

                    Toast.makeText(this, "Attendance Started", Toast.LENGTH_SHORT).show()
                }
        }

        // STOP ATTENDANCE
        btnStopAttendance.setOnClickListener {
            db.collection("attendance_session")
                .document("session")
                .update("status", "stopped")

            Toast.makeText(this, "Attendance Stopped", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener { finish() }
    }

    // 🔥 REAL-TIME FUNCTION
    private fun loadStatisticsRealtime() {

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("subjects")
            .document(subject)
            .collection("students")
            .addSnapshotListener { students, _ ->

                if (students == null) return@addSnapshotListener

                val total = students.size()
                txtTotal.text = "Total\n$total"

                db.collection("attendance")
                    .whereEqualTo("subject", subject)
                    .whereEqualTo("date", today)
                    .addSnapshotListener { attendance, _ ->

                        if (attendance == null) return@addSnapshotListener

                        val present = attendance.size()
                        val absent = total - present

                        txtPresent.text = "Present\n$present"
                        txtAbsent.text = "Absent\n$absent"
                    }
            }
    }
}