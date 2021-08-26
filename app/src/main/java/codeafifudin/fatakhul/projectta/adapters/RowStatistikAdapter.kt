package codeafifudin.fatakhul.projectta.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.models.RowStatus
import kotlinx.android.synthetic.main.row_statistik_presensi.view.*

class RowStatistikAdapter (items: MutableList<RowStatus>, context: Context): RecyclerView.Adapter<RowStatistikAdapter.ViewHolder>() {

    private val list = items
    private val context = context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RowStatistikAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_statistik_presensi, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RowStatistikAdapter.ViewHolder, position: Int) {
        if(list[position].status.equals("null")){
            holder.status.text = "Izin"
        }else if(list[position].status.equals("tm")){
            holder.status.text = "Tidak Masuk"
        }else if(list[position].status.equals("-")){
            holder.status.text = "Tepat Waktu"
        }else if(list[position].status.equals("masuk")){
            holder.status.text = "Masuk"
        }
        else{
            holder.status.text = list[position].status
        }
        holder.jumlah.text = list[position].jumlah.toString()
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val status = v.txtStatus
        val jumlah = v.txtJumlah
    }
}