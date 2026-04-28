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

class HouseDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_list_screen)

        val houseName = intent.getStringExtra("houseName") ?: "House Name"

        val title = findViewById<TextView>(R.id.lblListTitle)
        val btnAdd = findViewById<Button>(R.id.btnListAdd)
        val recycler = findViewById<RecyclerView>(R.id.recyclerList)

        title.text = houseName
        btnAdd.text = "Add Room"

        val rooms = listOf("Master Bedroom", "Bathroom", "Living Room", "Kitchen", "Laundry")

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = RoomTextAdapter(rooms)

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddRoomActivity::class.java)
            startActivity(intent)
        }
    }

    inner class RoomTextAdapter(private val rooms: List<String>) :
        RecyclerView.Adapter<RoomTextAdapter.RoomHolder>() {

        inner class RoomHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
            val txtName: TextView = view.findViewById(R.id.txtName)
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
            val roomName = rooms[position]

            holder.txtName.text = roomName

            holder.view.setOnClickListener {
                val intent = Intent(this@HouseDetailsActivity, RoomDetailsActivity::class.java)
                intent.putExtra("roomName", roomName)
                startActivity(intent)
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
