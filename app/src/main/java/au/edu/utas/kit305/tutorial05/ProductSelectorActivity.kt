package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class ProductSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_product_selector_screen)

        val recyclerProducts = findViewById<RecyclerView>(R.id.recyclerProducts)

        val jsonString = assets.open("products.json")
            .bufferedReader()
            .use { it.readText() }

        val type = intent.getStringExtra("type") ?: ""

        val allProducts = parseProducts(jsonString)

        val products = allProducts.filter {
            it.type == type
        }

        recyclerProducts.layoutManager = LinearLayoutManager(this)
        recyclerProducts.adapter = ProductAdapter(products) { product ->
            val resultIntent = android.content.Intent()

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

            // 👉 ADD THIS BLOCK HERE
            val variantsArray = item.getJSONArray("variants")
            val variants = mutableListOf<String>()

            for (j in 0 until variantsArray.length()) {
                variants.add(variantsArray.getString(j))
            }

            // 👉 THEN KEEP THIS (but add variants at the end)
            products.add(
                Product(
                    id = item.getString("id"),
                    type = item.getString("category"),
                    name = item.getString("name"),
                    pricePerSquareMeter = item.getDouble("price_per_sqm"),
                    description = item.getString("description"),
                    variants = variants   // 👈 ADD THIS LINE
                )
            )
        }

        return products
    }
}