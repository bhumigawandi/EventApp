package com.example.event_management_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // ✅ STEP 4: Notification setup
        NotificationHelper.createChannel(this)

        // ✅ Ask notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        db = DBHelper(this)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val spinner = findViewById<Spinner>(R.id.spinnerRole)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val roles = arrayOf("Admin", "Organizer", "Student")
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        btnLogin.setOnClickListener {

            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()
            val role = spinner.selectedItem.toString()

            if (userEmail.isEmpty()) {
                email.error = "Enter email"
                return@setOnClickListener
            }

            if (userPassword.isEmpty()) {
                password.error = "Enter password"
                return@setOnClickListener
            }

            Log.d("LOGIN_DEBUG", "Role selected: $role")

            // ADMIN LOGIN
            if (role.equals("Admin", ignoreCase = true)) {

                if (db.checkAdmin(userEmail, userPassword)) {
                    Toast.makeText(this, "Admin Login Successful ✅", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminDashboard::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid Admin credentials", Toast.LENGTH_SHORT).show()
                }

                return@setOnClickListener
            }

            // USER LOGIN
            val valid = db.checkUser(userEmail, userPassword, role)

            if (valid) {

                Toast.makeText(this, "$role Login Successful ✅", Toast.LENGTH_SHORT).show()

                if (role.equals("Organizer", ignoreCase = true)) {

                    val intent = Intent(this, OrganizerDashboard::class.java)
                    intent.putExtra("organizer_name", userEmail)
                    startActivity(intent)

                } else {

                    val intent = Intent(this, StudentDashboard::class.java)
                    intent.putExtra("email", userEmail)
                    startActivity(intent)
                }

                finish()

            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}