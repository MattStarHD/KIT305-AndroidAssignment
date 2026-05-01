package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONObject
import au.edu.utas.kit305.tutorial05.databinding.ActivityBaseProductSelectorScreenBinding

class ProductSelectorActivity : AppCompatActivity() {

    private lateinit var ui: ActivityBaseProductSelectorScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityBaseProductSelectorScreenBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val jsonString = assets.open("products.json")
            .bufferedReader()
            .use { it.readText() }

        val type = intent.getStringExtra("type") ?: ""
        val returnTo = intent.getStringExtra("returnTo") ?: ""
        val roomId = intent.getStringExtra("roomId") ?: ""
        val floorId = intent.getStringExtra("floorId") ?: ""
        val windowId = intent.getStringExtra("windowId") ?: ""
        val windowName = intent.getStringExtra("windowName") ?: ""
        val height = intent.getStringExtra("height") ?: ""
        val width = intent.getStringExtra("width") ?: ""
        val depth = intent.getStringExtra("depth") ?: ""
        val notes = intent.getStringExtra("notes") ?: ""
        val allProducts = readProducts(jsonString)
        val products = allProducts.filter { it.type == type }

        ui.headerBar.lblHeaderTitle.text = "Select Product"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.recyclerProducts.layoutManager = LinearLayoutManager(this)

        ui.recyclerProducts.adapter = ProductAdapter(products) { product ->

            val intent = Intent(this, ColourSelectorActivity::class.java)

            intent.putExtra("returnTo", returnTo)
            intent.putExtra("roomId", roomId)
            intent.putExtra("floorId", floorId)
            intent.putExtra("windowId", windowId)
            intent.putExtra("width", width)
            intent.putExtra("windowName", windowName)
            intent.putExtra("height", height)
            intent.putExtra("depth", depth)
            intent.putExtra("notes", notes)
            intent.putExtra("productId", product.id)
            intent.putExtra("productName", product.name)
            intent.putExtra("productPrice", product.pricePerSquareMeter)
            intent.putStringArrayListExtra("productVariants", ArrayList(product.variants))

            startActivity(intent)
            finish()
        }
    }

    private fun readProducts(jsonString: String): List<Product> {
        val products = mutableListOf<Product>()
        val jsonObject = JSONObject(jsonString)
        val dataArray = jsonObject.getJSONArray("data")

        for (i in 0 until dataArray.length()) {

            val item = dataArray.getJSONObject(i)
            val variantsArray = item.getJSONArray("variants")
            val variants = mutableListOf<String>()

            for (j in 0 until variantsArray.length()) {
                variants.add(variantsArray.getString(j))
            }

            products.add(
                Product(
                    id = item.getString("id"),
                    type = item.getString("category"),
                    name = item.getString("name"),
                    pricePerSquareMeter = item.getDouble("price_per_sqm"),
                    description = item.getString("description"),
                    variants = variants
                )
            )
        }

        return products
    }
}