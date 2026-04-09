package com.example.event_management_app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val login = findViewById<LinearLayout>(R.id.loginCard)
        val student = findViewById<LinearLayout>(R.id.studentCard)
        val organizer = findViewById<LinearLayout>(R.id.organizerCard)

        login.setOnClickListener {
            Toast.makeText(this, "Login Clicked", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, LoginActivity::class.java))
        }

        student.setOnClickListener {
            Toast.makeText(this, "Student Register Clicked", Toast.LENGTH_SHORT).show()
        }

        organizer.setOnClickListener {
            Toast.makeText(this, "Organizer Register Clicked", Toast.LENGTH_SHORT).show()
        }
        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        student.setOnClickListener {
            val intent = Intent(this, StudentRegisterActivity::class.java)
            startActivity(intent)
        }

        organizer.setOnClickListener {
            val intent = Intent(this, OrganizerRegisterActivity::class.java)
            startActivity(intent)
        }
    }
}