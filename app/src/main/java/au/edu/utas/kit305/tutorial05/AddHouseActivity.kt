package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
class AddHouseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_house)

        val db = FirebaseFirestore.getInstance() //----------------ai-----------------

        val txtHouseName = findViewById<EditText>(R.id.txtHouseName)
        val txtAddress = findViewById<EditText>(R.id.txtAddress)
        val txtCustomerName = findViewById<EditText>(R.id.txtCustomerName)
        val btnCreate = findViewById<Button>(R.id.btnCreateHouse)

        btnCreate.setOnClickListener {
            val house = hashMapOf(
                "houseName" to txtHouseName.text.toString(),
                "address" to txtAddress.text.toString(),
                "customerName" to txtCustomerName.text.toString(),
                "total" to 0.0,
                "status" to "Draft"
            )

            db.collection("houses")
                .add(house)
                .addOnSuccessListener {
                    finish()
                }                                //^^^^^^^^^^^^^^ai^^^^^^^^^^^^^
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}