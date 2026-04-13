package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class OrganizerDashboard : AppCompatActivity() {

    lateinit var container: LinearLayout
    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_dashboard)

        // 🔔 Notification channel
        NotificationHelper.createChannel(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val name = intent.getStringExtra("organizer_name") ?: "Organizer"

        val tv = findViewById<TextView>(R.id.tvWelcome)
        val createEventBtn = findViewById<Button>(R.id.createEventBtn)
        val myEventsBtn = findViewById<Button>(R.id.myEventsBtn)
        val cameraBtn = findViewById<Button>(R.id.btnCamera)

        container = findViewById(R.id.mainContainer)

        tv.text = "Hello, $name 👋"

        db = DBHelper(this)

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

        loadEvents()
    }

    // 🔹 MENU
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return true
    }

    // 🔹 Show events + students
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
                card.elevation = 8f

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
                studentList.setPadding(0, 10, 0, 0)

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

                    NotificationHelper.showNotification(
                        this,
                        "Students Loaded",
                        "Showing registered students"
                    )
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

    // 🔹 My Events (full details)
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
                card.elevation = 8f

                val tvTitle = TextView(this)
                tvTitle.text = "Title: $title"
                tvTitle.textSize = 18f

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
}