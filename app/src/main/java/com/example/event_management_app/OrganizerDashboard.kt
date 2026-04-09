package com.example.event_management_app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class OrganizerDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_dashboard)

        val createEventBtn = findViewById<LinearLayout>(R.id.createEventBtn)
        val myEventsBtn = findViewById<LinearLayout>(R.id.myEventsBtn)

        createEventBtn.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        myEventsBtn.setOnClickListener {
            startActivity(Intent(this, MyEventsActivity::class.java))
        }
    }
}