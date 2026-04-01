package com.example.adminattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SubjectListActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_subject_list)

        val list=findViewById<ListView>(R.id.listSubjects)
        val btnAdd=findViewById<Button>(R.id.btnAddSubject)

        val db=FirebaseFirestore.getInstance()

        btnAdd.setOnClickListener {

            startActivity(Intent(this,RegisterSubjectActivity::class.java))

        }

        db.collection("subjects").get().addOnSuccessListener{docs->

            val names=docs.map{it.id}

            val adapter=ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                names
            )

            list.adapter=adapter

            list.setOnItemClickListener{_,_,pos,_->

                val subject=names[pos]

                val i=Intent(this,SubjectDashboardActivity::class.java)
                i.putExtra("subject",subject)
                startActivity(i)

            }

        }
    }
}