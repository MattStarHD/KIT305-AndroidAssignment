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

        btnSelectProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "window")
            productLauncher.launch(intent)
        }

        val db = FirebaseFirestore.getInstance()

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
                "colour" to spinnerColour.selectedItem.toString(),
            )

        db.collection("windows")
            .add(window)
            .addOnSuccessListener {
                finish()
            }

            db.collection("windows")
                .add(window)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}