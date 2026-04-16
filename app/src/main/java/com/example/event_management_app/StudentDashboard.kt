package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class StudentDashboard : AppCompatActivity() {

    lateinit var db: DBHelper
    lateinit var container: LinearLayout
    lateinit var email: String
    lateinit var name: String

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        db = DBHelper(this)

        NotificationHelper.createChannel(this)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val eventsBtn = findViewById<Button>(R.id.eventsBtn)
        val myEventsBtn = findViewById<Button>(R.id.myEventsBtn)
        val titleText = findViewById<TextView>(R.id.titleText)

        container = findViewById(R.id.eventContainer)

        email = intent.getStringExtra("email") ?: ""
        name = intent.getStringExtra("name") ?: "Student"

        titleText.text = "Welcome, $name 👋"

        loadEvents()

        // ================= LOGOUT =================
        logoutBtn.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->

                    NotificationHelper.showNotification(
                        this,
                        "Logout Successful",
                        "You have been logged out"
                    )

                    mediaPlayer = MediaPlayer.create(this, R.raw.logout_success)
                    mediaPlayer?.start()

                    mediaPlayer?.setOnCompletionListener {
                        it.release()
                        mediaPlayer = null

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        eventsBtn.setOnClickListener {
            loadEvents()
        }

        myEventsBtn.setOnClickListener {
            loadMyEvents()
        }
    }

    // ================= ALL EVENTS =================
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
                tvTitle.setTextColor(Color.parseColor("#111827"))
                tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"
                tvInfo.setTextColor(Color.parseColor("#6B7280"))
                tvInfo.setPadding(0, 6, 0, 12)

                val btnRegister = Button(this)

                if (db.isAlreadyRegistered(eventId, email)) {
                    btnRegister.text = "Registered"
                    btnRegister.isEnabled = false
                    btnRegister.setBackgroundColor(Color.parseColor("#9CA3AF"))
                } else {
                    btnRegister.text = "Register"
                    btnRegister.setBackgroundColor(Color.parseColor("#6366F1"))
                    btnRegister.setTextColor(Color.WHITE)
                }

                btnRegister.setOnClickListener {

                    AlertDialog.Builder(this)
                        .setTitle("Confirm Registration")
                        .setMessage("Do you want to register for this event?")
                        .setPositiveButton("Yes") { _, _ ->

                            val result = db.registerEvent(eventId, email)

                            if (result) {
                                btnRegister.text = "Registered"
                                btnRegister.isEnabled = false
                                btnRegister.setBackgroundColor(Color.parseColor("#9CA3AF"))

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
            empty.setTextColor(Color.GRAY)
            empty.gravity = Gravity.CENTER
            empty.textSize = 16f
            container.addView(empty)
        }

        cursor.close()
    }

    // ================= MY EVENTS =================
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
                tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)

                val tvInfo = TextView(this)
                tvInfo.text = "$date • $venue"
                tvInfo.setPadding(0, 6, 0, 10)

                val status = TextView(this)
                status.text = "Registered ✅"
                status.setTextColor(Color.parseColor("#10B981"))
                status.textSize = 14f

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(status)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Registered Events"
            empty.setTextColor(Color.GRAY)
            empty.gravity = Gravity.CENTER
            container.addView(empty)
        }

        cursor.close()
    }

    // ================= CARD UI =================
    private fun createCard(): LinearLayout {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(24, 24, 24, 24)
        card.setBackgroundColor(Color.WHITE)

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 24)
        card.layoutParams = params

        return card
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}