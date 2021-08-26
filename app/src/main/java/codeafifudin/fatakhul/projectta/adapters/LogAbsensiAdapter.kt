package codeafifudin.fatakhul.projectta.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.views.absensi.RiwayatAbsensiFragment
import kotlinx.android.synthetic.main.row_presensi.view.*

class LogAbsensiAdapter(items: MutableList<Presensi>, context: Context, riwayatAbsensiFragment: RiwayatAbsensiFragment): RecyclerView.Adapter<LogAbsensiAdapter.ViewHolder>() {

    private val list = items
    private val context = context
    private val riwayatAbsensiFragment = riwayatAbsensiFragment
    var conveter: Conveter ?= null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogAbsensiAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_presensi,parent,false))
    }

    override fun onBindViewHolder(holder: LogAbsensiAdapter.ViewHolder, position: Int) {
        conveter = Conveter(context)

        holder.setIsRecyclable(false)
        holder.tgl.text = list[position].tgl
        holder.btnEditIzin.visibility = View.GONE
        if(list[position].keterangan.equals("null")){
            holder.keterangan.text = "-"
        }else{
            holder.keterangan.text = list[position].keterangan
        }

        if(list[position].tipeIzin.equals("dinas_luar")){
            holder.tipeIzin.text = "Dinas Luar"
        }else if(list[position].tipeIzin.equals("sakit")){
            holder.tipeIzin.text = "Sakit"
        }else{
            holder.tipeIzin.text = "Lainnya"
        }
        if(list[position].tipeFileIzin.equals("null") && list[position].tipeIzin.equals("sakit")){
            holder.fileIzin.text = "Belum ada"
            holder.fileIzin.setTextColor(context.resources.getColor(R.color.warning))
            holder.btnEditIzin.visibility = View.VISIBLE
        }else{
            holder.fileIzin.text = "Lihat file"
            holder.fileIzin.setTextColor(context.resources.getColor(R.color.colorBg))
            holder.fileIzin.setOnClickListener{
                riwayatAbsensiFragment.gotoOpenFile(list[position].buktiIzin,list[position].tipeFileIzin)
            }
        }
        holder.tglIzin.text = list[position].tglStartIzin+" sd "+list[position].tglEndIzin
        holder.lamaIzin.text = list[position].lamaIzin+" hari"

        if(list[position].statusIzin.equals("waiting")){
            holder.textViewStatusIzin.text = "Pending"
            holder.textViewStatusIzin.setTextColor(context.resources.getColor(R.color.warning))
        }else if(list[position].statusIzin.equals("accepted")){
            holder.textViewStatusIzin.text = "Diterima"
            holder.textViewStatusIzin.setTextColor(context.resources.getColor(R.color.colorBg))
            holder.textViewNoteAdmin.setTextColor(context.resources.getColor(R.color.colorBg))
            holder.textViewNoteAdmin.text = "Note : "+(list[position].ketFromAdmin)
        }else{
            holder.textViewStatusIzin.text = "Ditolak"
            holder.textViewStatusIzin.setTextColor(context.resources.getColor(R.color.danger))
            holder.textViewNoteAdmin.text = "Note : "+list[position].ketFromAdmin
        }
        holder.tableAbsensi.visibility = View.VISIBLE
        holder.tablePresensi.visibility = View.GONE

        holder.btnEditIzin.setOnClickListener {
            riwayatAbsensiFragment.gotoEdit(list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.count()
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val tgl = v.tglPresensi
        val tablePresensi = v.tablePresensi
        val tableAbsensi = v.tableAbsensi
        val keterangan = v.ketIzin
        val tipeIzin = v.tipeIzin
        val fileIzin = v.fileIzin
        val tglIzin = v.tglIzin
        val lamaIzin = v.txtLamaIzin
        val textViewStatusIzin = v.textViewStatusIzin
        val btnEditIzin = v.btnEditIzin
        val textViewNoteAdmin = v.textViewNoteAdmin
    }

}