package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.firestore.FirebaseFirestore

val items = mutableListOf<House>()

class MainActivity : AppCompatActivity()
{
    private lateinit var ui : ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.lblMovieCount.text = "${items.size} Houses"
        ui.myList.adapter = HouseAdapter(houses = items)
        ui.myList.layoutManager = LinearLayoutManager(this)

        ui.testingButton.setOnClickListener {
            val intent = android.content.Intent(this, AddHouseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadHouses()
    }

    // load houses from Firestore
    private fun loadHouses() {
        val db = FirebaseFirestore.getInstance()

        db.collection("houses")
            .get()
            .addOnSuccessListener { result ->
                items.clear()

                for (document in result) {
                    items.add(
                        House(
                            id = document.id,
                            houseName = document.getString("houseName") ?: "",
                            address = document.getString("address") ?: "",
                            customerName = document.getString("customerName") ?: ""
                        )
                    )
                }

                ui.lblMovieCount.text = "${items.size} Houses"
                ui.myList.adapter?.notifyDataSetChanged()
            }
    }

    inner class HouseHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class HouseAdapter(private val houses: MutableList<House>) : RecyclerView.Adapter<HouseHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.HouseHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)
            return HouseHolder(ui)
        }

        override fun getItemCount(): Int {
            return houses.size
        }

        override fun onBindViewHolder(holder: MainActivity.HouseHolder, position: Int) {
            val house = houses[position]

            holder.ui.txtName.text = house.houseName

            holder.ui.root.setOnClickListener {
                val intent = android.content.Intent(this@MainActivity, HouseDetailsActivity::class.java)
                intent.putExtra("houseId", house.id)
                intent.putExtra("houseName", house.houseName)
                intent.putExtra("address", house.address)
                intent.putExtra("customerName", house.customerName)
                startActivity(intent)
            }
        }
    }
}