package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.kit305.tutorial05.databinding.ActivityQuoteBinding
import com.google.firebase.firestore.FirebaseFirestore

class QuoteActivity : AppCompatActivity() {

    private lateinit var ui: ActivityQuoteBinding
    private lateinit var db: FirebaseFirestore

    private val selectedPrices = mutableMapOf<String, Double>()
    private val roomItems = mutableMapOf<String, MutableList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityQuoteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        db = FirebaseFirestore.getInstance()

        ui.btnBack.setOnClickListener {
            finish()
        }

        val houseId = intent.getStringExtra("houseId") ?: ""
        val houseName = intent.getStringExtra("houseName") ?: "House Quote"

        ui.lblQuoteTitle.text = "Quote for $houseName"

        loadQuote(houseId)
    }

    private fun loadQuote(houseId: String) {
        ui.quoteContainer.removeAllViews()
        selectedPrices.clear()
        roomItems.clear()

        db.collection("rooms")
            .whereEqualTo("houseId", houseId)
            .get()
            .addOnSuccessListener { roomsResult ->

                for (roomDoc in roomsResult) {
                    val roomId = roomDoc.id
                    val roomName = roomDoc.getString("roomName") ?: "Room"

                    roomItems[roomId] = mutableListOf()

                    val roomBox = LinearLayout(this)
                    roomBox.orientation = LinearLayout.VERTICAL
                    roomBox.setPadding(12, 12, 12, 12)
                    roomBox.setBackgroundColor(0xFFE0E0E0.toInt())

                    val roomCheckBox = CheckBox(this)
                    roomCheckBox.text = "$roomName - Labour $200.00"
                    roomCheckBox.textSize = 18f
                    roomCheckBox.isChecked = true

                    val labourKey = "labour_$roomId"
                    selectedPrices[labourKey] = 200.0
                    roomItems[roomId]?.add(labourKey)

                    roomBox.addView(roomCheckBox)

                    val roomTotalText = TextView(this)
                    roomTotalText.text = "Room Total: $200.00"
                    roomTotalText.textSize = 16f
                    roomTotalText.setPadding(8, 4, 8, 12)
                    roomBox.addView(roomTotalText)

                    val floorTitle = TextView(this)
                    floorTitle.text = "Floor Spaces"
                    floorTitle.textSize = 16f
                    floorTitle.setPadding(8, 12, 8, 4)
                    roomBox.addView(floorTitle)

                    val floorContainer = LinearLayout(this)
                    floorContainer.orientation = LinearLayout.VERTICAL
                    roomBox.addView(floorContainer)

                    val windowTitle = TextView(this)
                    windowTitle.text = "Windows"
                    windowTitle.textSize = 16f
                    windowTitle.setPadding(8, 12, 8, 4)
                    roomBox.addView(windowTitle)

                    val windowContainer = LinearLayout(this)
                    windowContainer.orientation = LinearLayout.VERTICAL
                    roomBox.addView(windowContainer)

                    ui.quoteContainer.addView(roomBox)

                    roomCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        val keys = roomItems[roomId] ?: mutableListOf()

                        if (isChecked) {
                            for (key in keys) {
                                if (key == labourKey) {
                                    selectedPrices[key] = 200.0
                                } else {
                                    val checkBox = roomBox.findViewWithTag<CheckBox>(key)

                                    if (checkBox != null && checkBox.isChecked) {
                                        val priceText = checkBox.text.toString()
                                            .substringAfterLast("Price: $")

                                        selectedPrices[key] = priceText.toDoubleOrNull() ?: 0.0
                                    }
                                }
                            }
                        } else {
                            for (key in keys) {
                                selectedPrices.remove(key)
                            }
                        }

                        floorContainer.isEnabled = isChecked
                        windowContainer.isEnabled = isChecked

                        updateTotal()
                        updateRoomTotal(roomTotalText, roomId)
                    }

                    loadFloors(roomId, floorContainer, roomTotalText)
                    loadWindows(roomId, windowContainer, roomTotalText)
                }

                updateTotal()
            }
    }

    private fun loadFloors(roomId: String, container: LinearLayout, roomTotalText: TextView) {
        db.collection("floors")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->

                for (doc in result) {
                    val productName = doc.getString("productName") ?: "Floor Space"
                    val width = doc.getDouble("width") ?: 0.0
                    val depth = doc.getDouble("depth") ?: 0.0
                    val colour = doc.getString("colour") ?: ""
                    val price = doc.getDouble("totalPrice") ?: 0.0

                    val itemKey = "floor_${doc.id}"
                    selectedPrices[itemKey] = price
                    roomItems[roomId]?.add(itemKey)

                    val checkBox = CheckBox(this)
                    checkBox.tag = itemKey
                    checkBox.text =
                        "$productName\n${width}mm x ${depth}mm\nColour: $colour\nPrice: $${"%.2f".format(price)}"
                    checkBox.isChecked = true
                    checkBox.setPadding(16, 8, 8, 8)
                    checkBox.setBackgroundColor(0xFFFFFFFF.toInt())

                    container.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedPrices[itemKey] = price
                        } else {
                            selectedPrices.remove(itemKey)
                        }

                        updateTotal()
                        updateRoomTotal(roomTotalText, roomId)
                    }
                }

                updateTotal()
                updateRoomTotal(roomTotalText, roomId)
            }
    }

    private fun loadWindows(roomId: String, container: LinearLayout, roomTotalText: TextView) {
        db.collection("windows")
            .whereEqualTo("roomId", roomId)
            .get()
            .addOnSuccessListener { result ->

                for (doc in result) {
                    val windowName = doc.getString("windowName") ?: "Window"
                    val productName = doc.getString("productName") ?: "Window Product"
                    val width = doc.getDouble("width") ?: 0.0
                    val height = doc.getDouble("height") ?: 0.0
                    val colour = doc.getString("colour") ?: ""
                    val price = doc.getDouble("totalPrice") ?: 0.0

                    val itemKey = "window_${doc.id}"
                    selectedPrices[itemKey] = price
                    roomItems[roomId]?.add(itemKey)

                    val checkBox = CheckBox(this)
                    checkBox.tag = itemKey
                    checkBox.text =
                        "$productName\n$windowName\n${width}mm x ${height}mm\nColour: $colour\nPrice: $${"%.2f".format(price)}"
                    checkBox.isChecked = true
                    checkBox.setPadding(16, 8, 8, 8)
                    checkBox.setBackgroundColor(0xFFFFFFFF.toInt())

                    container.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedPrices[itemKey] = price
                        } else {
                            selectedPrices.remove(itemKey)
                        }

                        updateTotal()
                        updateRoomTotal(roomTotalText, roomId)
                    }
                }

                updateTotal()
                updateRoomTotal(roomTotalText, roomId)
            }
    }

    private fun updateRoomTotal(roomTotalText: TextView, roomId: String) {
        val keys = roomItems[roomId] ?: mutableListOf()
        val total = keys.sumOf { selectedPrices[it] ?: 0.0 }
        roomTotalText.text = "Room Total: $${"%.2f".format(total)}"
    }

    private fun updateTotal() {
        val total = selectedPrices.values.sum()
        ui.lblQuoteTotal.text = "Final Quote Total: $${"%.2f".format(total)}"
    }
}