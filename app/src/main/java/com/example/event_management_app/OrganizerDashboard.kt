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

        val name = intent.getStringExtra("organizer_name") ?: "Organizer"

        val tv = findViewById<TextView>(R.id.tvWelcome)
        val createEventBtn = findViewById<Button>(R.id.createEventBtn)
        val cameraBtn = findViewById<Button>(R.id.btnCamera)

        listView = findViewById(R.id.eventList)

        tv.text = "Hello, $name 👋"

        db = DBHelper(this)

        createEventBtn.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        cameraBtn.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        loadEvents()
    }

    private fun loadEvents() {
        val cursor: Cursor = db.getAllEvents()
        val list = ArrayList<String>()

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                list.add("$title\n$date | $venue")
            } while (cursor.moveToNext())
        }

        cursor.close()

        if (list.isEmpty()) list.add("No events")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }
}