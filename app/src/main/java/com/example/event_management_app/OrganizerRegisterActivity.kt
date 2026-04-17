package com.example.event_management_app

import android.os.Bundle
import android.util.Patterns
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

        btn.setOnClickListener {

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val eventText = event.text.toString().trim()
            val category = spinner.selectedItem.toString()
            val passwordText = password.text.toString().trim()
            val confirmText = confirm.text.toString().trim()

            // ✅ NAME
            if (nameText.isEmpty()) {
                name.error = "Enter name"
                name.requestFocus()
                return@setOnClickListener
            }

            // ✅ EMAIL
            if (emailText.isEmpty()) {
                email.error = "Enter email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Invalid email"
                email.requestFocus()
                return@setOnClickListener
            }

            // ✅ PHONE
            if (phoneText.isEmpty()) {
                phone.error = "Enter phone"
                phone.requestFocus()
                return@setOnClickListener
            }

            if (phoneText.length != 10) {
                phone.error = "Enter valid 10-digit phone"
                phone.requestFocus()
                return@setOnClickListener
            }

            // ✅ EVENT
            if (eventText.isEmpty()) {
                event.error = "Enter event name"
                event.requestFocus()
                return@setOnClickListener
            }

            // ✅ CATEGORY
            if (category == "Select Category") {
                Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ PASSWORD
            if (passwordText.isEmpty()) {
                password.error = "Enter password"
                password.requestFocus()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                password.error = "Minimum 6 characters"
                password.requestFocus()
                return@setOnClickListener
            }

            // ✅ CONFIRM PASSWORD
            if (confirmText.isEmpty()) {
                confirm.error = "Confirm password"
                confirm.requestFocus()
                return@setOnClickListener
            }

            if (passwordText != confirmText) {
                confirm.error = "Passwords do not match"
                confirm.requestFocus()
                return@setOnClickListener
            }

            // 🔥 SAVE
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