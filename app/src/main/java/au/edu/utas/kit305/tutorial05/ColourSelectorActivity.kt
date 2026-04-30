package au.edu.utas.kit305.tutorial05

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Typeface
import android.view.Gravity


class ColourSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_colour_selector)

        val colours = intent.getStringArrayListExtra("colours") ?: arrayListOf()

        findViewById<TextView>(R.id.lblHeaderTitle).text = "Select Colour"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val layout = findViewById<LinearLayout>(R.id.colourContainer)

        for (colour in colours) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = Gravity.CENTER_VERTICAL
            row.setPadding(24, 0, 24, 0)
            row.setBackgroundColor(0xFFFFFFFF.toInt())
            row.elevation = 4f

            val rowParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                130
            )
            rowParams.setMargins(0, 0, 0, 16)
            row.layoutParams = rowParams

            val colourText = TextView(this)
            colourText.text = colour
            colourText.textSize = 18f
            colourText.setTypeface(null, Typeface.BOLD)
            colourText.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            val arrow = TextView(this)
            arrow.text = ">"
            arrow.textSize = 30f
            arrow.setPadding(16, 0, 0, 0)

            row.addView(colourText)
            row.addView(arrow)

            row.setOnClickListener {
                val resultIntent = Intent()
                resultIntent.putExtra("selectedColour", colour)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }

            layout.addView(row)
        }
    }
}