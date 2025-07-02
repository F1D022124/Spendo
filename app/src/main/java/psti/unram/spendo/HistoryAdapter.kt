package psti.unram.spendo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class HistoryAdapter(private var historyList: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tvNamaBarang)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        val context = holder.itemView.context

        // Format harga
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,###", symbols)
        val hargaFormatted = "Rp${formatter.format(historyItem.harga)}"

        holder.tvNamaBarang.text = historyItem.namaBarang
        holder.tvHarga.text = hargaFormatted
        holder.tvTanggal.text = historyItem.tanggal
        holder.tvStatus.text = historyItem.hasilRekomendasi

        // Set ikon dan warna berdasarkan hasil rekomendasi
        when (historyItem.hasilRekomendasi) {
            "Direkomendasikan" -> {
                holder.ivStatus.setImageResource(R.drawable.baseline_shopping_cart_24)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.green))
            }
            "Tunda" -> {
                holder.ivStatus.setImageResource(R.drawable.baseline_hourglass_empty_24)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.yellow))
            }
            "Tidak Direkomendasikan" -> {
                holder.ivStatus.setImageResource(R.drawable.baseline_block_24)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.red))
            }
            else -> {
                holder.ivStatus.setImageResource(R.drawable.baseline_error_24)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.grey))
                holder.ivStatus.setColorFilter(ContextCompat.getColor(context, R.color.grey))
            }
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newList: List<HistoryItem>) {
        historyList = newList
        notifyDataSetChanged()
    }
}