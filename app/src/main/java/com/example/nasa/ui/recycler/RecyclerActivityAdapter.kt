package com.example.nasa.ui.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nasa.R
import com.example.nasa.ui.recycler.Data.Companion.TYPE_EARTH
import com.example.nasa.ui.recycler.Data.Companion.TYPE_MARS

class RecyclerActivityAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var data: MutableList<Pair<Data, Boolean>>,
    private val dragListener: OnStartDragListener
) : RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EARTH -> EarthViewHolder(
                inflater.inflate(R.layout.activity_recycler_item_earth, parent, false) as View
            )
            TYPE_MARS ->
                MarsViewHolder(
                    inflater.inflate(R.layout.activity_recycler_item_mars, parent, false) as View
                )
            else -> HeaderViewHolder(
                inflater.inflate(R.layout.activity_recycler_item_header, parent, false) as View
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val combinedChange = createCombinedPayload(payloads as List<Change<Pair<Data, Boolean>>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData
            if (newData.first.someText != oldData.first.someText) {
                holder.itemView.findViewById<TextView>(R.id.marsTextView).text =
                    newData.first.someText
            }
        }
    }

    fun setItems(newItems: List<Pair<Data, Boolean>>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(data, newItems))
        result.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newItems)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].first.type
    }

    fun appendItem() {
        data.add(generateItem())
        notifyItemInserted(itemCount - 1)
    }

    private fun generateItem() = Pair(Data(TYPE_MARS, 1, ""), false)

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class EarthViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(data: Pair<Data, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<TextView>(R.id.descriptionTextView).text = data.first.someDescription
                itemView.findViewById<ImageView>(R.id.wikiImageView).setOnClickListener { onListItemClickListener.onItemClick(data.first) }
            }
        }
    }

    inner class MarsViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {
        override fun bind(data: Pair<Data, Boolean>) {
            itemView.findViewById<ImageView>(R.id.marsImageView).setOnClickListener { onListItemClickListener.onItemClick(data.first) }
            itemView.findViewById<ImageView>(R.id.addItemImageView).setOnClickListener { addItem() }
            itemView.findViewById<ImageView>(R.id.removeItemImageView).setOnClickListener { removeItem() }
            itemView.findViewById<ImageView>(R.id.moveItemDown).setOnClickListener { moveDown() }
            itemView.findViewById<ImageView>(R.id.moveItemUp).setOnClickListener { moveUp() }
            itemView.findViewById<TextView>(R.id.marsDescriptionTextView).visibility = if (data.second) View.VISIBLE else View.GONE
            itemView.findViewById<TextView>(R.id.marsTextView).setOnClickListener { toggleText() }
            itemView.findViewById<ImageView>(R.id.dragHandleImageView).setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition - 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < data.size - 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition + 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        private fun addItem() {
            data.add(layoutPosition, generateItem())
            notifyItemInserted(layoutPosition)
        }

        private fun removeItem() {
            data.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    inner class HeaderViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.setOnClickListener {
                data[1] = Pair(Data(TYPE_MARS, 1, ""), false)
                notifyItemChanged(1, Pair(Data(TYPE_MARS, 1, ""), false))
            }
        }
    }
}
