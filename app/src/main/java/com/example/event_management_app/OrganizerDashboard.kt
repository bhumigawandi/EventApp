package com.example.event_management_app

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class OrganizerDashboard : AppCompatActivity() {

    lateinit var container: LinearLayout
    lateinit var db: DBHelper
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_dashboard)

        val tv = findViewById<TextView>(R.id.tvWelcome)
        val createBtn = findViewById<Button>(R.id.createEventBtn)
        val myEventsBtn = findViewById<Button>(R.id.myEventsBtn)
        val allStudentsBtn = findViewById<Button>(R.id.allStudentsBtn)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        container = findViewById(R.id.mainContainer)
        db = DBHelper(this)

        tv.text = "Hello Organizer 👋"

        // LOGOUT
        logoutBtn.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->

                    mediaPlayer = MediaPlayer.create(this, R.raw.logout_success)
                    mediaPlayer?.start()

                    mediaPlayer?.setOnCompletionListener {
                        it.release()
                        mediaPlayer = null

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        createBtn.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        myEventsBtn.setOnClickListener {
            loadMyEvents()
        }

        allStudentsBtn.setOnClickListener {
            loadAllStudents()
        }
    }

    // ================= EVENTS =================
    private fun loadMyEvents() {

        container.removeAllViews()

        val cursor: Cursor = db.getAllEvents()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"))

                val timeVal = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                val rulesVal = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val guidelinesVal = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val maxVal = cursor.getString(cursor.getColumnIndexOrThrow("maxParticipants"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                val tvTitle = TextView(this)
                tvTitle.text = "Title: $title"
                tvTitle.setTextColor(Color.BLACK)

                val tvInfo = TextView(this)
                tvInfo.text = "$date | $venue"
                tvInfo.setTextColor(Color.DKGRAY)

                val btnLayout = LinearLayout(this)

                val editBtn = Button(this)
                editBtn.text = "Edit"

                editBtn.setOnClickListener {

                    val dialogView = layoutInflater.inflate(R.layout.dialog_edit_event, null)

                    val etDate = dialogView.findViewById<EditText>(R.id.etDate)
                    val etTime = dialogView.findViewById<EditText>(R.id.etTime)
                    val etVenue = dialogView.findViewById<EditText>(R.id.etVenue)
                    val etRules = dialogView.findViewById<EditText>(R.id.etRules)
                    val etGuidelines = dialogView.findViewById<EditText>(R.id.etGuidelines)

                    etDate.setText(date)
                    etTime.setText(timeVal)
                    etVenue.setText(venue)
                    etRules.setText(rulesVal)
                    etGuidelines.setText(guidelinesVal)

                    etDate.setOnClickListener {
                        val c = Calendar.getInstance()
                        val dp = android.app.DatePickerDialog(
                            this,
                            { _, y, m, d -> etDate.setText("$d-${m + 1}-$y") },
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                        )
                        dp.show()
                    }

                    etTime.setOnClickListener {
                        val c = Calendar.getInstance()
                        val tp = android.app.TimePickerDialog(
                            this,
                            { _, h, m ->
                                val ampm = if (h < 12) "AM" else "PM"
                                val hour12 = if (h % 12 == 0) 12 else h % 12
                                val min = if (m < 10) "0$m" else "$m"
                                etTime.setText("$hour12:$min $ampm")
                            },
                            c.get(Calendar.HOUR_OF_DAY),
                            c.get(Calendar.MINUTE),
                            false
                        )
                        tp.show()
                    }

                    AlertDialog.Builder(this)
                        .setTitle("Edit Event")
                        .setView(dialogView)
                        .setPositiveButton("Update") { _, _ ->

                            db.updateEvent(
                                id,
                                title,
                                etGuidelines.text.toString(),
                                etRules.text.toString(),
                                etDate.text.toString(),
                                etTime.text.toString(),
                                etVenue.text.toString(),
                                maxVal
                            )

                            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                            loadMyEvents()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                val deleteBtn = Button(this)
                deleteBtn.text = "Delete"

                deleteBtn.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Delete Event")
                        .setMessage("Delete this event?")
                        .setPositiveButton("Yes") { _, _ ->
                            db.deleteEvent(id)
                            loadMyEvents()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                btnLayout.addView(editBtn)
                btnLayout.addView(deleteBtn)

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(btnLayout)

                container.addView(card)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    // ================= ALL STUDENTS (FIXED) =================
    private fun loadAllStudents() {

        container.removeAllViews()

        val cursor = db.getRegisteredEventsForOrganizer()

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0)
                val date = cursor.getString(1)
                val venue = cursor.getString(2)
                val email = cursor.getString(3)

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                val tvTitle = TextView(this)
                tvTitle.text = "Event: $title"
                tvTitle.setTextColor(Color.BLACK)

                val tvDate = TextView(this)
                tvDate.text = "Date: $date"

                val tvVenue = TextView(this)
                tvVenue.text = "Venue: $venue"

                val tvEmail = TextView(this)
                tvEmail.text = "Student: $email"
                tvEmail.setTextColor(Color.parseColor("#0F3D3E"))

                card.addView(tvTitle)
                card.addView(tvDate)
                card.addView(tvVenue)
                card.addView(tvEmail)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No students registered"
            empty.setTextColor(Color.WHITE)
            container.addView(empty)
        }

        cursor.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}