package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
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

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        db = DBHelper(this)

        // 🔔 Required for notifications
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

        // ✅ LOGOUT (alert + notification + audio)
        logoutBtn.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->

                    // 🔔 Notification
                    NotificationHelper.showNotification(
                        this,
                        "Logout Successful",
                        "You have been logged out"
                    )

                    // 🔊 Audio
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}