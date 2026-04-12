package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OrganizerDashboard : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_dashboard)

        val name = intent.getStringExtra("organizer_name")

        val tv = findViewById<TextView>(R.id.tvWelcome)
        tv.text = "Hello, $name 👋"

        val createEventBtn = findViewById<LinearLayout>(R.id.createEventBtn)
        val myEventsBtn = findViewById<LinearLayout>(R.id.myEventsBtn)

        listView = findViewById(R.id.eventList)
        db = DBHelper(this)

        // Create Event
        createEventBtn.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        // Optional
        myEventsBtn.setOnClickListener {
            Toast.makeText(this, "Showing all events below", Toast.LENGTH_SHORT).show()
        }

        loadEvents()
    }

    // ✅ Load Events from DB (FIXED)
    private fun loadEvents() {

        val cursor: Cursor = db.getAllEvents()
        val list = ArrayList<String>()

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status")) // ✅ FIXED

                list.add("$title\n$date | $venue\nStatus: $status")

            } while (cursor.moveToNext())
        }

        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }
}