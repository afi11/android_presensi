package codeafifudin.fatakhul.projectta.views.aktivitas

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.adapters.LogAktivitasAdapter
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Aktivitas
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_aktivitas.view.*
import kotlinx.android.synthetic.main.fragment_aktivitas.view.btnSearch
import kotlinx.android.synthetic.main.fragment_aktivitas.view.clearButton
import kotlinx.android.synthetic.main.fragment_aktivitas.view.divalert
import kotlinx.android.synthetic.main.fragment_aktivitas.view.month_spinner
import kotlinx.android.synthetic.main.fragment_aktivitas.view.textViewAlert
import kotlinx.android.synthetic.main.fragment_aktivitas.view.year_spinner
import kotlinx.android.synthetic.main.layout_preview_image.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AktivitasFragment : Fragment() {

    lateinit var thisParent : MainActivity
    lateinit var v : View

    val dataAktivtas: MutableList<Aktivitas> = ArrayList()
    lateinit var logAktivitasAdapter: LogAktivitasAdapter

    var auth : Auth ?= null
    var bulan : Int = 0
    var tahun : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as MainActivity
        auth = Auth(thisParent)
        v = inflater.inflate(R.layout.fragment_aktivitas, container, false)
        thisParent.supportActionBar!!.title = "Aktivitas"

        var linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        v.recyclerViewLogAktivitas.layoutManager = linearLayoutManager
        v.recyclerViewLogAktivitas.setHasFixedSize(true)
        logAktivitasAdapter = LogAktivitasAdapter(dataAktivtas,thisParent,this)
        v.recyclerViewLogAktivitas.adapter = logAktivitasAdapter

        populateYears(0,10)

        v.divalert.visibility = View.GONE

        v.month_spinner.adapter = ArrayAdapter<String>(thisParent,R.layout.support_simple_spinner_dropdown_item,resources.getStringArray(R.array.months))

        v.month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bulan = (position+1)
                v.month_spinner.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                bulan = 1
                v.month_spinner.setSelection(0)
            }
        }

        v.year_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tahun = v.year_spinner.selectedItem.toString().toInt()
                v.year_spinner.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                tahun = v.year_spinner.selectedItem.toString().toInt()
                v.year_spinner.setSelection(0)
            }
        }

        v.btnSearch.setOnClickListener {
            logAktivitas(bulan,tahun)
            v.divalert.visibility = View.VISIBLE
            v.textViewAlert.setText("Menampilkan riwayat aktivitas pada bulan ${v.month_spinner.selectedItem} tahun ${v.year_spinner.selectedItem}")
        }

        v.clearButton.setOnClickListener {
            v.divalert.visibility = View.GONE
            logAktivitas(0,0)
        }

        v.btnAddAktivitas.setOnClickListener {
            val intent = Intent(thisParent,TambahAktivitasActivity::class.java)
            startActivity(intent)
        }


        isWriteStoragePermissionGranted()

        return v
    }

    private fun populateYears(minAge:Int, maxAge:Int){
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years_array = arrayOfNulls<String>(maxAge - minAge)
        for(i in 0 until maxAge - minAge){
            years_array[i] = ""+(currentYear - minAge - i)
        }
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(thisParent, R.layout.support_simple_spinner_dropdown_item, years_array)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        v.year_spinner.adapter = spinnerArrayAdapter
    }

    fun gotoOpenFile(buktiFile: String, tipeFile: String){
        if(tipeFile.equals(".pdf")){
            val intent = Intent(thisParent, OpenFileIzinAktivitasActivity::class.java)
            intent.putExtra("openFile", buktiFile)
            intent.putExtra("tipeFile", tipeFile)
            startActivity(intent)
        }else{
            val alertDialog = AlertDialog.Builder(thisParent)
            val dialog = alertDialog.create()
            val inflater : LayoutInflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_preview_image, null)
            dialog.setView(dialogView)
            dialog.setTitle("File Aktivitas")
            dialog.setButton(Dialog.BUTTON_POSITIVE,"close", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }
            })
            Picasso.get().load(EndPoints.URL_FILE_AKTIVITAS+buktiFile).into(dialogView.imageViewPreview)
            dialog.show()
        }
    }

    fun isWriteStoragePermissionGranted(): Boolean {
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(thisParent,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true
            }else{
                ActivityCompat.requestPermissions(thisParent, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
                return false
            }
        }else{
            return true
        }
    }

    private fun logAktivitas(bulan: Int, tahun: Int){
        v.recyclerViewLogAktivitas.recycledViewPool.clear()
        dataAktivtas.clear()
        val getLogAktivitas : StringRequest = object : StringRequest(Request.Method.GET,"${EndPoints.URL_AKTIVITAS}/${auth!!.userId()}?bulan=${bulan}&tahun=${tahun}",
            Response.Listener { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("data")
                    for (i in 0 until array.length()){
                        val aktivitas = Aktivitas()
                        val data = array.getJSONObject(i)
                        aktivitas.pegawaiId = data.getString("pegawai_id")
                        aktivitas.tgl_aktivitas = data.getString("tanggal")
                        aktivitas.uraian = data.getString("uraian")
                        aktivitas.satuan = data.getString("satuan")
                        aktivitas.kuantitas = data.getString("kuantitas")
                        aktivitas.tipe = data.getString("tipe_file")
                        aktivitas.file = data.getString("file")
                        dataAktivtas.add(aktivitas)
                    }
                    logAktivitasAdapter.notifyDataSetChanged()
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },Response.ErrorListener { error ->  }){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(getLogAktivitas)
    }

    override fun onStart() {
        super.onStart()
        logAktivitas(0,0)
    }

}