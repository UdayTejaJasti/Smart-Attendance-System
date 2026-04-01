package com.example.adminattendanceapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterStudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)

        val subject = intent.getStringExtra("subject")

        val reg = findViewById<EditText>(R.id.etRegNo)
        val pass = findViewById<EditText>(R.id.etPassword)
        val btn = findViewById<Button>(R.id.btnAdd)

        val db = FirebaseFirestore.getInstance()

        btn.setOnClickListener {

            val regNo = reg.text.toString()
            val password = pass.text.toString()

            val data = hashMapOf(
                "regNo" to regNo,
                "password" to password
            )

            db.collection("subjects")
                .document(subject!!)
                .collection("students")
                .document(regNo)
                .set(data)

            Toast.makeText(this,"Student Registered",Toast.LENGTH_SHORT).show()

            reg.setText("")
            pass.setText("")
        }
    }
}