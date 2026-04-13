package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class StudentDashboard : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var container: LinearLayout
    lateinit var email: String
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        db = DBHelper(this)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val eventsBtn = findViewById<Button>(R.id.eventsBtn)
        val myEventsBtn = findViewById<Button>(R.id.myEventsBtn)

        val titleText = findViewById<TextView>(R.id.titleText)

        container = findViewById(R.id.eventContainer)

        email = intent.getStringExtra("email") ?: ""
        name = intent.getStringExtra("name") ?: "Student"

        // ✅ Show student name
        titleText.text = "Welcome, $name 👋"

        // Default load
        loadEvents()

        // LOGOUT
        logoutBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // ALL EVENTS
        eventsBtn.setOnClickListener {
            loadEvents()
        }

        // MY EVENTS
        myEventsBtn.setOnClickListener {
            loadMyEvents()
        }
    }

    // 🔹 ALL EVENTS
    private fun loadEvents() {

        container.removeAllViews()

        val cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {
                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = createCard()

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 18f

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"

                val btnRegister = Button(this)

                if (db.isAlreadyRegistered(eventId, email)) {
                    btnRegister.text = "Registered"
                    btnRegister.isEnabled = false
                    btnRegister.setBackgroundColor(0xFF9CA3AF.toInt())
                } else {
                    btnRegister.text = "Register"
                    btnRegister.setBackgroundColor(0xFF6366F1.toInt())
                    btnRegister.setTextColor(0xFFFFFFFF.toInt())
                }

                // ✅ POPUP CONFIRMATION
                btnRegister.setOnClickListener {

                    AlertDialog.Builder(this)
                        .setTitle("Confirm Registration")
                        .setMessage("Do you want to register for this event?")
                        .setPositiveButton("Yes") { _, _ ->

                            val result = db.registerEvent(eventId, email)

                            if (result) {
                                btnRegister.text = "Registered"
                                btnRegister.isEnabled = false
                                btnRegister.setBackgroundColor(0xFF9CA3AF.toInt())

                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Already Registered", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
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

    // 🔹 MY EVENTS (REGISTERED ONLY)
    private fun loadMyEvents() {

        container.removeAllViews()

        val cursor = db.getMyEvents(email)

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = createCard()

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 18f

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"

                val status = TextView(this)
                status.text = "Registered ✅"
                status.setTextColor(0xFF10B981.toInt())

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(status)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Registered Events"
            container.addView(empty)
        }

        cursor.close()
    }

    // 🔹 CARD DESIGN (Reusable)
    private fun createCard(): LinearLayout {

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

        return card
    }
}