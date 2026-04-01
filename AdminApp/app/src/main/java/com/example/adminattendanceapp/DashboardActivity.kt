package com.example.adminattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var txtTotal: TextView
    private lateinit var txtPresent: TextView
    private lateinit var txtAbsent: TextView

    private lateinit var btnRegisterStudents: Button
    private lateinit var btnStartAttendance: Button
    private lateinit var btnStopAttendance: Button
    private lateinit var btnAbsentees: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_dashboard)

        txtTotal = findViewById(R.id.txtTotal)
        txtPresent = findViewById(R.id.txtPresent)
        txtAbsent = findViewById(R.id.txtAbsent)

        btnRegisterStudents = findViewById(R.id.btnRegisterStudents)
        btnStartAttendance = findViewById(R.id.btnStartAttendance)
        btnStopAttendance = findViewById(R.id.btnStopAttendance)
        btnAbsentees = findViewById(R.id.btnAbsentees)
        btnLogout = findViewById(R.id.btnLogout)

        btnRegisterStudents.setOnClickListener {
            startActivity(Intent(this, RegisterStudentActivity::class.java))
        }

        btnAbsentees.setOnClickListener {
            startActivity(Intent(this, AbsenteesList::class.java))
        }

        btnLogout.setOnClickListener {
            finish()
        }
    }
}