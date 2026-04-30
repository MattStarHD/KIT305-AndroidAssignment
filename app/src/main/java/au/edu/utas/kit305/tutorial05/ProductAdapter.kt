package au.edu.utas.kit305.tutorial05

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    inner class ProductHolder(val ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val ui = MyListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(ui)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.ui.txtName.text = product.name
        holder.ui.txtArrow.text = "$${product.pricePerSquareMeter} psm"

        holder.ui.root.setOnClickListener {
            onProductClick(product)
        }
    }
}