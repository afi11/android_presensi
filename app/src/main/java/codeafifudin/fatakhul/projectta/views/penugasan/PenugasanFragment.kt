package codeafifudin.fatakhul.projectta.views.penugasan

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
import codeafifudin.fatakhul.projectta.adapters.LogTugasAdapter
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.models.Tugas
import codeafifudin.fatakhul.projectta.views.absensi.TambahAbsensiActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_penugasan.view.*
import kotlinx.android.synthetic.main.fragment_penugasan.view.btnSearch
import kotlinx.android.synthetic.main.fragment_penugasan.view.clearButton
import kotlinx.android.synthetic.main.fragment_penugasan.view.divalert
import kotlinx.android.synthetic.main.fragment_penugasan.view.month_spinner
import kotlinx.android.synthetic.main.fragment_penugasan.view.textViewAlert
import kotlinx.android.synthetic.main.fragment_penugasan.view.year_spinner
import kotlinx.android.synthetic.main.layout_preview_image.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class PenugasanFragment : Fragment() {

    lateinit var v :View
    lateinit var thisParent: MainActivity

    val tugasList : MutableList<Tugas> = ArrayList()
    lateinit var logTugasAdapter: LogTugasAdapter
    var auth : Auth ?= null
    var bulan : Int = 0
    var tahun : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as MainActivity
        auth = Auth(thisParent)
        v = inflater.inflate(R.layout.fragment_penugasan, container, false)

        thisParent.supportActionBar!!.title = "Penugasan"

        var linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        v.recyclerViewLogPenugasan.layoutManager = linearLayoutManager
        v.recyclerViewLogPenugasan.setHasFixedSize(true)
        logTugasAdapter = LogTugasAdapter(tugasList,thisParent,this)
        v.recyclerViewLogPenugasan.adapter = logTugasAdapter

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
            logPenugasan(bulan,tahun)
            v.divalert.visibility = View.VISIBLE
            v.textViewAlert.setText("Menampilkan riwayat aktivitas pada bulan ${v.month_spinner.selectedItem} tahun ${v.year_spinner.selectedItem}")
        }

        v.clearButton.setOnClickListener {
            v.divalert.visibility = View.GONE
            logPenugasan(0,0)
        }

        isWriteStoragePermissionGranted()

        return v
    }

    fun isWriteStoragePermissionGranted(): Boolean {
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(thisParent,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true
            }else{
                ActivityCompat.requestPermissions(thisParent, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
                return false
            }
        }else{
            return true
        }
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

    fun gotoEdit(tugas: Tugas){
        val intent = Intent(thisParent, DetailTugasActivity::class.java)
        intent.putExtra("editTugas", tugas)
        startActivity(intent)
    }

    fun logPenugasan(bulan: Int, tahun: Int){
        v.recyclerViewLogPenugasan.recycledViewPool.clear()
        tugasList.clear()
        val getLogTugas : StringRequest = object : StringRequest(Request.Method.GET, "${EndPoints.URL_GET_LOG_TUGAS}/${auth!!.userId()}?bulan=${bulan}&tahun=${tahun}",
            Response.Listener { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("data")
                    for(i in 0 until array.length()){
                        val tugas = Tugas()
                        val data = array.getJSONObject(i)
                        tugas.id = data.getString("id")
                        tugas.noSurat = data.getString("no_surat")
                        tugas.perihal = data.getString("perihal")
                        tugas.tglBerangkat = data.getString("tgl_berangkat")
                        tugas.tglKembali = data.getString("tgl_kembali")
                        tugas.perihal = data.getString("perihal")
                        tugas.tempatTugas = data.getString("tempat_tugas")
                        tugas.jeniAngkutan = data.getString("jenis_angkutan")
                        tugas.jenisTugas = data.getString("jenis_tugas")
                        tugas.fileTugas = data.getString("file")
                        tugas.status = data.getString("status")
                        tugas.ketAdmin = data.getString("keterangan")
                        tugas.tipeFile = data.getString("tipe_file")
                        tugas.penyelesaian = data.getString("bukti_file")
                        tugasList.add(tugas)
                    }
                    logTugasAdapter.notifyDataSetChanged()
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },Response.ErrorListener { error ->  }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(getLogTugas)
    }

    override fun onStart() {
        super.onStart()
        logPenugasan(0,0)
    }



}