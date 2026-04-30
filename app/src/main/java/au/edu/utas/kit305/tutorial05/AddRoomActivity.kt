package au.edu.utas.kit305.tutorial05

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityAddRoomBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddRoomActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAddRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityAddRoomBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val db = FirebaseFirestore.getInstance()

        val houseId = intent.getStringExtra("houseId") ?: ""
        val roomId = intent.getStringExtra("roomId")
        val isEdit = intent.getBooleanExtra("editMode", false)

        ui.headerBar.lblHeaderTitle.text = if (isEdit) "Edit Room" else "Add Room"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.headerBar.btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE

        ui.headerBar.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Room?")
                .setMessage("Are you sure you want to delete this room?")
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

        // load room input when editing
        if (isEdit && roomId != null) {
            db.collection("rooms")
                .document(roomId)
                .get()
                .addOnSuccessListener { document ->
                    ui.txtRoomName.setText(document.getString("roomName") ?: "")
                    ui.txtRoomWidth.setText((document.getDouble("width") ?: 0.0).toString())
                    ui.txtRoomDepth.setText((document.getDouble("depth") ?: 0.0).toString())
                }
        }

        ui.btnSaveRoom.setOnClickListener {
            val roomName = ui.txtRoomName.text.toString().trim()
            val widthText = ui.txtRoomWidth.text.toString().trim()
            val depthText = ui.txtRoomDepth.text.toString().trim()

            val width = widthText.toDoubleOrNull()
            val depth = depthText.toDoubleOrNull()

            // check room details
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

            if (isEdit && roomId != null) {
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
            } else {
                db.collection("rooms")
                    .add(room)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Room saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}