package psti.unram.spendo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TipsAdapter(private val tipsList: List<FinanceTip>) : RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipTitle: TextView = itemView.findViewById(R.id.tvTipTitle)
        val tvTipDescription: TextView = itemView.findViewById(R.id.tvTipDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = tipsList[position]
        holder.tvTipTitle.text = tip.title
        holder.tvTipDescription.text = tip.description
    }

    override fun getItemCount(): Int = tipsList.size
}