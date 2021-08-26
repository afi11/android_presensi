package codeafifudin.fatakhul.projectta.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.views.presensi.RiwayatPresensiFragment
import kotlinx.android.synthetic.main.row_presensi.view.*

class LogPresensiAdapter(items: MutableList<Presensi>, context: Context, riwayatPresensiFragment: RiwayatPresensiFragment): RecyclerView.Adapter<LogPresensiAdapter.ViewHolder>() {

    private val list = items
    private val context = context
    private val riwayatPresensiFragment = riwayatPresensiFragment
    var conveter: Conveter ?= null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogPresensiAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_presensi,parent,false))
    }

    override fun onBindViewHolder(holder: LogPresensiAdapter.ViewHolder, position: Int) {
        conveter = Conveter(context)
        holder.tgl.text = list[position].tgl

        if(list[position].jamMasuk.equals("null")){
            holder.jam_masuk.text = "Tidak Presensi"
            holder.jam_masuk.setTextColor(context.resources.getColor(R.color.danger))
        }else{
            holder.jam_masuk.text = list[position].jamMasuk
        }

        if(list[position].jamPulang.equals("null")){
            holder.jam_pulang.text = "Tidak Presensi"
            holder.jam_pulang.setTextColor(context.resources.getColor(R.color.danger))
        }else{
            holder.jam_pulang.text = list[position].jamMasuk
        }

        if(list[position].statusJamMasuk.equals("null")){
            holder.statusJamMasuk.text = "Tidak Presensi"
            holder.statusJamPulang.setTextColor(context.resources.getColor(R.color.danger))
        }else{
            holder.statusJamMasuk.text = list[position].statusJamMasuk
        }

        if(list[position].statusJamPulang.equals("null")){
            holder.statusJamPulang.text = "Tidak Presensi"
            holder.statusJamPulang.setTextColor(context.resources.getColor(R.color.danger))
        }else{
            holder.statusJamPulang.text = list[position].statusJamPulang
        }

        holder.tableAbsensi.visibility = View.GONE
        holder.tablePresensi.visibility = View.VISIBLE
        holder.rowActionBtn.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val tgl = v.tglPresensi
        val jam_masuk = v.jamMasuk
        val jam_pulang = v.jamPulang
        val statusJamMasuk = v.ketJamMasuk
        val statusJamPulang = v.ketJamPulang
        val tablePresensi = v.tablePresensi
        val tableAbsensi = v.tableAbsensi
        val rowActionBtn = v.rowActionBtn
    }

}