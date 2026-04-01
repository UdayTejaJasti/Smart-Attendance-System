package com.example.adminattendanceapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterSubjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register_subject)

        val et = findViewById<EditText>(R.id.etSubject)
        val btn = findViewById<Button>(R.id.btnSave)

        val db = FirebaseFirestore.getInstance()

        btn.setOnClickListener {

            val subject = et.text.toString().trim()

            if (subject.isEmpty()) {
                Toast.makeText(this, "Enter subject name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("subjects")
                .document(subject)
                .get()
                .addOnSuccessListener { doc ->

                    if (doc.exists()) {
                        Toast.makeText(this, "Subject already exists", Toast.LENGTH_SHORT).show()
                    } else {

                        val data = hashMapOf(
                            "name" to subject
                        )

                        db.collection("subjects")
                            .document(subject)
                            .set(data)

                        Toast.makeText(this, "Subject Registered", Toast.LENGTH_SHORT).show()

                        et.setText("")
                    }
                }
        }
    }
}