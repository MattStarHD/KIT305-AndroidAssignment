package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.EditText
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import android.app.AlertDialog
import android.view.View
import android.widget.TextView
import android.widget.ImageView

class AddFloorActivity : AppCompatActivity() {

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

            val variants =
                result.data?.getStringArrayListExtra("productVariants") ?: arrayListOf()

            findViewById<Button>(R.id.btnChooseProduct).text = selectedProductName

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
        setContentView(R.layout.activity_add_floor)

        val roomId = intent.getStringExtra("roomId") ?: ""
        Log.d("ROOM_DEBUG", "Received roomId: $roomId")

        val txtWidth = findViewById<EditText>(R.id.txtWidth)
        val txtDepth = findViewById<EditText>(R.id.txtDepth)
        val txtNotes = findViewById<EditText>(R.id.txtNotes)
        val btnSaveFloor = findViewById<Button>(R.id.btnSaveFloor)
        val btnChooseProduct = findViewById<Button>(R.id.btnChooseProduct)
        val spinnerColour = findViewById<Spinner>(R.id.spinnerColour)
        val db = FirebaseFirestore.getInstance()
        val floorId = intent.getStringExtra("floorId")
        val editMode = intent.getBooleanExtra("editMode", false)
        val isEdit = intent.getBooleanExtra("editMode", false)
        val title = findViewById<TextView>(R.id.lblHeaderTitle)
        val btnDelete = findViewById<ImageView>(R.id.btnDelete)


        findViewById<TextView>(R.id.lblHeaderTitle).text =
            if (isEdit) "Edit Floor" else "Add Floor"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        if (isEdit) {
            btnDelete.visibility = View.VISIBLE
        }


        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Floor?")
                .setMessage("Are you sure you want to delete this floor? This cannot be undone.")
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

        if (editMode && floorId != null) {
            db.collection("floors")
                .document(floorId)
                .get()
                .addOnSuccessListener { document ->

                    val width = document.getDouble("width") ?: 0.0
                    val depth = document.getDouble("depth") ?: 0.0
                    val notes = document.getString("notes") ?: ""

                    txtWidth.setText(width.toString())
                    txtDepth.setText(depth.toString())
                    txtNotes.setText(notes)

                    selectedProductId = document.getString("productId") ?: ""
                    selectedProductName = document.getString("productName") ?: ""
                    selectedProductPrice = document.getDouble("pricePerSquareMeter") ?: 0.0

                    btnChooseProduct.text = selectedProductName
                }
        }

        btnChooseProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "floor")
            productLauncher.launch(intent)
        }

        btnSaveFloor.setOnClickListener {
            val width = txtWidth.text.toString().toDoubleOrNull() ?: 0.0
            val depth = txtDepth.text.toString().toDoubleOrNull() ?: 0.0

            val area = (width * depth) / 1_000_000   // mm → m²
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
                "notes" to txtNotes.text.toString(),
                "colour" to (spinnerColour.selectedItem?.toString() ?: ""),
            )


            Toast.makeText(
                this,
                "editMode=$editMode floorId=$floorId width=$width",
                Toast.LENGTH_LONG
            ).show()

            if (editMode && floorId != null) {
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
}