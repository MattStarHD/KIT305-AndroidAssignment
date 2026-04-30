package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.app.AlertDialog
import android.view.View
import android.widget.TextView
import android.widget.ImageView

class AddWindowActivity : AppCompatActivity() {

    private var selectedProductId = ""
    private var selectedProductName = ""
    private var selectedProductPrice = 0.0

    private val productLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {

            selectedProductId = result.data?.getStringExtra("productId") ?: ""
            selectedProductName = result.data?.getStringExtra("productName") ?: ""
            selectedProductPrice = result.data?.getDoubleExtra("productPrice", 0.0) ?: 0.0

            // 👇 ADD THIS
            val variants =
                result.data?.getStringArrayListExtra("productVariants") ?: arrayListOf()

            // set button text
            findViewById<Button>(R.id.btnSelectProduct).text = selectedProductName

            // 👇 ADD THIS (spinner setup)
            val spinnerColour = findViewById<Spinner>(R.id.spinnerColour)

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                variants
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerColour.adapter = adapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_window)

        val roomId = intent.getStringExtra("roomId") ?: ""

        val txtWindowName = findViewById<EditText>(R.id.txtWindowName)
        val txtWidth = findViewById<EditText>(R.id.txtWidth)
        val txtHeight = findViewById<EditText>(R.id.txtHeight)
        val txtNotes = findViewById<EditText>(R.id.txtNotes)
        val btnSaveWindow = findViewById<Button>(R.id.btnSaveWindow)
        val btnSelectProduct = findViewById<Button>(R.id.btnSelectProduct)
        val isEdit = intent.getBooleanExtra("editMode", false)
        val db = FirebaseFirestore.getInstance()
        val windowId = intent.getStringExtra("windowId")
        val editMode = intent.getBooleanExtra("editMode", false)
        val title = findViewById<TextView>(R.id.lblHeaderTitle)
        val btnDelete = findViewById<ImageView>(R.id.btnDelete)


// Show trash icon only when editing
        if (isEdit) {
            btnDelete.visibility = View.VISIBLE
        }

        findViewById<TextView>(R.id.lblHeaderTitle).text =
            if (isEdit) "Edit Window" else "Add Window"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }


        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Window?")
                .setMessage("Are you sure you want to delete this window?")
                .setPositiveButton("Delete") { _, _ ->
                    val windowId = intent.getStringExtra("windowId") ?: ""

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

        if (editMode && windowId != null) {
            db.collection("windows")
                .document(windowId)
                .get()
                .addOnSuccessListener { document ->

                    val width = document.getDouble("width") ?: 0.0
                    val height = document.getDouble("height") ?: 0.0
                    val notes = document.getString("notes") ?: ""

                    txtWidth.setText(width.toString())
                    txtHeight.setText(height.toString())
                    txtNotes.setText(notes)

                    selectedProductId = document.getString("productId") ?: ""
                    selectedProductName = document.getString("productName") ?: ""
                    selectedProductPrice = document.getDouble("pricePerSquareMeter") ?: 0.0

                    btnSelectProduct.text = selectedProductName
                }
        }

        btnSelectProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "window")
            productLauncher.launch(intent)
        }

        btnSaveWindow.setOnClickListener {
            val width = txtWidth.text.toString().toDoubleOrNull() ?: 0.0
            val height = txtHeight.text.toString().toDoubleOrNull() ?: 0.0
            val spinnerColour = findViewById<Spinner>(R.id.spinnerColour)

            val area = (width * height) / 1_000_000
            val totalPrice = area * selectedProductPrice


            val window = hashMapOf(
                "roomId" to roomId,
                "productId" to selectedProductId,
                "productName" to selectedProductName,
                "pricePerSquareMeter" to selectedProductPrice,
                "width" to width,
                "height" to height,
                "area" to area,
                "totalPrice" to totalPrice,
                "notes" to txtNotes.text.toString(),
                "colour" to (spinnerColour.selectedItem?.toString() ?: ""),
            )

            if (editMode && windowId != null) {
                db.collection("windows")
                    .document(windowId)
                    .set(window)
                    .addOnSuccessListener { finish() }
            } else {
                db.collection("windows")
                    .add(window)
                    .addOnSuccessListener { finish() }
            }

            db.collection("windows")
                .add(window)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}