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

class AddFloorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_floor)

        val roomId = intent.getStringExtra("roomId") ?: ""

        val txtWidth = findViewById<EditText>(R.id.txtWidth)
        val txtDepth = findViewById<EditText>(R.id.txtDepth)
        val txtNotes = findViewById<EditText>(R.id.txtNotes)
        val btnSaveFloor = findViewById<Button>(R.id.btnSaveFloor)
        val btnChooseProduct = findViewById<Button>(R.id.btnChooseProduct)

        btnChooseProduct.setOnClickListener {
            val intent = Intent(this, ProductSelectorActivity::class.java)
            intent.putExtra("type", "floor")
            startActivity(intent)
        }


        val db = FirebaseFirestore.getInstance()

        btnSaveFloor.setOnClickListener {
            val width = txtWidth.text.toString().toDoubleOrNull()
            val depth = txtDepth.text.toString().toDoubleOrNull()

            val area = if (width != null && depth != null) {
                (width * depth) / 1_000_000   // mm → m²
            } else 0.0

            val floor = hashMapOf(
                "roomId" to roomId,
                "width" to width,
                "depth" to depth,
                "notes" to txtNotes.text.toString(),
                "area" to area,
                "totalPrice" to 0.0
            )

            db.collection("floors")
                .add(floor)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}