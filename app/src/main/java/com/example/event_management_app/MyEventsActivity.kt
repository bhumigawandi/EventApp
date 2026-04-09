package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MyEventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        val listView = findViewById<ListView>(R.id.listViewEvents)

        val events = arrayListOf(
            "Tech Fest - 20 April",
            "Coding Competition - 25 April",
            "Workshop - Android Dev",
            "Seminar - AI & ML"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, events)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, events[position], Toast.LENGTH_SHORT).show()
        }
    }
}