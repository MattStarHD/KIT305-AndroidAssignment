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
import androidx.recyclerview.widget.ItemTouchHelper
import android.app.AlertDialog

class RoomDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_details)
        val roomId = intent.getStringExtra("roomId") ?: ""
        val roomName = intent.getStringExtra("roomName") ?: "Room"
        val houseId = intent.getStringExtra("houseId") ?: ""
        val houseName = intent.getStringExtra("houseName") ?: ""

        findViewById<TextView>(R.id.lblRoomTitle).text = roomName

        val db = FirebaseFirestore.getInstance()

        val floorItems = mutableListOf<RoomItem>()
        val windowItems = mutableListOf<RoomItem>()

        val recyclerFloorItems = findViewById<RecyclerView>(R.id.recyclerRoomItems)
        val recyclerWindowItems = findViewById<RecyclerView>(R.id.recyclerWindowItems)

        recyclerFloorItems.layoutManager = LinearLayoutManager(this)
        recyclerWindowItems.layoutManager = LinearLayoutManager(this)

            recyclerFloorItems.adapter = RoomItemAdapter(floorItems) { item ->
                val intent = Intent(this, AddFloorActivity::class.java)
                intent.putExtra("floorId", item.id)
                intent.putExtra("roomId", roomId)
                intent.putExtra("editMode", true)
                startActivity(intent)
            }


            recyclerWindowItems.adapter = RoomItemAdapter(windowItems) { item ->
                val intent = Intent(this, AddWindowActivity::class.java)
                intent.putExtra("windowId", item.id)
                intent.putExtra("roomId", roomId)
                intent.putExtra("editMode", true)
                startActivity(intent)
            }

        fun updateTotal() {
            val floorTotal = floorItems.sumOf { it.price }
            val windowTotal = windowItems.sumOf { it.price }

            findViewById<TextView>(R.id.lblTotal).text =
                "Total: $${"%.2f".format(floorTotal + windowTotal)}"
        }

        db.collection("floors")
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener { result, _ ->
                floorItems.clear()

                if (result != null) {
                    for (document in result) {
                        val price = document.getDouble("totalPrice") ?: 0.0
                        val width = document.getDouble("width") ?: 0.0
                        val depth = document.getDouble("depth") ?: 0.0

                        floorItems.add(
                            RoomItem(
                                id = document.id,
                                name = document.getString("productName") ?: "Floor Space",
                                details = "${width} x ${depth} mm",
                                price = price
                            )
                        )
                    }
                }

                recyclerFloorItems.adapter?.notifyDataSetChanged()
                updateTotal()
            }

        db.collection("windows")
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener { result, _ ->
                windowItems.clear()

                if (result != null) {
                    for (document in result) {
                        val price = document.getDouble("totalPrice") ?: 0.0
                        val width = document.getDouble("width") ?: 0.0
                        val height = document.getDouble("height") ?: 0.0

                        windowItems.add(
                            RoomItem(
                                id = document.id,
                                name = document.getString("productName") ?: "Window",
                                details = "${width} x ${height} mm",
                                price = price
                            )
                        )
                    }
                }

                recyclerWindowItems.adapter?.notifyDataSetChanged()
                updateTotal()
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

        findViewById<Button>(R.id.btnQuote).setOnClickListener {
            val intent = Intent(this, QuoteActivity::class.java)
            intent.putExtra("houseId", houseId)
            intent.putExtra("houseName", houseName)
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

        val btnEditRoom = findViewById<ImageView>(R.id.btnEditRoom)

        btnEditRoom.setOnClickListener {
            val intent = Intent(this, AddRoomActivity::class.java)
            intent.putExtra("roomId", roomId)
            intent.putExtra("houseId", houseId)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }
    }
}