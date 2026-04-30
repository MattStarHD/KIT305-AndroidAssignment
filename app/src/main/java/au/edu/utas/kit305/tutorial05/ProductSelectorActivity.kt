package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import au.edu.utas.kit305.tutorial05.databinding.ActivityBaseProductSelectorScreenBinding
import org.json.JSONObject

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

        val allProducts = parseProducts(jsonString)

        val products = allProducts.filter {
            it.type == type
        }

        ui.headerBar.lblHeaderTitle.text = "Select Product"

        ui.headerBar.btnBack.setOnClickListener {
            finish()
        }

        ui.recyclerProducts.layoutManager = LinearLayoutManager(this)

        ui.recyclerProducts.adapter = ProductAdapter(products) { product ->
            val resultIntent = Intent()

            resultIntent.putExtra("productId", product.id)
            resultIntent.putExtra("productName", product.name)
            resultIntent.putExtra("productPrice", product.pricePerSquareMeter)

            resultIntent.putStringArrayListExtra(
                "productVariants",
                ArrayList(product.variants)
            )

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun parseProducts(jsonString: String): List<Product> {
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