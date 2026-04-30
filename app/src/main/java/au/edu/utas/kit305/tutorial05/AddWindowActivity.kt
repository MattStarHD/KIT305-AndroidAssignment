package au.edu.utas.kit305.tutorial05

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddWindowBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddWindowActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddWindowBinding

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

            ui.btnSelectProduct.text = selectedProductName
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

        ui = ActivityAddWindowBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val windowId = intent.getStringExtra("windowId")
        val isEdit = intent.getBooleanExtra("editMode", false)

        ui.headerBar.lblHeaderTitle.text = if (isEdit) "Edit Window" else "Add Window"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.headerBar.btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Window?")
                .setMessage("Are you sure you want to delete this window?")
                .setPositiveButton("Delete") { _, _ ->
                    if (windowId != null) {
                        db.collection("windows")
                            .document(windowId)
                            .delete()
                            .addOnSuccessListener {
                                finish()
                            }
                    }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        if (isEdit && windowId != null) {
            db.collection("windows")
                .document(windowId)
                .get()
                .addOnSuccessListener { document ->
                    ui.txtWindowName.setText(document.getString("windowName") ?: "")
                    ui.txtWidth.setText((document.getDouble("width") ?: 0.0).toString())
                    ui.txtHeight.setText((document.getDouble("height") ?: 0.0).toString())
                    ui.txtNotes.setText(document.getString("notes") ?: "")

                    selectedProductId = document.getString("productId") ?: ""
                    selectedProductName = document.getString("productName") ?: ""
                    selectedProductPrice = document.getDouble("pricePerSquareMeter") ?: 0.0
                    selectedColour = document.getString("colour") ?: ""

                    ui.btnSelectProduct.text =
                        if (selectedProductName.isBlank()) "Choose Product" else selectedProductName

                    ui.btnChooseColour.text =
                        if (selectedColour.isBlank()) "Choose Colour" else selectedColour
                }
        }

        ui.btnSelectProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "window")
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

        ui.btnSaveWindow.setOnClickListener {
            val windowName = ui.txtWindowName.text.toString()
            val widthText = ui.txtWidth.text.toString()
            val heightText = ui.txtHeight.text.toString()

            if (!isValidText(windowName)) {
                ui.txtWindowName.error = "Required"
                return@setOnClickListener
            }

            if (!isValidDouble(widthText)) {
                ui.txtWidth.error = "Enter a valid width"
                return@setOnClickListener
            }

            if (!isValidDouble(heightText)) {
                ui.txtHeight.error = "Enter a valid height"
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
            val height = heightText.toDouble()

            val area = (width * height) / 1_000_000
            val totalPrice = area * selectedProductPrice

            val window = hashMapOf(
                "roomId" to roomId,
                "windowName" to windowName.trim(),
                "productId" to selectedProductId,
                "productName" to selectedProductName,
                "pricePerSquareMeter" to selectedProductPrice,
                "width" to width,
                "height" to height,
                "area" to area,
                "totalPrice" to totalPrice,
                "notes" to ui.txtNotes.text.toString().trim(),
                "colour" to selectedColour
            )

            if (isEdit && windowId != null) {
                db.collection("windows")
                    .document(windowId)
                    .set(window)
                    .addOnSuccessListener {
                        finish()
                    }
            } else {
                db.collection("windows")
                    .add(window)
                    .addOnSuccessListener {
                        finish()
                    }
            }
        }
    }

    private fun isValidText(input: String): Boolean {
        return input.trim().isNotEmpty()
    }

    private fun isValidDouble(input: String): Boolean {
        val number = input.toDoubleOrNull()
        return number != null && number > 0
    }
}