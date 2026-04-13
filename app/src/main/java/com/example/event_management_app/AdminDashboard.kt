package com.example.event_management_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AdminDashboard : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout

    private lateinit var totalBox: LinearLayout
    private lateinit var pendingBox: LinearLayout
    private lateinit var approvedBox: LinearLayout

    private lateinit var totalCount: TextView
    private lateinit var pendingCount: TextView
    private lateinit var approvedCount: TextView
    private lateinit var pendingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        db = DBHelper(this)

        container = findViewById(R.id.pendingContainer)

        totalBox = findViewById(R.id.totalBox)
        pendingBox = findViewById(R.id.pendingBox)
        approvedBox = findViewById(R.id.approvedBox)

        totalCount = findViewById(R.id.totalCount)
        pendingCount = findViewById(R.id.pendingCount)
        approvedCount = findViewById(R.id.approvedCount)
        pendingText = findViewById(R.id.pendingText)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        // ✅ CLICK FILTERS
        totalBox.setOnClickListener { loadEvents("ALL") }
        pendingBox.setOnClickListener { loadEvents("PENDING") }
        approvedBox.setOnClickListener { loadEvents("APPROVED") }

        // ✅ LOGOUT ALERT
        logoutBtn.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        loadEvents("PENDING")
        updateCounts()
    }

    private fun loadEvents(type: String) {

        container.removeAllViews()

        val cursor = when (type) {
            "ALL" -> db.getAllEvents()
            "APPROVED" -> db.getApprovedEvents()
            else -> db.getPendingEvents()
        }

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(25, 25, 25, 25)
                card.setBackgroundColor(Color.WHITE)

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 16f

                val tvInfo = TextView(this)
                tvInfo.text = "$category • $date"

                card.addView(tvTitle)
                card.addView(tvInfo)

                container.addView(card)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    private fun updateCounts() {
        val total = db.getAllEvents().count
        val pending = db.getPendingEvents().count
        val approved = db.getApprovedEvents().count

        totalCount.text = total.toString()
        pendingCount.text = pending.toString()
        approvedCount.text = approved.toString()
        pendingText.text = "$pending events awaiting approval"
    }
}