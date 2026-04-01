package com.example.adminattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRegisterStudent).setOnClickListener {
            startActivity(Intent(this, RegisterStudentActivity::class.java))
        }

        findViewById<Button>(R.id.btnSetLocation).setOnClickListener {
            startActivity(Intent(this, SetLocationActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewAttendance).setOnClickListener {
            startActivity(Intent(this, AbsenteesList::class.java))
        }
    }
}