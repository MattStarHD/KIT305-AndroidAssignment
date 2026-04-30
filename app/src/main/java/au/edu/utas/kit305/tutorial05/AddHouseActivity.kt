package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import android.widget.TextView
import android.widget.ImageView
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
        val houseId = intent.getStringExtra("houseId")
        val editMode = intent.getBooleanExtra("editMode", false)
        val title = findViewById<TextView>(R.id.lblHeaderTitle)
        val btnDelete = findViewById<ImageView>(R.id.btnDelete)

        if (editMode) {
            btnCreate.text = "Save House"
        } else {
            btnCreate.text = "Create"
        }

        val isEdit = intent.getBooleanExtra("editMode", false)

        findViewById<TextView>(R.id.lblHeaderTitle).text =
            if (isEdit) "Edit House" else "Add House"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnDeleteHouse = findViewById<Button>(R.id.btnDeleteHouse)

        if (editMode) {
            btnDeleteHouse.visibility = View.VISIBLE
        } else {
            btnDeleteHouse.visibility = View.GONE
        }

        findViewById<TextView>(R.id.lblHeaderTitle).text = "Create House"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        if (editMode && houseId != null) {
            db.collection("houses")
                .document(houseId)
                .get()
                .addOnSuccessListener { document ->
                    txtHouseName.setText(document.getString("houseName") ?: "")
                    txtAddress.setText(document.getString("address") ?: "")
                    txtCustomerName.setText(document.getString("customerName") ?: "")
                }
        }

        btnDeleteHouse.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete House?")
                .setMessage("Are you sure you want to delete this house? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    if (houseId != null) {
                        db.collection("houses")
                            .document(houseId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "House deleted", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                }
                .setNegativeButton("Keep", null)
                .show()
        }



        btnCreate.setOnClickListener {
            val houseName = txtHouseName.text.toString()
            val address = txtAddress.text.toString()
            val customerName = txtCustomerName.text.toString()

            if (!isValidText(houseName)) {
                txtHouseName.error = "Required"
                return@setOnClickListener
            }

            if (!isValidText(address)) {
                txtAddress.error = "Required"
                return@setOnClickListener
            }

            if (!isValidText(customerName)) {
                txtCustomerName.error = "Required"
                return@setOnClickListener
            }

            val house = hashMapOf(


                "houseName" to houseName.trim(),
                "address" to address.trim(),
                "customerName" to customerName.trim(),
                "total" to 0.0,
                "status" to "Draft"
            )

            if (editMode && houseId != null) {
                db.collection("houses")
                    .document(houseId)
                    .set(house)
                    .addOnSuccessListener {
                        Toast.makeText(this, "House updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            } else {
                db.collection("houses")
                    .add(house)
                    .addOnSuccessListener {
                        Toast.makeText(this, "House created", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }                               //^^^^^^^^^^^^^^ai^^^^^^^^^^^^^
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

        private fun isValidText(input: String): Boolean {
            return input.trim().isNotEmpty()
        }

}