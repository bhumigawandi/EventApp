package com.example.event_management_app

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdminDashboard : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout
    private lateinit var pendingCount: TextView

    private lateinit var totalBox: TextView
    private lateinit var pendingBox: TextView
    private lateinit var approvedBox: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        db = DBHelper(this)

        container = findViewById(R.id.pendingContainer)
        pendingCount = findViewById(R.id.pendingCount)

        totalBox = findViewById(R.id.totalCount)
        pendingBox = findViewById(R.id.pendingBox)
        approvedBox = findViewById(R.id.approvedCount)

        // Click listeners
        totalBox.setOnClickListener {
            loadEvents("ALL")
        }

        pendingBox.setOnClickListener {
            loadEvents("PENDING")
        }

        approvedBox.setOnClickListener {
            loadEvents("APPROVED")
        }

        // Default load
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

                val card = LinearLayout(this)
                card.orientation = LinearLayout.VERTICAL
                card.setPadding(20, 20, 20, 20)
                card.setBackgroundResource(R.drawable.card_bg)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 20)
                card.layoutParams = params

                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 16f
                tvTitle.setTextColor(Color.BLACK)

                val tvInfo = TextView(this)
                tvInfo.text = "$category • $date"
                tvInfo.setTextColor(Color.GRAY)

                card.addView(tvTitle)
                card.addView(tvInfo)

                // Buttons (only for pending)
                if (type == "PENDING") {

                    val btnLayout = LinearLayout(this)
                    btnLayout.orientation = LinearLayout.HORIZONTAL

                    val approve = Button(this)
                    approve.text = "✓ Approve"
                    approve.setBackgroundColor(Color.parseColor("#4CAF50"))
                    approve.setTextColor(Color.WHITE)

                    val reject = Button(this)
                    reject.text = "✗ Reject"
                    reject.setBackgroundColor(Color.parseColor("#F44336"))
                    reject.setTextColor(Color.WHITE)

                    val btnParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    btnParams.setMargins(10, 10, 10, 0)

                    approve.layoutParams = btnParams
                    reject.layoutParams = btnParams

                    approve.setOnClickListener {
                        db.updateEventStatus(id, "Approved")
                        Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                        loadEvents("PENDING")
                        updateCounts()
                    }

                    reject.setOnClickListener {
                        db.updateEventStatus(id, "Rejected")
                        Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
                        loadEvents("PENDING")
                        updateCounts()
                    }

                    btnLayout.addView(approve)
                    btnLayout.addView(reject)
                    card.addView(btnLayout)
                }

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Events Found"
            empty.setTextColor(Color.GRAY)
            container.addView(empty)
        }

        cursor.close()
    }

    private fun updateCounts() {
        val total = db.getAllEvents().count
        val pending = db.getPendingEvents().count
        val approved = db.getApprovedEvents().count

        totalBox.text = "$total\nTotal"
        pendingBox.text = "$pending\nPending"
        approvedBox.text = "$approved\nApproved"

        pendingCount.text = "$pending events awaiting approval"
    }
}