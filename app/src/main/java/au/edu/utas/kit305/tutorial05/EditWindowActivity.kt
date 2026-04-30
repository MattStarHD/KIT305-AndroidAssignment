package au.edu.utas.kit305.tutorial05

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddWindowBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditWindowActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddWindowBinding

    private var selectedProductId = ""
    private var selectedProductName = ""
    private var selectedProductPrice = 0.0
    private var selectedColour = ""
    private var availableColours = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddWindowBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val windowId = intent.getStringExtra("windowId") ?: ""

        selectedProductId = intent.getStringExtra("productId") ?: ""
        selectedProductName = intent.getStringExtra("productName") ?: ""
        selectedProductPrice = intent.getDoubleExtra("productPrice", 0.0)
        selectedColour = intent.getStringExtra("selectedColour") ?: ""
        availableColours = intent.getStringArrayListExtra("productVariants") ?: arrayListOf()

        ui.headerBar.lblHeaderTitle.text = "Edit Window"
        ui.headerBar.btnDelete.visibility = View.VISIBLE

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Window?")
                .setMessage("Are you sure you want to delete this window?")
                .setPositiveButton("Delete") { _, _ ->
                    db.collection("windows")
                        .document(windowId)
                        .delete()
                        .addOnSuccessListener {
                            finish()
                        }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        // load existing window details
        if (selectedProductId.isBlank()) {
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
        } else {
            ui.txtWindowName.setText(intent.getStringExtra("windowName") ?: "")
            ui.txtWidth.setText(intent.getStringExtra("width") ?: "")
            ui.txtHeight.setText(intent.getStringExtra("height") ?: "")
            ui.txtNotes.setText(intent.getStringExtra("notes") ?: "")

            ui.btnSelectProduct.text = selectedProductName
            ui.btnChooseColour.text =
                if (selectedColour.isBlank()) "Choose Colour" else selectedColour
        }

        ui.btnSelectProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)

            val windowName = ui.txtWindowName.text.toString()
            val width = ui.txtWidth.text.toString()
            val height = ui.txtHeight.text.toString()
            val notes = ui.txtNotes.text.toString()

            intent.putExtra("type", "window")
            intent.putExtra("returnTo", "EditWindow")
            intent.putExtra("roomId", roomId)
            intent.putExtra("windowId", windowId)

            intent.putExtra("windowName", windowName)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
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

            val windowName = ui.txtWindowName.text.toString()
            val width = ui.txtWidth.text.toString()
            val height = ui.txtHeight.text.toString()
            val notes = ui.txtNotes.text.toString()

            intent.putExtra("returnTo", "EditWindow")
            intent.putExtra("roomId", roomId)
            intent.putExtra("windowId", windowId)

            intent.putExtra("windowName", windowName)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            intent.putExtra("notes", notes)

            intent.putExtra("productId", selectedProductId)
            intent.putExtra("productName", selectedProductName)
            intent.putExtra("productPrice", selectedProductPrice)
            intent.putStringArrayListExtra("productVariants", availableColours)

            startActivity(intent)
            finish()
        }

        ui.btnSaveWindow.setOnClickListener {
            val windowName = ui.txtWindowName.text.toString().trim()
            val widthText = ui.txtWidth.text.toString().trim()
            val heightText = ui.txtHeight.text.toString().trim()

            val width = widthText.toDoubleOrNull()
            val height = heightText.toDoubleOrNull()

            if (windowName.isEmpty()) {
                ui.txtWindowName.error = "Required"
                return@setOnClickListener
            }

            if (width == null || width <= 0) {
                ui.txtWidth.error = "Enter a valid width"
                return@setOnClickListener
            }

            if (height == null || height <= 0) {
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

            val area = (width * height) / 1000000
            val totalPrice = area * selectedProductPrice

            val window = hashMapOf(
                "roomId" to roomId,
                "windowName" to windowName,
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

            db.collection("windows")
                .document(windowId)
                .set(window)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}