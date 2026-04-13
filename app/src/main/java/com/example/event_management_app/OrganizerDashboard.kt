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
        val eventIds = ArrayList<Int>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                list.add("$title\n$date | $venue")
                eventIds.add(id)

            } while (cursor.moveToNext())
        }

        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter

        // 🔥 CLICK EVENT
        listView.setOnItemClickListener { _, _, position, _ ->

            val eventId = eventIds[position]

            val intent = Intent(this, StudentListActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        loadEvents()
    }
}