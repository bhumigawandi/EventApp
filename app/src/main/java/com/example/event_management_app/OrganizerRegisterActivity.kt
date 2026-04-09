package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OrganizerRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_register)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val event = findViewById<EditText>(R.id.event)
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val password = findViewById<EditText>(R.id.password)
        val confirm = findViewById<EditText>(R.id.confirmPassword)
        val btn = findViewById<Button>(R.id.btnRegister)

        val db = DBHelper(this)

        // =========================
        // ✅ SPINNER DATA
        // =========================
        val categories = arrayOf(
            "Select Category",
            "Technical",
            "Cultural",
            "Sports",
            "Workshop",
            "Seminar"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // =========================
        // ✅ REGISTER BUTTON
        // =========================
        btn.setOnClickListener {

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val eventText = event.text.toString().trim()
            val category = spinner.selectedItem.toString()
            val passwordText = password.text.toString().trim()
            val confirmText = confirm.text.toString().trim()

            // 🔴 Validation
            if (nameText.isEmpty() || emailText.isEmpty() || phoneText.isEmpty()
                || eventText.isEmpty() || passwordText.isEmpty() || confirmText.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (category == "Select Category") {
                Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText != confirmText) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 SAVE TO DATABASE (IMPORTANT)
            val result = db.insertOrganizer(nameText, emailText, passwordText)

            if (result) {
                Toast.makeText(this, "Organizer Registered Successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}