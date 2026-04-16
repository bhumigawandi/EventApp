package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer   // ✅ added
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class OrganizerDashboard : AppCompatActivity() {

    lateinit var container: LinearLayout
    lateinit var db: DBHelper

    private var mediaPlayer: MediaPlayer? = null   // ✅ added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_dashboard)

        NotificationHelper.createChannel(this)

        val name = intent.getStringExtra("organizer_name") ?: "Organizer"

        val tv = findViewById<TextView>(R.id.tvWelcome)
        val createEventBtn = findViewById<Button>(R.id.createEventBtn)
        val myEventsBtn = findViewById<Button>(R.id.myEventsBtn)
        val cameraBtn = findViewById<Button>(R.id.btnCamera)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val allStudentsBtn = findViewById<Button>(R.id.allStudentsBtn)

        container = findViewById(R.id.mainContainer)

        tv.text = "Hello, $name 👋"

        db = DBHelper(this)

        // 🔴 LOGOUT (only additions done)
        logoutBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->

                    // 🔔 Notification added
                    NotificationHelper.showNotification(
                        this,
                        "Logout Successful",
                        "You have been logged out"
                    )

                    // 🔊 Audio added
                    mediaPlayer = MediaPlayer.create(this, R.raw.logout_success)
                    mediaPlayer?.start()

                    mediaPlayer?.setOnCompletionListener {
                        it.release()
                        mediaPlayer = null

                        // original logic (unchanged)
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        createEventBtn.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))

            NotificationHelper.showNotification(
                this,
                "Create Event",
                "Opening event creation screen"
            )
        }

        cameraBtn.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        myEventsBtn.setOnClickListener {
            loadMyEvents()
        }

        allStudentsBtn.setOnClickListener {
            loadAllStudents()
        }

        loadEvents()
    }

    private fun loadEvents() {

        container.removeAllViews()

        val cursor: Cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {
                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)

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
                tvInfo.text = "$date | $venue"

                val btnView = Button(this)
                btnView.text = "View Students"

                val studentList = TextView(this)

                btnView.setOnClickListener {

                    val studentCursor = db.getStudentsForEvent(eventId)

                    val data = StringBuilder()

                    if (studentCursor.moveToFirst()) {
                        do {
                            val name = studentCursor.getString(0)
                            val email = studentCursor.getString(1)
                            data.append("• $name ($email)\n")
                        } while (studentCursor.moveToNext())
                    } else {
                        data.append("No students registered")
                    }

                    studentCursor.close()
                    studentList.text = data.toString()
                }

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(btnView)
                card.addView(studentList)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No events"
            container.addView(empty)
        }

        cursor.close()
    }

    private fun loadMyEvents() {

        container.removeAllViews()

        val cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))
                val max = cursor.getString(cursor.getColumnIndexOrThrow("maxParticipants"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundResource(android.R.drawable.dialog_holo_light_frame)

                val tvTitle = TextView(this)
                tvTitle.text = "Title: $title"

                val tvCategory = TextView(this)
                tvCategory.text = "Category: $category"

                val tvDesc = TextView(this)
                tvDesc.text = "Description: $description"

                val tvDate = TextView(this)
                tvDate.text = "Date: $date"

                val tvTime = TextView(this)
                tvTime.text = "Time: $time"

                val tvVenue = TextView(this)
                tvVenue.text = "Venue: $venue"

                val tvMax = TextView(this)
                tvMax.text = "Max Participants: $max"

                card.addView(tvTitle)
                card.addView(tvCategory)
                card.addView(tvDesc)
                card.addView(tvDate)
                card.addView(tvTime)
                card.addView(tvVenue)
                card.addView(tvMax)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No events found"
            container.addView(empty)
        }

        cursor.close()
    }

    private fun loadAllStudents() {

        container.removeAllViews()

        val cursor = db.getRegisteredEventsForOrganizer()

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0)
                val date = cursor.getString(1)
                val venue = cursor.getString(2)
                val email = cursor.getString(3)

                val tv = TextView(this)
                tv.text = "Event: $title\n$date | $venue\nStudent: $email\n"
                tv.setPadding(10, 10, 10, 20)

                container.addView(tv)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No student registrations"
            container.addView(empty)
        }

        cursor.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}