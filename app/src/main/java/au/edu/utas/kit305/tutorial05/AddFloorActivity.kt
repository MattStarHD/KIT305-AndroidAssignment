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
import android.widget.Toast

class AddFloorActivity : AppCompatActivity() {

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

            findViewById<Button>(R.id.btnChooseProduct).text = selectedProductName
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
        setContentView(R.layout.activity_add_floor)

        val db = FirebaseFirestore.getInstance()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val floorId = intent.getStringExtra("floorId")
        val isEdit = intent.getBooleanExtra("editMode", false)

        val txtWidth = findViewById<EditText>(R.id.txtWidth)
        val txtDepth = findViewById<EditText>(R.id.txtDepth)
        val txtNotes = findViewById<EditText>(R.id.txtNotes)

        val btnSaveFloor = findViewById<Button>(R.id.btnSaveFloor)
        val btnChooseProduct = findViewById<Button>(R.id.btnChooseProduct)
        val btnChooseColour = findViewById<Button>(R.id.btnChooseColour)
        val btnDelete = findViewById<ImageView>(R.id.btnDelete)

        findViewById<TextView>(R.id.lblHeaderTitle).text =
            if (isEdit) "Edit Floor" else "Add Floor"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE

        btnDelete.setOnClickListener {
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
                    txtWidth.setText((document.getDouble("width") ?: 0.0).toString())
                    txtDepth.setText((document.getDouble("depth") ?: 0.0).toString())
                    txtNotes.setText(document.getString("notes") ?: "")

                    selectedProductId = document.getString("productId") ?: ""
                    selectedProductName = document.getString("productName") ?: ""
                    selectedProductPrice = document.getDouble("pricePerSquareMeter") ?: 0.0
                    selectedColour = document.getString("colour") ?: ""

                    btnChooseProduct.text =
                        if (selectedProductName.isBlank()) "Choose Product" else selectedProductName

                    btnChooseColour.text =
                        if (selectedColour.isBlank()) "Choose Colour" else selectedColour
                }
        }

        btnChooseProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "floor")
            productLauncher.launch(intent)
        }

        btnChooseColour.setOnClickListener {
            val intent = Intent(this, ColourSelectorActivity::class.java)
            intent.putStringArrayListExtra("colours", availableColours)
            colourLauncher.launch(intent)
        }

        btnSaveFloor.setOnClickListener {
            val widthText = txtWidth.text.toString()
            val depthText = txtDepth.text.toString()

            if (!isValidDouble(widthText)) {
                txtWidth.error = "Enter a valid width"
                return@setOnClickListener
            }

            if (!isValidDouble(depthText)) {
                txtDepth.error = "Enter a valid depth"
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
                "notes" to txtNotes.text.toString().trim(),
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