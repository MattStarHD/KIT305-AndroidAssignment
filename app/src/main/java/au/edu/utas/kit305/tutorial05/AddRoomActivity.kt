package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog
import android.view.View
import android.widget.Toast

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
        val roomId = intent.getStringExtra("roomId")
        val editMode = intent.getBooleanExtra("editMode", false)
        val btnDeleteRoom = findViewById<Button>(R.id.btnDeleteRoom)

        if (editMode) {
            btnDeleteRoom.visibility = View.VISIBLE
        } else {
            btnDeleteRoom.visibility = View.GONE
        }

        btnDeleteRoom.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Room?")
                .setMessage("Are you sure you want to delete this room? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    if (roomId != null) {
                        db.collection("rooms")
                            .document(roomId)
                            .delete()
                            .addOnSuccessListener {
                                finish()
                            }
                    }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        if (editMode && roomId != null) {
            db.collection("rooms")
                .document(roomId)
                .get()
                .addOnSuccessListener { document ->
                    txtRoomName.setText(document.getString("roomName") ?: "")
                    txtRoomWidth.setText((document.getDouble("width") ?: 0.0).toString())
                    txtRoomDepth.setText((document.getDouble("depth") ?: 0.0).toString())
                }
        }

        btnSaveRoom.setOnClickListener {
            val room = hashMapOf(
                "houseId" to houseId,
                "roomName" to txtRoomName.text.toString(),
                "width" to txtRoomWidth.text.toString().toDoubleOrNull(),
                "depth" to txtRoomDepth.text.toString().toDoubleOrNull(),
                "notes" to ""
            )

            if (editMode && roomId != null) {
                db.collection("rooms")
                    .document(roomId)
                    .set(room)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            } else {
                db.collection("rooms")
                    .add(room)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Room saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
                .addOnFailureListener { e ->
                    android.widget.Toast.makeText(this, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
        }
    }
}