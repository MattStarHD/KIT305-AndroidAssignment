package au.edu.utas.kit305.tutorial05

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddHouseBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddHouseActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddHouseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddHouseBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val houseId = intent.getStringExtra("houseId")
        val isEdit = intent.getBooleanExtra("editMode", false)

        ui.btnCreateHouse.text = if (isEdit) "Save House" else "Create"
        ui.headerBar.lblHeaderTitle.text = if (isEdit) "Edit House" else "Add House"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.headerBar.btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete House?")
                .setMessage("Are you sure you want to delete this house?")
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

        // load current house details when editing
        if (isEdit && houseId != null) {
            db.collection("houses")
                .document(houseId)
                .get()
                .addOnSuccessListener { document ->
                    ui.txtHouseName.setText(document.getString("houseName") ?: "")
                    ui.txtAddress.setText(document.getString("address") ?: "")
                    ui.txtCustomerName.setText(document.getString("customerName") ?: "")
                }
        }

        ui.btnCreateHouse.setOnClickListener {
            val houseName = ui.txtHouseName.text.toString().trim()
            val address = ui.txtAddress.text.toString().trim()
            val customerName = ui.txtCustomerName.text.toString().trim()

            // check each input field
            if (houseName.isEmpty()) {
                ui.txtHouseName.error = "Required"
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                ui.txtAddress.error = "Required"
                return@setOnClickListener
            }

            if (customerName.isEmpty()) {
                ui.txtCustomerName.error = "Required"
                return@setOnClickListener
            }

            val house = hashMapOf(
                "houseName" to houseName,
                "address" to address,
                "customerName" to customerName,
                "total" to 0.0,
                "status" to "Draft"
            )

            if (isEdit && houseId != null) {
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
            } else {
                db.collection("houses")
                    .add(house)
                    .addOnSuccessListener {
                        Toast.makeText(this, "House created", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}