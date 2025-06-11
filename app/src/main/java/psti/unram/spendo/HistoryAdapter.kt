package psti.unram.spendo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class HistoryAdapter(private val list: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvKategori: TextView = itemView.findViewById(R.id.tvKategori)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvTanggal.text = item.tanggal
        holder.tvKategori.text = item.kategori
        holder.tvJumlah.text = item.jumlah
    }

    override fun getItemCount(): Int = list.size
}