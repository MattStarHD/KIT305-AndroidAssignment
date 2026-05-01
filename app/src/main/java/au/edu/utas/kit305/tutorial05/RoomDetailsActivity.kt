package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import au.edu.utas.kit305.tutorial05.databinding.ActivityRoomDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore

class RoomDetailsActivity : AppCompatActivity() {

    private lateinit var ui: ActivityRoomDetailsBinding
    private lateinit var db: FirebaseFirestore

    private val floorItems = mutableListOf<RoomItem>()
    private val windowItems = mutableListOf<RoomItem>()

    private var roomId = ""
    private var houseId = ""
    private var houseName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityRoomDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        db = FirebaseFirestore.getInstance()

        roomId = intent.getStringExtra("roomId") ?: ""
        val roomName = intent.getStringExtra("roomName") ?: "Room"
        houseId = intent.getStringExtra("houseId") ?: ""
        houseName = intent.getStringExtra("houseName") ?: ""

        ui.lblRoomTitle.text = roomName

        ui.recyclerRoomItems.layoutManager = LinearLayoutManager(this)
        ui.recyclerWindowItems.layoutManager = LinearLayoutManager(this)

        ui.recyclerRoomItems.adapter = RoomItemAdapter(floorItems) { item ->
            val intent = Intent(this, EditFloorActivity::class.java)
            intent.putExtra("floorId", item.id)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        ui.recyclerWindowItems.adapter = RoomItemAdapter(windowItems) { item ->
            val intent = Intent(this, EditWindowActivity::class.java)
            intent.putExtra("windowId", item.id)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
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
            val intent = Intent(this, EditRoomActivity::class.java)
            intent.putExtra("roomId", roomId)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }
    }

    // reload room items when returning to screen
    override fun onResume() {
        super.onResume()
        loadFloors()
        loadWindows()
    }

    private fun loadFloors() {
        db.collection("floors")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                floorItems.clear()

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

                ui.recyclerRoomItems.adapter?.notifyDataSetChanged()
                calculateTotal()
            }
    }

    private fun loadWindows() {
        db.collection("windows")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->
                windowItems.clear()

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

                ui.recyclerWindowItems.adapter?.notifyDataSetChanged()
                calculateTotal()
            }
    }

    // update room total from floors and windows
    private fun calculateTotal() {
        var floorTotal = 0.0
        for (item in floorItems) {
            floorTotal += item.price
        }

        var windowTotal = 0.0
        for (item in windowItems) {
            windowTotal += item.price
        }

        val total = floorTotal + windowTotal
        ui.lblTotal.text = "Total: $" + String.format("%.2f", total)
    }
}