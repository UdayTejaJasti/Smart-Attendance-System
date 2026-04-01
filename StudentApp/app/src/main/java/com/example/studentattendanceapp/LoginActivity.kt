package com.example.studentattendanceapp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etReg = findViewById<EditText>(R.id.etReg)
        val etPass = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val regNo = etReg.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (regNo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deviceId = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )

            val deviceRef = db.collection("device_lock").document(deviceId)

            // 🔒 STEP 1: CHECK DEVICE ALREADY USED
            deviceRef.get().addOnSuccessListener { deviceDoc ->

                if (deviceDoc.exists()) {

                    val savedReg = deviceDoc.getString("regNo")

                    if (savedReg != regNo) {

                        Toast.makeText(
                            this,
                            "❌ This device is already used by another student",
                            Toast.LENGTH_LONG
                        ).show()

                        return@addOnSuccessListener
                    }
                }

                // 🔥 STEP 2: CHECK SUBJECTS
                val subjectsList = mutableListOf<String>()
                val studentDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()

                db.collection("subjects")
                    .get()
                    .addOnSuccessListener { subjects ->

                        var checkedCount = 0
                        val totalSubjects = subjects.size()

                        if (totalSubjects == 0) {
                            Toast.makeText(this, "No subjects found", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        for (subjectDoc in subjects) {

                            val subjectName = subjectDoc.id

                            db.collection("subjects")
                                .document(subjectName)
                                .collection("students")
                                .document(regNo)
                                .get()
                                .addOnSuccessListener { studentDoc ->

                                    checkedCount++

                                    if (studentDoc.exists()) {

                                        val dbPass = studentDoc.getString("password")

                                        if (dbPass == password) {
                                            subjectsList.add(subjectName)
                                            studentDocs.add(studentDoc)
                                        }
                                    }

                                    // ✅ AFTER ALL SUBJECTS CHECKED
                                    if (checkedCount == totalSubjects) {

                                        if (subjectsList.isEmpty()) {
                                            Toast.makeText(
                                                this,
                                                "Invalid credentials",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@addOnSuccessListener
                                        }

                                        // 🔒 STEP 3: CHECK DEVICE IN ALL SUBJECT DOCS
                                        for (doc in studentDocs) {

                                            val savedDevice = doc.getString("deviceId")

                                            if (!savedDevice.isNullOrEmpty() && savedDevice != deviceId) {

                                                Toast.makeText(
                                                    this,
                                                    "❌ Account already used on another device",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                return@addOnSuccessListener
                                            }
                                        }

                                        // 💾 STEP 4: SAVE DEVICE IN ALL SUBJECT DOCS
                                        for (doc in studentDocs) {

                                            val savedDevice = doc.getString("deviceId")

                                            if (savedDevice.isNullOrEmpty()) {
                                                doc.reference.update("deviceId", deviceId)
                                            }
                                        }

                                        // 🔐 BIOMETRIC
                                        BiometricHelper.authenticate(this) {

                                            // 🔒 SAVE DEVICE LOCK
                                            deviceRef.set(
                                                mapOf("regNo" to regNo)
                                            )

                                            val intent = Intent(this, SubjectSelectActivity::class.java)
                                            intent.putExtra("regNo", regNo)
                                            intent.putStringArrayListExtra(
                                                "subjects",
                                                ArrayList(subjectsList)
                                            )

                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }
                        }
                    }
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}