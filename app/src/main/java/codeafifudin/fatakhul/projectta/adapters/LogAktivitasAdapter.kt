package codeafifudin.fatakhul.projectta.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.models.Aktivitas
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.views.aktivitas.AktivitasFragment
import kotlinx.android.synthetic.main.row_aktivitas.view.*

class LogAktivitasAdapter(items: MutableList<Aktivitas>, context: Context, aktivitasFragment: AktivitasFragment): RecyclerView.Adapter<LogAktivitasAdapter.ViewHolder>() {

    private val list = items
    private val context = context
    private val aktivitasFragment = aktivitasFragment
    var conveter: Conveter ?= null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogAktivitasAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_aktivitas,parent,false))
    }

    override fun onBindViewHolder(holder: LogAktivitasAdapter.ViewHolder, position: Int) {
        conveter = Conveter(context)
        holder.setIsRecyclable(false)
        holder.tgl.text = list[position].tgl_aktivitas
        var no = 1
        holder.textViewNoAktivitas.text = "No."+no.toString()
        if(position > 0){
            if(list[position].tgl_aktivitas.equals(list[position-1].tgl_aktivitas)){
                no++
                holder.textViewNoAktivitas.text = "No."+no.toString()
                holder.tgl.visibility = View.GONE
            }
        }
        holder.uraian.text = list[position].uraian
        holder.kuantitas.text = list[position].kuantitas
        holder.satuan.text = list[position].satuan
        holder.btnFile.setOnClickListener {
            aktivitasFragment.gotoOpenFile(list[position].file,list[position].tipe)
        }

    }

    override fun getItemCount(): Int {
        return list.count()
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val tgl = v.tglAktivitas
        val uraian = v.txtUraian
        val kuantitas = v.txtKuantitas
        val satuan = v.txtSatuan
        val textViewNoAktivitas = v.textViewNoAktivitas
        val btnFile = v.fileAktivitas
    }

}