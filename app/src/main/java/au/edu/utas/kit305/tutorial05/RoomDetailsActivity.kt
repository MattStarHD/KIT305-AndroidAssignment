package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.ImageView
import android.content.Intent
import android.widget.Button

class RoomDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_details)
        val roomId = intent.getStringExtra("roomId") ?: ""
        val roomName = intent.getStringExtra("roomName") ?: "Room"

        findViewById<TextView>(R.id.lblRoomTitle).text = roomName

        findViewById<Button>(R.id.btnAddFloor).setOnClickListener {
            val intent = Intent(this, AddFloorActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAddWindows).setOnClickListener {
            val intent = Intent(this, AddWindowActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}