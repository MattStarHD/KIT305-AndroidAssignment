package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityBaseListScreenBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class HouseDetailsActivity : AppCompatActivity() {

    private lateinit var ui: ActivityBaseListScreenBinding
    private lateinit var db: FirebaseFirestore

    private val rooms = mutableListOf<Room>()
    private var houseId = ""
    private var houseName = "House Name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityBaseListScreenBinding.inflate(layoutInflater)
        setContentView(ui.root)

        houseId = intent.getStringExtra("houseId") ?: ""
        houseName = intent.getStringExtra("houseName") ?: "House Name"

        db = FirebaseFirestore.getInstance()

        ui.lblListTitle.text = houseName
        ui.btnListAdd.text = "Add Room"
        ui.btnQuote.text = "Quote"

        ui.btnBack.setOnClickListener {
            finish()
        }

        ui.btnEdit.setOnClickListener {
            val intent = Intent(this, EditHouseActivity::class.java)
            intent.putExtra("houseId", houseId)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        ui.btnQuote.setOnClickListener {
            val intent = Intent(this, QuoteActivity::class.java)
            intent.putExtra("houseId", houseId)
            intent.putExtra("houseName", houseName)
            startActivity(intent)
        }

        ui.recyclerList.layoutManager = LinearLayoutManager(this)
        ui.recyclerList.adapter = RoomTextAdapter(rooms)

        ui.btnListAdd.setOnClickListener {
            val intent = Intent(this, AddRoomActivity::class.java)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadRooms()
    }

    private fun loadRooms() {
        db.collection("rooms")
            .whereEqualTo("houseId", houseId)
            .get()
            .addOnSuccessListener { result ->
                rooms.clear()

                for (document in result) {
                    rooms.add(
                        Room(
                            id = document.id,
                            houseId = document.getString("houseId") ?: "",
                            roomName = document.getString("roomName") ?: "",
                            width = document.getDouble("width") ?: 0.0,
                            depth = document.getDouble("depth") ?: 0.0,
                            notes = document.getString("notes") ?: ""
                        )
                    )
                }

                ui.recyclerList.adapter?.notifyDataSetChanged()
            }
    }

    inner class RoomTextAdapter(
        private val rooms: List<Room>
    ) : RecyclerView.Adapter<RoomTextAdapter.RoomHolder>() {

        inner class RoomHolder(val ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)
            return RoomHolder(ui)
        }

        override fun getItemCount(): Int {
            return rooms.size
        }

        override fun onBindViewHolder(holder: RoomHolder, position: Int) {
            val room = rooms[position]

            holder.ui.txtName.text = room.roomName
            holder.ui.txtArrow.text = ">"

            holder.ui.root.setOnClickListener {
                val intent = Intent(this@HouseDetailsActivity, RoomDetailsActivity::class.java)
                intent.putExtra("roomId", room.id)
                intent.putExtra("roomName", room.roomName)
                intent.putExtra("houseId", room.houseId)
                intent.putExtra("houseName", houseName)
                startActivity(intent)
            }
        }
    }
}