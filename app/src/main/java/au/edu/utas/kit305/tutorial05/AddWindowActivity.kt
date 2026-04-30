package au.edu.utas.kit305.tutorial05

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddWindowActivity : AppCompatActivity() {

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

            findViewById<Button>(R.id.btnSelectProduct).text = selectedProductName
            findViewById<Button>(R.id.btnChooseColour).text = "Choose Colour"

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
            findViewById<Button>(R.id.btnChooseColour).text = selectedColour
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_window)

        val db = FirebaseFirestore.getInstance()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val windowId = intent.getStringExtra("windowId")
        val isEdit = intent.getBooleanExtra("editMode", false)

        val txtWindowName = findViewById<EditText>(R.id.txtWindowName)
        val txtWidth = findViewById<EditText>(R.id.txtWidth)
        val txtHeight = findViewById<EditText>(R.id.txtHeight)
        val txtNotes = findViewById<EditText>(R.id.txtNotes)

        val btnSaveWindow = findViewById<Button>(R.id.btnSaveWindow)
        val btnSelectProduct = findViewById<Button>(R.id.btnSelectProduct)
        val btnChooseColour = findViewById<Button>(R.id.btnChooseColour)
        val btnDelete = findViewById<ImageView>(R.id.btnDelete)

        findViewById<TextView>(R.id.lblHeaderTitle).text =
            if (isEdit) "Edit Window" else "Add Window"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE

        btnDelete.setOnClickListener {
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
                    txtWindowName.setText(document.getString("windowName") ?: "")
                    txtWidth.setText((document.getDouble("width") ?: 0.0).toString())
                    txtHeight.setText((document.getDouble("height") ?: 0.0).toString())
                    txtNotes.setText(document.getString("notes") ?: "")

                    selectedProductId = document.getString("productId") ?: ""
                    selectedProductName = document.getString("productName") ?: ""
                    selectedProductPrice = document.getDouble("pricePerSquareMeter") ?: 0.0
                    selectedColour = document.getString("colour") ?: ""

                    btnSelectProduct.text =
                        if (selectedProductName.isBlank()) "Choose Product" else selectedProductName

                    btnChooseColour.text =
                        if (selectedColour.isBlank()) "Choose Colour" else selectedColour
                }
        }

        btnSelectProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "window")
            productLauncher.launch(intent)
        }

        btnChooseColour.setOnClickListener {
            val intent = Intent(this, ColourSelectorActivity::class.java)
            intent.putStringArrayListExtra("colours", availableColours)
            colourLauncher.launch(intent)
        }

        btnSaveWindow.setOnClickListener {
            val width = txtWidth.text.toString().toDoubleOrNull() ?: 0.0
            val height = txtHeight.text.toString().toDoubleOrNull() ?: 0.0

            val area = (width * height) / 1_000_000
            val totalPrice = area * selectedProductPrice

            val window = hashMapOf(
                "roomId" to roomId,
                "windowName" to txtWindowName.text.toString(),
                "productId" to selectedProductId,
                "productName" to selectedProductName,
                "pricePerSquareMeter" to selectedProductPrice,
                "width" to width,
                "height" to height,
                "area" to area,
                "totalPrice" to totalPrice,
                "notes" to txtNotes.text.toString(),
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
}