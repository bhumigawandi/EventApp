package com.example.event_management_app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class HomepageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val login = findViewById<LinearLayout>(R.id.loginCard)
        val student = findViewById<LinearLayout>(R.id.studentCard)
        val organizer = findViewById<LinearLayout>(R.id.organizerCard)

        login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        student.setOnClickListener {
            startActivity(Intent(this, StudentRegisterActivity::class.java))
        }

        organizer.setOnClickListener {
            startActivity(Intent(this, OrganizerRegisterActivity::class.java))
        }
    }
}