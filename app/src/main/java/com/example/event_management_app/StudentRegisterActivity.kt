package com.example.event_management_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StudentRegisterActivity : AppCompatActivity() {

    lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)

        db = DBHelper(this)

        val btn = findViewById<Button>(R.id.btnRegister)

        val nameField = findViewById<EditText>(R.id.name)
        val emailField = findViewById<EditText>(R.id.email)
        val phoneField = findViewById<EditText>(R.id.phone)
        val courseSpinner = findViewById<Spinner>(R.id.spinnerCourse)
        val yearSpinner = findViewById<Spinner>(R.id.spinnerYear)
        val passwordField = findViewById<EditText>(R.id.password)
        val confirmField = findViewById<EditText>(R.id.confirmPassword)

        // =========================
        // ✅ SPINNER DATA (IMPORTANT)
        // =========================

        val courses = arrayOf("Select Course", "IT", "CS", "Mechanical", "Civil")
        val years = arrayOf("Select Year", "FY", "SY", "TY")

        val courseAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = courseAdapter

        val yearAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            years
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // =========================
        // ✅ BUTTON CLICK
        // =========================

        btn.setOnClickListener {

            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val phone = phoneField.text.toString().trim()
            val course = courseSpinner.selectedItem.toString()
            val year = yearSpinner.selectedItem.toString()
            val password = passwordField.text.toString()
            val confirm = confirmField.text.toString()

            // Validation
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirm.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (course == "Select Course" || year == "Select Year") {
                Toast.makeText(this, "Please select course and year", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.insertStudent(name, email, phone, course, year, password)

            if (result) {
                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}