package com.example.event_management_app

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboard : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout
    private lateinit var pendingCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 IMPORTANT
        setContentView(R.layout.activity_admin_dashboard)

        // Initialize views
        db = DBHelper(this)
        container = findViewById(R.id.pendingContainer)
        pendingCount = findViewById(R.id.pendingCount)

        loadEvents()
    }

    private fun loadEvents() {

        container.removeAllViews()

        val cursor = db.getPendingEvents()
        var count = 0

        if (cursor.moveToFirst()) {
            do {
                count++

                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                // Card
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

                // Title
                val tvTitle = TextView(this)
                tvTitle.text = title
                tvTitle.textSize = 16f
                tvTitle.setTextColor(Color.BLACK)

                // Info
                val tvInfo = TextView(this)
                tvInfo.text = "$category • $date"
                tvInfo.setTextColor(Color.GRAY)

                // Buttons Layout
                val btnLayout = LinearLayout(this)
                btnLayout.orientation = LinearLayout.HORIZONTAL

                val approve = Button(this)
                approve.text = "✓ Approve"

                val reject = Button(this)
                reject.text = "✗ Reject"

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
                    loadEvents()
                }

                reject.setOnClickListener {
                    db.updateEventStatus(id, "Rejected")
                    Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show()
                    loadEvents()
                }

                btnLayout.addView(approve)
                btnLayout.addView(reject)

                card.addView(tvTitle)
                card.addView(tvInfo)
                card.addView(btnLayout)

                container.addView(card)

            } while (cursor.moveToNext())
        } else {
            val empty = TextView(this)
            empty.text = "No Pending Events"
            empty.setTextColor(Color.GRAY)
            container.addView(empty)
        }

        pendingCount.text = "$count events awaiting approval"
        cursor.close()
    }
}