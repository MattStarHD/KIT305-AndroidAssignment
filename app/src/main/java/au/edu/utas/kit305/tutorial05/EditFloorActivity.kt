package au.edu.utas.kit305.tutorial05

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddFloorBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditFloorActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddFloorBinding

    private var selectedProductId = ""
    private var selectedProductName = ""
    private var selectedProductPrice = 0.0
    private var selectedColour = ""
    private var availableColours = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddFloorBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val floorId = intent.getStringExtra("floorId") ?: ""

        selectedProductId = intent.getStringExtra("productId") ?: ""
        selectedProductName = intent.getStringExtra("productName") ?: ""
        selectedProductPrice = intent.getDoubleExtra("productPrice", 0.0)
        selectedColour = intent.getStringExtra("selectedColour") ?: ""
        availableColours = intent.getStringArrayListExtra("productVariants") ?: arrayListOf()

        ui.headerBar.lblHeaderTitle.text = "Edit Floor"
        ui.headerBar.btnDelete.visibility = View.VISIBLE

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Floor?")
                .setMessage("Are you sure you want to delete this floor?")
                .setPositiveButton("Delete") { _, _ ->
                    db.collection("floors")
                        .document(floorId)
                        .delete()
                        .addOnSuccessListener {
                            finish()
                        }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        // load existing floor details
        if (selectedProductId.isBlank()) {
            db.collection("floors")
                .document(floorId)
                .get()
                .addOnSuccessListener { document ->
                    ui.txtWidth.setText((document.getDouble("width") ?: 0.0).toString())
                    ui.txtDepth.setText((document.getDouble("depth") ?: 0.0).toString())
                    ui.txtNotes.setText(document.getString("notes") ?: "")

                    selectedProductId = document.getString("productId") ?: ""
                    selectedProductName = document.getString("productName") ?: ""
                    selectedProductPrice = document.getDouble("pricePerSquareMeter") ?: 0.0
                    selectedColour = document.getString("colour") ?: ""

                    ui.btnChooseProduct.text =
                        if (selectedProductName.isBlank()) "Choose Product" else selectedProductName

                    ui.btnChooseColour.text =
                        if (selectedColour.isBlank()) "Choose Colour" else selectedColour
                }
        } else {
            ui.txtWidth.setText(intent.getStringExtra("width") ?: "")
            ui.txtDepth.setText(intent.getStringExtra("depth") ?: "")
            ui.txtNotes.setText(intent.getStringExtra("notes") ?: "")

            ui.btnChooseProduct.text = selectedProductName
            ui.btnChooseColour.text =
                if (selectedColour.isBlank()) "Choose Colour" else selectedColour
        }

        ui.btnChooseProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)

            val width = ui.txtWidth.text.toString()
            val depth = ui.txtDepth.text.toString()
            val notes = ui.txtNotes.text.toString()

            intent.putExtra("type", "floor")
            intent.putExtra("returnTo", "EditFloor")
            intent.putExtra("roomId", roomId)
            intent.putExtra("floorId", floorId)
            intent.putExtra("width", width)
            intent.putExtra("depth", depth)
            intent.putExtra("notes", notes)

            startActivity(intent)
            finish()
        }

        ui.btnChooseColour.setOnClickListener {
            if (availableColours.isEmpty()) {
                Toast.makeText(this, "Please choose the product again first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ColourSelectorActivity::class.java)

            val width = ui.txtWidth.text.toString()
            val depth = ui.txtDepth.text.toString()
            val notes = ui.txtNotes.text.toString()

            intent.putExtra("returnTo", "EditFloor")
            intent.putExtra("roomId", roomId)
            intent.putExtra("floorId", floorId)
            intent.putExtra("width", width)
            intent.putExtra("depth", depth)
            intent.putExtra("notes", notes)
            intent.putExtra("productId", selectedProductId)
            intent.putExtra("productName", selectedProductName)
            intent.putExtra("productPrice", selectedProductPrice)
            intent.putStringArrayListExtra("productVariants", availableColours)

            startActivity(intent)
            finish()
        }

        ui.btnSaveFloor.setOnClickListener {
            val widthText = ui.txtWidth.text.toString().trim()
            val depthText = ui.txtDepth.text.toString().trim()

            val width = widthText.toDoubleOrNull()
            val depth = depthText.toDoubleOrNull()

            if (width == null || width <= 0) {
                ui.txtWidth.error = "Enter a valid width"
                return@setOnClickListener
            }

            if (depth == null || depth <= 0) {
                ui.txtDepth.error = "Enter a valid depth"
                return@setOnClickListener
            }

            if (selectedProductId.isBlank() || selectedProductName.isBlank()) {
                Toast.makeText(this, "Please choose a product", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedColour.isBlank()) {
                Toast.makeText(this, "Please choose a colour", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val area = (width * depth) / 1000000
            val totalPrice = area * selectedProductPrice

            val floor = hashMapOf(
                "roomId" to roomId,
                "productId" to selectedProductId,
                "productName" to selectedProductName,
                "pricePerSquareMeter" to selectedProductPrice,
                "width" to width,
                "depth" to depth,
                "area" to area,
                "totalPrice" to totalPrice,
                "notes" to ui.txtNotes.text.toString().trim(),
                "colour" to selectedColour
            )

            db.collection("floors")
                .document(floorId)
                .set(floor)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}