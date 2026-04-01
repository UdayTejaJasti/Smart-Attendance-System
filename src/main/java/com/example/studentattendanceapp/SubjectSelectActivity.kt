package com.example.studentattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SubjectSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_subject_select)

        val listView = findViewById<ListView>(R.id.listSubjects)

        val subjects = intent.getStringArrayListExtra("subjects")
        val regNo = intent.getStringExtra("regNo")

        if (subjects == null || regNo == null) {
            Toast.makeText(this, "Error loading subjects", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            subjects
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->

            val selectedSubject = subjects[position]

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("regNo", regNo)
            intent.putExtra("subject", selectedSubject)

            startActivity(intent)
            finish()
        }
    }
}