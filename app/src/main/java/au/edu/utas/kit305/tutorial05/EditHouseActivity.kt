package au.edu.utas.kit305.tutorial05

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddHouseBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditHouseActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddHouseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddHouseBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()
        val houseId = intent.getStringExtra("houseId") ?: ""

        ui.btnCreateHouse.text = "Save House"
        ui.headerBar.lblHeaderTitle.text = "Edit House"
        ui.headerBar.btnDelete.visibility = View.VISIBLE

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        // load existing house details
        db.collection("houses")
            .document(houseId)
            .get()
            .addOnSuccessListener { document ->
                ui.txtHouseName.setText(document.getString("houseName") ?: "")
                ui.txtAddress.setText(document.getString("address") ?: "")
                ui.txtCustomerName.setText(document.getString("customerName") ?: "")
            }

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete House?")
                .setMessage("Are you sure you want to delete this house? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    db.collection("houses")
                        .document(houseId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "House deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        ui.btnCreateHouse.setOnClickListener {
            val houseName = ui.txtHouseName.text.toString()
            val address = ui.txtAddress.text.toString()
            val customerName = ui.txtCustomerName.text.toString()

            // check required fields
            if (!isValidText(houseName)) {
                ui.txtHouseName.error = "Required"
                return@setOnClickListener
            }

            if (!isValidText(address)) {
                ui.txtAddress.error = "Required"
                return@setOnClickListener
            }

            if (!isValidText(customerName)) {
                ui.txtCustomerName.error = "Required"
                return@setOnClickListener
            }

            val house = hashMapOf(
                "houseName" to houseName.trim(),
                "address" to address.trim(),
                "customerName" to customerName.trim(),
                "total" to 0.0,
                "status" to "Draft"
            )

            db.collection("houses")
                .document(houseId)
                .set(house)
                .addOnSuccessListener {
                    Toast.makeText(this, "House updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun isValidText(input: String): Boolean {
        return input.trim().isNotEmpty()
    }
}