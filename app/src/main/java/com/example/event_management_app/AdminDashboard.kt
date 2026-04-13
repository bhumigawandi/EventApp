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

        // FILTER BUTTONS
        totalBox.setOnClickListener { loadEvents("ALL") }
        pendingBox.setOnClickListener { loadEvents("PENDING") }
        approvedBox.setOnClickListener { loadEvents("APPROVED") }

        // LOGOUT
        logoutBtn.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure?")

            builder.setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
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
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))

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

                // BUTTONS
                val btnApprove = Button(this)
                btnApprove.text = "Approve"

                val btnReject = Button(this)
                btnReject.text = "Reject"

                // APPROVE
                btnApprove.setOnClickListener {

                    db.updateEventStatus(id, "Approved")

                    Notify.show(this, "Event Approved", "$title approved")

                    Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()

                    loadEvents("PENDING")
                    updateCounts()
                }

                // REJECT
                btnReject.setOnClickListener {

                    db.updateEventStatus(id, "Rejected")

                    Notify.show(this, "Event Rejected", "$title rejected")

                    Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()

                    loadEvents("PENDING")
                    updateCounts()
                }

                // ADD VIEWS
                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(btnApprove)
                card.addView(btnReject)

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