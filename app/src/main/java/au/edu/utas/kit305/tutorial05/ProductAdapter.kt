package au.edu.utas.kit305.tutorial05

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    inner class ProductHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtProductName: TextView = view.findViewById(R.id.txtProductName)
        val txtProductPrice: TextView = view.findViewById(R.id.txtProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_product_list, parent, false)

        return ProductHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.txtProductName.text = product.name
        holder.txtProductPrice.text = "$${product.pricePerSquareMeter} psm"

        holder.view.setOnClickListener {
            onProductClick(product)
        }
    }
}