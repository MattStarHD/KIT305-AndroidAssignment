package au.edu.utas.kit305.tutorial05

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddFloorBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddFloorActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddFloorBinding

    private var selectedProductId = ""
    private var selectedProductName = ""
    private var selectedProductPrice = 0.0
    private var selectedColour = ""
    private var availableColours = arrayListOf<String>()

    private val productLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedProductId = result.data?.getStringExtra("productId") ?: ""
            selectedProductName = result.data?.getStringExtra("productName") ?: ""
            selectedProductPrice = result.data?.getDoubleExtra("productPrice", 0.0) ?: 0.0
            availableColours = result.data?.getStringArrayListExtra("productVariants") ?: arrayListOf()

            selectedColour = ""

            ui.btnChooseProduct.text = selectedProductName
            ui.btnChooseColour.text = "Choose Colour"

            val intent = Intent(this, ColourSelectorActivity::class.java)
            intent.putStringArrayListExtra("colours", availableColours)
            colourLauncher.launch(intent)
        }
    }

    private val colourLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedColour = result.data?.getStringExtra("selectedColour") ?: ""
            ui.btnChooseColour.text = selectedColour
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddFloorBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val floorId = intent.getStringExtra("floorId")
        val isEdit = intent.getBooleanExtra("editMode", false)

        ui.headerBar.lblHeaderTitle.text = if (isEdit) "Edit Floor" else "Add Floor"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.headerBar.btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Floor?")
                .setMessage("Are you sure you want to delete this floor?")
                .setPositiveButton("Delete") { _, _ ->
                    if (floorId != null) {
                        db.collection("floors")
                            .document(floorId)
                            .delete()
                            .addOnSuccessListener {
                                finish()
                            }
                    }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        if (isEdit && floorId != null) {
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
        }

        ui.btnChooseProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "floor")
            productLauncher.launch(intent)
        }

        ui.btnChooseColour.setOnClickListener {
            if (availableColours.isEmpty()) {
                Toast.makeText(this, "Please choose the product again first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ColourSelectorActivity::class.java)
            intent.putStringArrayListExtra("colours", availableColours)
            colourLauncher.launch(intent)
        }

        ui.btnSaveFloor.setOnClickListener {
            val widthText = ui.txtWidth.text.toString()
            val depthText = ui.txtDepth.text.toString()

            if (!isValidDouble(widthText)) {
                ui.txtWidth.error = "Enter a valid width"
                return@setOnClickListener
            }

            if (!isValidDouble(depthText)) {
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

            val width = widthText.toDouble()
            val depth = depthText.toDouble()

            val area = (width * depth) / 1_000_000
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

            if (isEdit && floorId != null) {
                db.collection("floors")
                    .document(floorId)
                    .set(floor)
                    .addOnSuccessListener {
                        finish()
                    }
            } else {
                db.collection("floors")
                    .add(floor)
                    .addOnSuccessListener {
                        finish()
                    }
            }
        }
    }

    private fun isValidDouble(input: String): Boolean {
        val number = input.toDoubleOrNull()
        return number != null && number > 0
    }
}