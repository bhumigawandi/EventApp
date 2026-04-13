package com.example.event_management_app

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentDashboard : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var container: LinearLayout
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        db = DBHelper(this)

        val logoutBtn = findViewById<TextView>(R.id.logoutBtn)
        container = findViewById(R.id.eventContainer)

        email = intent.getStringExtra("email") ?: ""

        loadEvents()

        logoutBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadEvents() {

        container.removeAllViews()

        val cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {
                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title")) // ✅ FIX
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(20, 20, 20, 20)
                card.setBackgroundColor(0xFFFFFFFF.toInt())

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 18f

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"

                val btnRegister = Button(this)

                if (db.isAlreadyRegistered(eventId, email)) {
                    btnRegister.text = "Registered"
                    btnRegister.isEnabled = false
                } else {
                    btnRegister.text = "Register"
                }

                btnRegister.setOnClickListener {
                    val result = db.registerEvent(eventId, email)

                    if (result) {
                        btnRegister.text = "Registered"
                        btnRegister.isEnabled = false
                        Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Already Registered", Toast.LENGTH_SHORT).show()
                    }
                }

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(btnRegister)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Events Available"
            container.addView(empty)
        }

        cursor.close()
    }
}