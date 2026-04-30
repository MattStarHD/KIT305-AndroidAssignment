package au.edu.utas.kit305.tutorial05

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddRoomBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditRoomActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddRoomBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val houseId = intent.getStringExtra("houseId") ?: ""
        val roomId = intent.getStringExtra("roomId") ?: ""

        ui.headerBar.lblHeaderTitle.text = "Edit Room"
        ui.headerBar.btnDelete.visibility = View.VISIBLE

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        // load existing room details
        db.collection("rooms")
            .document(roomId)
            .get()
            .addOnSuccessListener { document ->
                ui.txtRoomName.setText(document.getString("roomName") ?: "")
                ui.txtRoomWidth.setText((document.getDouble("width") ?: 0.0).toString())
                ui.txtRoomDepth.setText((document.getDouble("depth") ?: 0.0).toString())
            }

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Room?")
                .setMessage("Are you sure you want to delete this room? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    db.collection("rooms")
                        .document(roomId)
                        .delete()
                        .addOnSuccessListener {
                            finish()
                        }
                }
                .setNegativeButton("Keep", null)
                .show()
        }

        ui.btnSaveRoom.setOnClickListener {
            val roomName = ui.txtRoomName.text.toString().trim()
            val widthText = ui.txtRoomWidth.text.toString().trim()
            val depthText = ui.txtRoomDepth.text.toString().trim()

            val width = widthText.toDoubleOrNull()
            val depth = depthText.toDoubleOrNull()

            // check required fields
            if (roomName.isEmpty()) {
                ui.txtRoomName.error = "Required"
                return@setOnClickListener
            }

            if (width == null || width <= 0) {
                ui.txtRoomWidth.error = "Enter a valid width"
                return@setOnClickListener
            }

            if (depth == null || depth <= 0) {
                ui.txtRoomDepth.error = "Enter a valid depth"
                return@setOnClickListener
            }

            val room = hashMapOf(
                "houseId" to houseId,
                "roomName" to roomName,
                "width" to width,
                "depth" to depth,
                "notes" to ""
            )

            db.collection("rooms")
                .document(roomId)
                .set(room)
                .addOnSuccessListener {
                    Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun isValidText(input: String): Boolean {
        return input.trim().isNotEmpty()
    }
}