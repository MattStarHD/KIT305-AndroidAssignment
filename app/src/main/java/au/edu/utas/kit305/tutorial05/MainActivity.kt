// House list now reads from Firestore

package au.edu.utas.kit305.tutorial05
import com.google.firebase.firestore.FirebaseFirestore

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding

val items = mutableListOf<House>()

class MainActivity : AppCompatActivity()
{
    private lateinit var ui : ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()
/*
        val movie = hashMapOf(
            "title" to "Test Movie Firebase"
        )

        db.collection("movies")
            .add(movie)
*/
        ui.lblMovieCount.text = "${items.size} Houses"
        ui.myList.adapter = HouseAdapter(houses = items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)
        // 👇 PASTE THE BLOCK HERE (INSIDE onCreate)

        db.collection("houses")
            .get()
            .addOnSuccessListener { result ->
                items.clear()

                for (document in result) { //------------------ai--------------------
                    val houseName = document.getString("houseName") ?: ""
                    val address = document.getString("address") ?: ""
                    val customerName = document.getString("customerName") ?: ""

                    items.add(
                        House(
                            id = document.id,
                            houseName = houseName,
                            address = address,
                            customerName = customerName
                        )
                    )
                }

                ui.lblMovieCount.text = "${items.size} Houses"
                ui.myList.adapter?.notifyDataSetChanged()
            }
    }

    inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class HouseAdapter(private val houses: MutableList<House>) : RecyclerView.Adapter<MovieHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.MovieHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return MovieHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return houses.size
        }

        override fun onBindViewHolder(holder: MainActivity.MovieHolder, position: Int) {
            val house = houses[position]
            holder.ui.txtName.text = house.houseName //-----------AI----------
            holder.ui.txtYear.text = house.address
        }
    }
}

