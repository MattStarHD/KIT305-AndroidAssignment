package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore

class AddRoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room)

        val houseId = intent.getStringExtra("houseId") ?: ""

        val txtRoomName = findViewById<EditText>(R.id.txtRoomName)
        val txtRoomWidth = findViewById<EditText>(R.id.txtRoomWidth)
        val txtRoomDepth = findViewById<EditText>(R.id.txtRoomDepth)
        val btnSaveRoom = findViewById<Button>(R.id.btnSaveRoom)

        val db = FirebaseFirestore.getInstance()

        btnSaveRoom.setOnClickListener {
            val room = hashMapOf(
                "houseId" to houseId,
                "roomName" to txtRoomName.text.toString(),
                "width" to txtRoomWidth.text.toString().toDoubleOrNull(),
                "depth" to txtRoomDepth.text.toString().toDoubleOrNull(),
                "notes" to ""
            )

            db.collection("rooms")
                .add(room)
                .addOnSuccessListener {
                    finish()
                }
        }
    }
}