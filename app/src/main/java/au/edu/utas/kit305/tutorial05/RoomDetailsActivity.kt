package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.ImageView
import android.content.Intent
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RoomDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_details)
        val roomId = intent.getStringExtra("roomId") ?: ""
        val roomName = intent.getStringExtra("roomName") ?: "Room"

        findViewById<TextView>(R.id.lblRoomTitle).text = roomName

        val db = FirebaseFirestore.getInstance()
        var total = 0.0

        val floorItems = mutableListOf<RoomItem>()
        val windowItems = mutableListOf<RoomItem>()

        val recyclerFloorItems = findViewById<RecyclerView>(R.id.recyclerRoomItems)
        val recyclerWindowItems = findViewById<RecyclerView>(R.id.recyclerWindowItems)

        recyclerFloorItems.layoutManager = LinearLayoutManager(this)
        recyclerWindowItems.layoutManager = LinearLayoutManager(this)

        recyclerFloorItems.adapter = RoomItemAdapter(floorItems)
        recyclerWindowItems.adapter = RoomItemAdapter(windowItems)

        db.collection("floors")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val price = document.getDouble("totalPrice") ?: 0.0
                    val width = document.getDouble("width") ?: 0.0
                    val depth = document.getDouble("depth") ?: 0.0

                    total += price

                    floorItems.add(
                        RoomItem(
                            name = document.getString("productName") ?: "",
                            details = "${width} x ${depth} mm",
                            price = price
                        )
                    )
                }

                recyclerFloorItems.adapter?.notifyDataSetChanged()
            }

        db.collection("windows")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val price = document.getDouble("totalPrice") ?: 0.0
                    val width = document.getDouble("width") ?: 0.0
                    val height = document.getDouble("height") ?: 0.0

                    total += price

                    windowItems.add(
                        RoomItem(
                            name = document.getString("productName") ?: "",
                            details = "${width} x ${height} mm",
                            price = price
                        )
                    )
                }

                findViewById<TextView>(R.id.lblTotal).text =
                    "Total: $${"%.2f".format(total)}"

                recyclerWindowItems.adapter?.notifyDataSetChanged()
            }

        findViewById<Button>(R.id.btnAddFloor).setOnClickListener {
            val intent = Intent(this, AddFloorActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddWindows).setOnClickListener {
            val intent = Intent(this, AddWindowActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}