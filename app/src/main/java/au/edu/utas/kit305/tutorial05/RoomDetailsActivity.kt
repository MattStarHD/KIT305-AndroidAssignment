package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import au.edu.utas.kit305.tutorial05.databinding.ActivityRoomDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class RoomDetailsActivity : AppCompatActivity() {

    private lateinit var ui: ActivityRoomDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityRoomDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val roomId = intent.getStringExtra("roomId") ?: ""
        val roomName = intent.getStringExtra("roomName") ?: "Room"
        val houseId = intent.getStringExtra("houseId") ?: ""
        val houseName = intent.getStringExtra("houseName") ?: ""

        ui.lblRoomTitle.text = roomName

        val db = FirebaseFirestore.getInstance()

        val floorItems = mutableListOf<RoomItem>()
        val windowItems = mutableListOf<RoomItem>()

        ui.recyclerRoomItems.layoutManager = LinearLayoutManager(this)
        ui.recyclerWindowItems.layoutManager = LinearLayoutManager(this)

        ui.recyclerRoomItems.adapter = RoomItemAdapter(floorItems) { item ->
            val intent = Intent(this, EditFloorActivity::class.java)
            intent.putExtra("floorId", item.id)
            intent.putExtra("roomId", roomId)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        ui.recyclerWindowItems.adapter = RoomItemAdapter(windowItems) { item ->
            val intent = Intent(this, EditWindowActivity::class.java)
            intent.putExtra("windowId", item.id)
            intent.putExtra("roomId", roomId)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        fun updateTotal() {
            val floorTotal = floorItems.sumOf { it.price }
            val windowTotal = windowItems.sumOf { it.price }

            ui.lblTotal.text =
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

                ui.recyclerRoomItems.adapter?.notifyDataSetChanged()
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

                ui.recyclerWindowItems.adapter?.notifyDataSetChanged()
                updateTotal()
            }

        ui.btnAddFloor.setOnClickListener {
            val intent = Intent(this, AddFloorActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        ui.btnAddWindows.setOnClickListener {
            val intent = Intent(this, AddWindowActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        ui.btnQuote.setOnClickListener {
            val intent = Intent(this, QuoteActivity::class.java)
            intent.putExtra("houseId", houseId)
            intent.putExtra("houseName", houseName)
            startActivity(intent)
        }

        ui.btnBack.setOnClickListener {
            finish()
        }

        ui.btnEditRoom.setOnClickListener {
            val intent = Intent(this, AddRoomActivity::class.java)
            intent.putExtra("roomId", roomId)
            intent.putExtra("houseId", houseId)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(ui.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}