package codeafifudin.fatakhul.projectta.views.absensi

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.adapters.LogAbsensiAdapter
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.controllers.PresensiController
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.utils.DatePickerHelper
import codeafifudin.fatakhul.projectta.views.presensi.OpenFileIzinActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_riwayat_presensi.view.*
import kotlinx.android.synthetic.main.layout_preview_image.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set


class RiwayatAbsensiFragment : Fragment() {

    lateinit var thisParent : MainActivity
    var presensiController : PresensiController ?= null
    val dataPresensi : MutableList<Presensi> = ArrayList()
    lateinit var presensiAdapter : LogAbsensiAdapter
    lateinit var datePicker: DatePickerHelper
    lateinit var v : View
    var auth : Auth?= null
    var tgl_awal = ""
    var tgl_akhir = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        presensiController = PresensiController(thisParent)
        auth = Auth(thisParent)
        datePicker = DatePickerHelper(thisParent)

        v = inflater.inflate(R.layout.fragment_riwayat_presensi, container, false)

        thisParent.supportActionBar!!.title = "Riwayat Ketidakhadiran / Cuti"

        var verticalLayout = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        v.recyclerViewLogPresensi.layoutManager = verticalLayout
        v.recyclerViewLogPresensi.setHasFixedSize(true)
        presensiAdapter = LogAbsensiAdapter(dataPresensi, thisParent, this)
        v.recyclerViewLogPresensi.adapter = presensiAdapter

        if(thisParent.isabsen){
            v.txtJudulStatistik.visibility = View.GONE
            v.rcStatistikPresensi.visibility = View.GONE
            v.txtJudulPengajuan.setText("Pengajuan Izin / Cuti")
        }

        v.btnPickTglAwal.setOnClickListener {
            val cal = Calendar.getInstance()
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            datePicker.showDialog(d,m,y, object : DatePickerHelper.Callback {
                override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                    val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                    val mon = month + 1
                    val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                    tgl_awal = "${year}-${monthStr}-${dayStr}"
                    v.tglSelectedAwal.setText(tgl_awal)
                }
            })
        }

        v.btnPickTglAkhir.setOnClickListener {
            val cal = Calendar.getInstance()
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            datePicker.showDialog(d,m,y, object : DatePickerHelper.Callback {
                override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                    val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                    val mon = month + 1
                    val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                    tgl_akhir = "${year}-${monthStr}-${dayStr}"
                    v.tglSelectedAkhir.setText(tgl_akhir)
                }
            })
        }

        v.divalert.visibility = View.GONE


        if(!thisParent.isabsen){
            v.btnAddAbsensi.visibility = View.GONE
        }else{
            v.btnAddAbsensi.setOnClickListener{
                val intent  = Intent(thisParent, TambahAbsensiActivity::class.java)
                startActivity(intent)
            }
        }

        v.btnSearch.setOnClickListener {
            showLogPresensi(tgl_awal,tgl_akhir)
            v.divalert.visibility = View.VISIBLE
            v.textViewAlert.setText("Menampilkan riwayat pengajuan izin/cuti pada periode ${tgl_awal} sampai ${tgl_akhir}")
        }


        v.clearButton.setOnClickListener {
            v.divalert.visibility = View.GONE
            showLogPresensi("","")
        }

        isWriteStoragePermissionGranted()

        return v
    }

    fun isWriteStoragePermissionGranted() : Boolean {
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(thisParent, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true
            }else{
                ActivityCompat.requestPermissions(thisParent, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),10)
                return false
            }
        }else{
            return true
        }
    }


    fun showLogPresensi(tgl_awal: String, tgl_akhir: String){
        v.recyclerViewLogPresensi.recycledViewPool.clear()
        dataPresensi.clear()
        val getLogPresensi = object : StringRequest(
                Request.Method.GET, EndPoints.URL_GET_LOG_ABSENSI + "/" + auth!!.userId()+"?tgl_awal=${tgl_awal}&tgl_akhir=${tgl_akhir}", Response.Listener { s ->
            try {
                val obj = JSONObject(s)
                val array = obj.getJSONArray("data")
                Log.d("logPresensi",array.toString())
                for (i in 0..array.length() - 1) {
                    val presensi = Presensi()
                    val data = array.getJSONObject(i)
                    presensi.id = data.getString("id_presensi")
                    presensi.tgl = data.getString("tgl_presensi")
                    presensi.tipeIzin = data.getString("tipe_izin")
                    presensi.buktiIzin = data.getString("bukti_izin")
                    presensi.statusIzin = data.getString("status_izin")
                    presensi.keterangan = data.getString("keterangan")
                    presensi.lamaIzin = data.getString("lama_izin")
                    presensi.tglStartIzin = data.getString("tgl_start_izin")
                    presensi.tglEndIzin = data.getString("tgl_end_izin")
                    presensi.tipeFileIzin = data.getString("tipe_file")
                    presensi.ketFromAdmin = data.getString("ket_from_admin")
                    dataPresensi.add(presensi)
                }
                presensiAdapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                Log.d("ErrorLogPre", e.toString())
            }
        }, Response.ErrorListener { error ->
            Log.d("ErrorLogPresensi", error.toString())
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(getLogPresensi)
//        presensiController!!.getLogPresensi {
//            for (doc in it){
//                val obj = Presensi()
//                obj.tgl = doc.tgl
//                obj.waktu = doc.waktu
//                obj.tipejam = doc.tipejam
//                obj.status = doc.status
//                obj.buktiIzin = doc.buktiIzin
//                obj.tipeFileIzin = doc.tipeFileIzin
//                obj.tipeIzin = doc.tipeIzin
//                dataPresensi.add(doc)
//            }
//            presensiAdapter.notifyDataSetChanged()
//        }
    }

    fun gotoEdit(presensi: Presensi){
        val intent = Intent(thisParent, TambahAbsensiActivity::class.java)
        intent.putExtra("editPresensi", presensi)
        intent.putExtra("isEdit",true)
        startActivity(intent)
    }

    fun gotoOpenFile(buktiFile: String, tipeFile: String){
        if(tipeFile.equals("pdf")){
            val intent = Intent(thisParent, OpenFileIzinActivity::class.java)
            intent.putExtra("openFile", buktiFile)
            intent.putExtra("tipeFile", tipeFile)
            startActivity(intent)
        }else{
            val alertDialog = AlertDialog.Builder(thisParent)
            val dialog = alertDialog.create()
            val inflater : LayoutInflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_preview_image, null)
            dialog.setView(dialogView)
            dialog.setTitle("File Izin Pegawai")
            dialog.setButton(Dialog.BUTTON_POSITIVE,"close", object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                   dialog!!.dismiss()
                }
            })
            Picasso.get().load(EndPoints.URL_FILE_IZIN+buktiFile).into(dialogView.imageViewPreview)
            dialog.show()
        }
    }


    override fun onStart() {
        super.onStart()
        showLogPresensi("","")
    }

}