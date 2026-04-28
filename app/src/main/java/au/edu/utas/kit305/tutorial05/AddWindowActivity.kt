package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent

class AddWindowActivity : AppCompatActivity() {

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
            startActivity(intent)
        }

        val db = FirebaseFirestore.getInstance()

        btnSaveWindow.setOnClickListener {
            val width = txtWidth.text.toString().toDoubleOrNull()
            val height = txtHeight.text.toString().toDoubleOrNull()

            val area = if (width != null && height != null) {
                (width * height) / 1_000_000
            } else {
                0.0
            }

            val window = hashMapOf(
                "roomId" to roomId,
                "windowName" to txtWindowName.text.toString(),
                "width" to width,
                "height" to height,
                "notes" to txtNotes.text.toString(),
                "area" to area,
                "totalPrice" to 0.0
            )

            db.collection("windows")
                .add(window)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}