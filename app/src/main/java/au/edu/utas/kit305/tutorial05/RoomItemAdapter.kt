package au.edu.utas.kit305.tutorial05

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.RoomItemBinding

class RoomItemAdapter(
    private val items: List<RoomItem>,
    private val onEdit: (RoomItem) -> Unit
) : RecyclerView.Adapter<RoomItemAdapter.ViewHolder>() {

    inner class ViewHolder(val ui: RoomItemBinding) : RecyclerView.ViewHolder(ui.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = RoomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ui)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.ui.txtName.text = item.name
        holder.ui.txtDetails.text = item.details
        holder.ui.txtPrice.text = "$${"%.2f".format(item.price)}"

        holder.ui.btnEdit.setOnClickListener {
            onEdit(item)
        }
    }
}