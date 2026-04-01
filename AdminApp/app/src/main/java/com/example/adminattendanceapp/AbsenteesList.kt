package com.example.adminattendanceapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AbsenteesList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)

        val list = findViewById<ListView>(R.id.listAttendance)
        val btnReset = findViewById<Button>(R.id.btnResetAbsentees)

        val subject = intent.getStringExtra("subject") ?: ""

        val db = FirebaseFirestore.getInstance()

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val presentStudents = mutableListOf<String>()
        val allStudents = mutableListOf<String>()

        // 1️⃣ Get today's attendance for this subject
        db.collection("attendance")
            .whereEqualTo("subject", subject)
            .whereEqualTo("date", today)
            .get()
            .addOnSuccessListener { attendanceDocs ->

                presentStudents.clear()

                for (doc in attendanceDocs) {

                    val reg = doc.getString("regNo")

                    if (reg != null) {
                        presentStudents.add(reg)
                    }
                }

                // 2️⃣ Get students registered for this subject
                db.collection("subjects")
                    .document(subject)
                    .collection("students")
                    .get()
                    .addOnSuccessListener { studentDocs ->

                        allStudents.clear()

                        for (doc in studentDocs) {
                            allStudents.add(doc.id)
                        }

                        // 3️⃣ Compare and find absentees
                        val absentees = allStudents.filter { !presentStudents.contains(it) }

                        val adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            absentees
                        )

                        list.adapter = adapter

                        if (absentees.isEmpty()) {

                            Toast.makeText(
                                this,
                                "All students are present",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }

        // 4️⃣ Reset attendance button
        btnReset.setOnClickListener {

            db.collection("attendance")
                .whereEqualTo("subject", subject)
                .whereEqualTo("date", today)
                .get()
                .addOnSuccessListener { docs ->

                    for (doc in docs) {

                        db.collection("attendance")
                            .document(doc.id)
                            .delete()
                    }

                    Toast.makeText(
                        this,
                        "Attendance reset successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
        }
    }
}