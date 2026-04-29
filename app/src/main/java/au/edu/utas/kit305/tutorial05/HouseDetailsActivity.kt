package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageView
import android.util.Log

class HouseDetailsActivity : AppCompatActivity() {
    private var roomEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val houseId = intent.getStringExtra("houseId") ?: ""
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_list_screen)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        val btnEdit = findViewById<ImageView>(R.id.btnEdit)

        btnBack.setOnClickListener {
            finish()
        }

        val houseName = intent.getStringExtra("houseName") ?: "House Name"

        val title = findViewById<TextView>(R.id.lblListTitle)
        val btnAdd = findViewById<Button>(R.id.btnListAdd)
        val recycler = findViewById<RecyclerView>(R.id.recyclerList)

        btnEdit.setOnClickListener {
            roomEditMode = !roomEditMode
            recycler.adapter?.notifyDataSetChanged()
        }

        title.text = houseName
        btnAdd.text = "Add Room"

        val db = FirebaseFirestore.getInstance() //---------------ai----------------
        val rooms = mutableListOf<Room>()

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = RoomTextAdapter(rooms)

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

                recycler.adapter?.notifyDataSetChanged()
            } //-------------------------------------------ai-------------------------

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddRoomActivity::class.java)
            intent.putExtra("houseId", houseId)
            startActivity(intent)
        }
    }

    inner class RoomTextAdapter(private val rooms: List<Room>) :
        RecyclerView.Adapter<RoomTextAdapter.RoomHolder>() {

        inner class RoomHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
            val txtName: TextView = view.findViewById(R.id.txtName)
            val txtArrow: TextView = view.findViewById(R.id.txtArrow)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_list_item, parent, false)

            return RoomHolder(view)
        }

        override fun getItemCount(): Int {
            return rooms.size
        }

        override fun onBindViewHolder(holder: RoomHolder, position: Int) {
            val room = rooms[position]

            holder.txtName.text = room.roomName

            if (roomEditMode) {
                holder.txtArrow.text = "Edit"
            } else {
                holder.txtArrow.text = ">"
            }

            holder.view.setOnClickListener {
                if (roomEditMode) {
                    val intent = Intent(holder.view.context, AddRoomActivity::class.java)
                    intent.putExtra("roomId", room.id)
                    intent.putExtra("houseId", room.houseId)
                    intent.putExtra("editMode", true)
                    holder.view.context.startActivity(intent)
                } else {
                    val intent = Intent(holder.view.context, RoomDetailsActivity::class.java)
                    intent.putExtra("roomId", room.id)
                    intent.putExtra("roomName", room.roomName)
                    holder.view.context.startActivity(intent)
                }
            }
        }
    }
    }











/*package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class HouseDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_list_screen)

        val houseName = intent.getStringExtra("houseName") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val customerName = intent.getStringExtra("customerName") ?: ""

        findViewById<TextView>(R.id.lblHouseName).text = houseName
        findViewById<TextView>(R.id.lblAddress).text = address
        findViewById<TextView>(R.id.lblCustomerName).text = customerName

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
*/
