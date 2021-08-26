package codeafifudin.fatakhul.projectta.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.models.Tugas
import codeafifudin.fatakhul.projectta.views.penugasan.PenugasanFragment
import kotlinx.android.synthetic.main.row_tugas.view.*

class LogTugasAdapter(items : MutableList<Tugas>, context: Context, penugasanFragment: PenugasanFragment): RecyclerView.Adapter<LogTugasAdapter.ViewHolder>() {

    private val list = items
    private val context = context
    private val penugasanFragment = penugasanFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogTugasAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_tugas,parent,false))
    }

    override fun onBindViewHolder(holder: LogTugasAdapter.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.noSurat.text = list[position].noSurat
        holder.tgl.text = list[position].tglBerangkat+" sd "+list[position].tglKembali
        holder.ketAdmin.text = list[position].ketAdmin

        if(list[position].status.equals("selesai")){
            holder.status.setText("Selesai")
            holder.status.setTextColor(context.resources.getColor(R.color.colorBg))
        }else if(list[position].status.equals("batal")){
            holder.status.setText("Batal")
            holder.status.setTextColor(context.resources.getColor(R.color.danger))
        }else{
            holder.status.setText("Kirim")
        }

        if(list[position].ketAdmin.equals("null")){
            holder.ketAdmin.setText("")
        }else{
            holder.ketAdmin.text = "Note : "+list[position].ketAdmin
        }

        holder.btnEditTugas.setOnClickListener {
            penugasanFragment.gotoEdit(list[position])
        }

    }

    override fun getItemCount(): Int {
       return list.size
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val noSurat = v.txtNoSurat
        val tgl = v.txtTglTugas
        val status = v.txtStatusTugas
        val ketAdmin = v.textViewKetAdmin
        val btnEditTugas = v.btnEditTugas
    }


}