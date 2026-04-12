package com.example.event_management_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DBHelper(this)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val spinner = findViewById<Spinner>(R.id.spinnerRole)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val roles = arrayOf("Admin", "Organizer", "Student")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        btnLogin.setOnClickListener {

            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()
            val role = spinner.selectedItem.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ADMIN
            if (role == "Admin") {
                if (db.checkAdmin(userEmail, userPassword)) {
                    startActivity(Intent(this, AdminDashboard::class.java))
                } else {
                    Toast.makeText(this, "Invalid Admin credentials", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            // USER
            val valid = db.checkUser(userEmail, userPassword, role)

            if (valid) {
                if (role == "Organizer") {
                    val intent = Intent(this, OrganizerDashboard::class.java)
                    intent.putExtra("organizer_name", userEmail)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, StudentDashboard::class.java)
                    intent.putExtra("email", userEmail)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}