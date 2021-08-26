package codeafifudin.fatakhul.projectta.views.presensi

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.adapters.LogPresensiAdapter
import codeafifudin.fatakhul.projectta.adapters.RowStatistikAdapter
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.controllers.AuthController
import codeafifudin.fatakhul.projectta.controllers.PresensiController
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.models.RowStatus
import codeafifudin.fatakhul.projectta.utils.CustomProgress
import codeafifudin.fatakhul.projectta.utils.DatePickerHelper
import codeafifudin.fatakhul.projectta.views.absensi.TambahAbsensiActivity
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
import kotlinx.android.synthetic.main.fragment_absensi.view.*
import kotlinx.android.synthetic.main.fragment_riwayat_presensi.*
import kotlinx.android.synthetic.main.fragment_riwayat_presensi.view.*
import kotlinx.android.synthetic.main.layout_header_drawer.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set


class RiwayatPresensiFragment : Fragment() {

    lateinit var thisParent : MainActivity
    var authController : AuthController? = null
    var presensiController : PresensiController ?= null
    val dataPresensi : MutableList<Presensi> = ArrayList()
    val dataStatistikPresensi : MutableList<RowStatus> = ArrayList()
    var namaPeg: String = ""
    var nipPeg: String = ""
    lateinit var datePicker: DatePickerHelper
    lateinit var presensiAdapter : LogPresensiAdapter
    lateinit var statistikAdapter : RowStatistikAdapter
    lateinit var v : View
    var auth : Auth?= null
    var tgl_awal = ""
    var tgl_akhir = ""

    private val progressDialog = CustomProgress()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        authController = AuthController(thisParent)
        presensiController = PresensiController(thisParent)
        auth = Auth(thisParent)
        datePicker = DatePickerHelper(thisParent)
        v = inflater.inflate(R.layout.fragment_riwayat_presensi, container, false)

        thisParent.supportActionBar!!.title = "Riwayat Presensi"


        var verticalLayout = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        var horizontalLayout = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        v.recyclerViewLogPresensi.layoutManager = verticalLayout
        v.recyclerViewLogPresensi.setHasFixedSize(true)
        v.rcStatistikPresensi.layoutManager = horizontalLayout
        v.rcStatistikPresensi.setHasFixedSize(true)
        presensiAdapter = LogPresensiAdapter(dataPresensi, thisParent, this)
        statistikAdapter = RowStatistikAdapter(dataStatistikPresensi,thisParent)
        v.recyclerViewLogPresensi.adapter = presensiAdapter
        v.rcStatistikPresensi.adapter = statistikAdapter

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
            showStatistikPresensi(tgl_awal, tgl_akhir)
            showLogPresensi(tgl_awal, tgl_akhir)
            v.divalert.visibility = View.VISIBLE
            v.txtJudulStatistik.setText("Statistik Presensi Periode ${tgl_awal} sd ${tgl_akhir}")
            v.textViewAlert.setText("Menampilkan riwayat presensi pada periode ${tgl_awal} sampai ${tgl_akhir}")
        }

        v.clearButton.setOnClickListener {
            v.divalert.visibility = View.GONE
            showLogPresensi("","")
            showStatistikPresensi("","")
        }

        isWriteStoragePermissionGranted()

        getPeriode()
        getDetailUser()

        return v
    }

    private fun getPeriode() {
        presensiController!!.getPeriode {
            v.txtJudulStatistik.setText("Statistik Presensi Periode ${it.periode_awal} sd ${it.periode_akhir}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_log_presensi, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            R.id.menu_print_file -> {
                val popupMenu = PopupMenu(thisParent,thisParent.findViewById(R.id.menu_print_file))
                popupMenu.menuInflater.inflate(R.menu.menu_print, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_kalkulasi -> {
                            isWriteStoragePermissionGranted()
                            printPdfKalkulasi()
                        }
                        R.id.menu_riwayat -> {
                            isWriteStoragePermissionGranted()
                            printPdf()
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
        return super.onOptionsItemSelected(item)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            10 -> {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    printPdf()
                }
            }
        }
    }

    fun getDetailUser(){
        authController!!.getDataUser {
            namaPeg = it.nama
            nipPeg = it.nip
        }
    }

    fun showStatistikPresensi(tglAwal: String, tglAkhir: String){
        v.rcStatistikPresensi.recycledViewPool.clear()
        dataStatistikPresensi.clear()
        val getStatistikPresensi = object : StringRequest(
            Request.Method.GET,EndPoints.URL_STATISTIK_PRESENSI+ "/" + auth!!.userId()+"?tgl_awal=${tglAwal}&tgl_akhir=${tglAkhir}", Response.Listener { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("data")
                    for (i in 0..array.length() - 1){
                        val rowStatus = RowStatus()
                        val data = array.getJSONObject(i)
                        rowStatus.status = data.getString("status")
                        rowStatus.jumlah = data.getInt("jumlah")
                        dataStatistikPresensi.add(rowStatus)
                    }
                    statistikAdapter.notifyDataSetChanged()
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->  Log.d("ErrorLogStatistik", error.toString()) }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(getStatistikPresensi)
    }

    fun showLogPresensi(tglAwal: String, tglAkhir: String){
        v.recyclerViewLogPresensi.recycledViewPool.clear()
        dataPresensi.clear()
        val getLogPresensi = object : StringRequest(
                Request.Method.GET, EndPoints.URL_GET_LOG_PRESENSI + "/" + auth!!.userId()+"?tgl_awal=${tglAwal}&tgl_akhir=${tglAkhir}", Response.Listener { s ->
            try {
                val obj = JSONObject(s)
                val array = obj.getJSONArray("data")
                Log.d("logPresensi",array.toString())
                for (i in 0..array.length() - 1) {
                    val presensi = Presensi()
                    val data = array.getJSONObject(i)
                    presensi.tgl = data.getString("tgl_presensi")
                    presensi.waktu = data.getString("waktu_presensi")
                    presensi.tipejam = data.getString("tipe_presensi")
                    presensi.jamMasuk = data.getString("jam_masuk")
                    presensi.jamPulang = data.getString("jam_pulang")
                    presensi.statusJamMasuk = data.getString("status_jam_masuk")
                    presensi.statusJamPulang = data.getString("status_jam_pulang")
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

    private fun printPdfKalkulasi() {
        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(pdfPath, "kalkulasiPresensi.pdf")
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument)


        val d = resources.getDrawable(R.drawable.logokemenag)
        val bitDw = d as BitmapDrawable
        val bmp = bitDw.bitmap
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bitmapData : ByteArray = stream.toByteArray()

        val imageData = ImageDataFactory.create(bitmapData)
        val image : com.itextpdf.layout.element.Image = com.itextpdf.layout.element.Image(imageData)
        image.setHeight(70f)
        image.setWidth(70f)

        val tableHeader = Table(UnitValue.createPercentArray(floatArrayOf(5f,25f))).useAllAvailableWidth()
        tableHeader.addHeaderCell(Cell().add(image).setBorder(Border.NO_BORDER))
        tableHeader.addHeaderCell(Cell().add(Paragraph("Kementerian Agama Kabupaten Jombang")
            .setBold().setFontSize(18f).setTextAlignment(TextAlignment.CENTER))
            .add(Paragraph("Jalan Pattimura 26 Telp. (0321) 861321 Faksimili (0321) 861321 Jombang 61419")
                .setBold().setFontSize(12f).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))
        document.add(tableHeader)

        val lineDrawer : SolidLine = SolidLine(1f)
        val objectName : LineSeparator = LineSeparator(lineDrawer)
        document.add(objectName)

        val tableRowJudul = Table(UnitValue.createPercentArray(floatArrayOf(15f))).useAllAvailableWidth()
        tableRowJudul.setMarginTop(4f)
        tableRowJudul.addHeaderCell(Cell().add(Paragraph("Kalkulasi Presensi").setBold().setFontSize(16f).setTextAlignment(TextAlignment.CENTER)).setBorder(
            Border.NO_BORDER))
        document.add(tableRowJudul)

        val table = Table(UnitValue.createPercentArray(floatArrayOf(6f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f))).useAllAvailableWidth()
        table.setMarginTop(4f)
        // Add Header Cells
        table.addHeaderCell(Cell().add(Paragraph("Nama").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("TL-1").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("TL-2").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("TL-3").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("TL-4").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("PSW-1").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("PSW-2").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("PSW-3").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("PSW-4").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("TP").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("M").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("I").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("TM").setBold().setTextAlignment(TextAlignment.CENTER)))

        table.addCell(Cell().add(Paragraph(namaPeg).setTextAlignment(TextAlignment.LEFT)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(0).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(1).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(2).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(3).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(4).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(5).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(6).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(7).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(8).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(9).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(10).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))
        table.addCell(Cell().add(Paragraph(dataStatistikPresensi.get(11).jumlah.toString()).setTextAlignment(TextAlignment.CENTER)))

        document.add(table)

        document.close()

        Toast.makeText(thisParent, "Berhasil cetak data, lihat pada folder download", Toast.LENGTH_LONG).show()
    }

    private fun printPdf() {
        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(pdfPath, "daftarPresensi.pdf")
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        pdfDocument.defaultPageSize = PageSize.A4
        val document = Document(pdfDocument)


        val d = resources.getDrawable(R.drawable.logokemenag)
        val bitDw = d as BitmapDrawable
        val bmp = bitDw.bitmap
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bitmapData : ByteArray = stream.toByteArray()

        val imageData = ImageDataFactory.create(bitmapData)
        val image : com.itextpdf.layout.element.Image = com.itextpdf.layout.element.Image(imageData)
        image.setHeight(70f)
        image.setWidth(70f)

        val tableHeader = Table(UnitValue.createPercentArray(floatArrayOf(5f,25f))).useAllAvailableWidth()
        tableHeader.addHeaderCell(Cell().add(image).setBorder(Border.NO_BORDER))
        tableHeader.addHeaderCell(Cell().add(Paragraph("Kementerian Agama Kabupaten Jombang")
            .setBold().setFontSize(18f).setTextAlignment(TextAlignment.CENTER))
            .add(Paragraph("Jalan Pattimura 26 Telp. (0321) 861321 Faksimili (0321) 861321 Jombang 61419")
            .setBold().setFontSize(12f).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER))
        document.add(tableHeader)

        val lineDrawer : SolidLine = SolidLine(1f)
        val objectName : LineSeparator = LineSeparator(lineDrawer)
        document.add(objectName)

        val tableRowJudul = Table(UnitValue.createPercentArray(floatArrayOf(15f))).useAllAvailableWidth()
        tableRowJudul.setMarginTop(4f)
        tableRowJudul.addHeaderCell(Cell().add(Paragraph("Riwayat Presensi").setBold().setFontSize(16f).setTextAlignment(TextAlignment.CENTER)).setBorder(
            Border.NO_BORDER))
        document.add(tableRowJudul)

        val table = Table(UnitValue.createPercentArray(floatArrayOf(2f, 8f, 8f, 8f, 5f,5f))).useAllAvailableWidth()
        table.setMarginTop(4f)
        // Add Header Cells
        table.addHeaderCell(Cell().add(Paragraph("No").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Tanggal").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Jam Masuk").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Jam Pulang").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Ket.Masuk").setBold().setTextAlignment(TextAlignment.CENTER)))
        table.addHeaderCell(Cell().add(Paragraph("Ket.Pulang").setBold().setTextAlignment(TextAlignment.CENTER)))

        for (i in 0 until dataPresensi.size){
            table.addCell(Cell().add(Paragraph((i+1).toString()).setTextAlignment(TextAlignment.LEFT)))
            table.addCell(Cell().add(Paragraph(dataPresensi.get(i).tgl).setTextAlignment(TextAlignment.LEFT)))
            table.addCell(Cell().add(Paragraph(dataPresensi.get(i).jamMasuk).setTextAlignment(TextAlignment.LEFT)))
            table.addCell(Cell().add(Paragraph(dataPresensi.get(i).jamPulang).setTextAlignment(TextAlignment.LEFT)))
            table.addCell(Cell().add(Paragraph(dataPresensi.get(i).statusJamMasuk).setTextAlignment(TextAlignment.LEFT)))
            table.addCell(Cell().add(Paragraph(dataPresensi.get(i).statusJamPulang).setTextAlignment(TextAlignment.LEFT)))
        }
        document.add(table)

        document.close()

        Toast.makeText(thisParent, "Berhasil cetak data, lihat pada folder download", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        showStatistikPresensi("","")
        showLogPresensi("","")
    }

}