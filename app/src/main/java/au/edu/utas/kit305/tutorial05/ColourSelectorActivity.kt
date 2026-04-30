package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityColourSelectorBinding

class ColourSelectorActivity : AppCompatActivity() {

    private lateinit var ui: ActivityColourSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityColourSelectorBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val colours = intent.getStringArrayListExtra("productVariants") ?: arrayListOf()
        val returnTo = intent.getStringExtra("returnTo") ?: ""

        ui.headerBar.lblHeaderTitle.text = "Select Colour"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        for (colour in colours) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(24, 0, 24, 0)
                setBackgroundColor(0xFFFFFFFF.toInt())
                elevation = 4f

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    130
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            }

            val colourText = TextView(this).apply {
                text = colour
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val arrow = TextView(this).apply {
                text = ">"
                textSize = 30f
                setPadding(16, 0, 0, 0)
            }

            row.addView(colourText)
            row.addView(arrow)

            row.setOnClickListener {
                val nextIntent = when (returnTo) {
                    "AddFloor" -> Intent(this, AddFloorActivity::class.java)
                    "EditFloor" -> Intent(this, EditFloorActivity::class.java)
                    "AddWindow" -> Intent(this, AddWindowActivity::class.java)
                    "EditWindow" -> Intent(this, EditWindowActivity::class.java)
                    else -> Intent(this, AddFloorActivity::class.java)
                }

                val roomId = intent.getStringExtra("roomId") ?: ""
                val floorId = intent.getStringExtra("floorId") ?: ""
                val windowId = intent.getStringExtra("windowId") ?: ""
                val width = intent.getStringExtra("width") ?: ""
                val depth = intent.getStringExtra("depth") ?: ""
                val height = intent.getStringExtra("height") ?: ""
                val notes = intent.getStringExtra("notes") ?: ""
                val windowName = intent.getStringExtra("windowName") ?: ""
                val productId = intent.getStringExtra("productId") ?: ""
                val productName = intent.getStringExtra("productName") ?: ""
                val productPrice = intent.getDoubleExtra("productPrice", 0.0)
                val productVariants = intent.getStringArrayListExtra("productVariants") ?: arrayListOf()

                nextIntent.putExtra("returnTo", returnTo)
                nextIntent.putExtra("roomId", roomId)
                nextIntent.putExtra("floorId", floorId)
                nextIntent.putExtra("windowId", windowId)
                nextIntent.putExtra("width", width)
                nextIntent.putExtra("depth", depth)
                nextIntent.putExtra("height", height)
                nextIntent.putExtra("notes", notes)
                nextIntent.putExtra("windowName", windowName)
                nextIntent.putExtra("productId", productId)
                nextIntent.putExtra("productName", productName)
                nextIntent.putExtra("productPrice", productPrice)
                nextIntent.putStringArrayListExtra("productVariants", productVariants)
                nextIntent.putExtra("selectedColour", colour)

                startActivity(nextIntent)
                finish()
            }

            ui.colourContainer.addView(row)
        }
    }
}