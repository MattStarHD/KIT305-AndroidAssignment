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

        btnChooseProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "floor")
            productLauncher.launch(intent)
        }


        val db = FirebaseFirestore.getInstance()

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
                "colour" to spinnerColour.selectedItem.toString(),
            )


            db.collection("floors")
                .add(floor)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}