package au.edu.utas.kit305.tutorial05

import android.app.Activity
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

        val colours = intent.getStringArrayListExtra("colours") ?: arrayListOf()

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
                val resultIntent = Intent()
                resultIntent.putExtra("selectedColour", colour)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }

            ui.colourContainer.addView(row)
        }
    }
}