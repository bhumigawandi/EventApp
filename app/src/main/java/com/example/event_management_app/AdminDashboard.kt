package com.example.event_management_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
//sidjmioa
class AdminDashboard : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout
///SDSADSDAS
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

        // FILTER
        totalBox.setOnClickListener { loadEvents("ALL") }
        pendingBox.setOnClickListener { loadEvents("PENDING") }
        approvedBox.setOnClickListener { loadEvents("APPROVED") }

        // LOGOUT
        logoutBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        loadEvents("PENDING")
        updateCounts()
    }

    private fun loadEvents(type: String) {

        container.removeAllViews()

        pendingText.text = when (type) {
            "ALL" -> "Showing all events"
            "APPROVED" -> "Approved events"
            else -> "Pending approvals"
        }

        val cursor = when (type) {
            "ALL" -> db.getAllEvents()
            "APPROVED" -> db.getApprovedEvents()
            else -> db.getPendingEvents()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))

                // CARD
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

                // ONLY for pending
                if (status.equals("Pending", true)) {

                    val btnApprove = Button(this)
                    btnApprove.text = "Approve"
                    btnApprove.setBackgroundColor(Color.parseColor("#22C55E"))
                    btnApprove.setTextColor(Color.WHITE)

                    val btnReject = Button(this)
                    btnReject.text = "Reject"
                    btnReject.setBackgroundColor(Color.parseColor("#EF4444"))
                    btnReject.setTextColor(Color.WHITE)

                    btnApprove.setOnClickListener {
                        db.updateEventStatus(id, "Approved")
                        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                        loadEvents("PENDING")
                        updateCounts()
                    }

                    btnReject.setOnClickListener {
                        db.updateEventStatus(id, "Rejected")
                        Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
                        loadEvents("PENDING")
                        updateCounts()
                    }

                    card.addView(btnApprove)
                    card.addView(btnReject)
                }

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
    }
}