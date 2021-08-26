package codeafifudin.fatakhul.projectta.views.absensi

import android.content.ContentValues
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.components.BottomSheetAddFileFragment
import codeafifudin.fatakhul.projectta.controllers.PresensiController
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.utils.CustomProgress
import codeafifudin.fatakhul.projectta.utils.DatePickerHelper
import kotlinx.android.synthetic.main.activity_tambah_absensi.*
import java.util.*

class TambahAbsensiActivity : AppCompatActivity() {

    lateinit var datePicker: DatePickerHelper
    lateinit var toolbar : Toolbar
    private val progressDialog = CustomProgress()
    var presensiController: PresensiController?= null
    val presensi = Presensi()

    val tipeIzin = arrayOf("Sakit","Dinas Luar", "Lainnya")

    var conveter : Conveter?= null
    var tipeFile: String ?= null
    var fileUrl : String ?= null
    var imageUri : Uri?= null
    var isEdit : Boolean ?= false
    var oldFile : String = ""

    companion object {
        const val TAG_FILE_PDF = 100
        const val TAG_FILE_CAMERA = 101
        const val TAG_FILE_GALERY = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_absensi)

        conveter = Conveter(this)
        datePicker = DatePickerHelper(this)
        presensiController = PresensiController(this)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }


        toolbar = findViewById(R.id.toolbarAddAbsensi)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Tambah Ketidakhadiran/Cuti"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        val backArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        backArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        spinnerTipeIzin.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, tipeIzin)

        val editPresensi = intent.getParcelableExtra<Presensi>("editPresensi")
        isEdit = intent.getBooleanExtra("isEdit",false)
        if(isEdit as Boolean){
            supportActionBar!!.title = "Edit Ketidakhadiran/Cuti"
            oldFile = editPresensi!!.buktiIzin
            presensi.id = editPresensi!!.id
            presensi.tipeFileIzin = editPresensi.tipeFileIzin
            if(editPresensi.tipeFileIzin.equals("null")){
                txtSelectedFile.setText("-")
            }else{
                txtSelectedFile.setText("Lihat file izin")
            }
            textViewTglSelected.setText(editPresensi.tglStartIzin)
            textViewTglSelected2.setText(editPresensi.tglEndIzin)
            editTextKeterangan.setText(editPresensi.keterangan)
            val editIzin = editPresensi!!.tipeIzin
            if(editIzin.equals("sakit")){
                spinnerTipeIzin.setSelection(tipeIzin.indexOf("Sakit"))
            }else if(editIzin.equals("dinas_luar")){
                spinnerTipeIzin.setSelection(tipeIzin.indexOf("Dinas Luar"))
            }else{
                spinnerTipeIzin.setSelection(tipeIzin.indexOf("Lainnya"))
            }
            textViewTglSelected.isClickable = false
            textViewTglSelected2.isClickable = false
            pickTglMulai.isEnabled = false
            pickTglSelesai.isEnabled = false
            spinnerTipeIzin.isEnabled = false
        }

        spinnerTipeIzin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                presensi.tipeIzin = spinnerTipeIzin.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinnerTipeIzin.setSelection(0)
            }

        }

        btnAddFile.setOnClickListener {
            val bottomSheetAddFileFragment = BottomSheetAddFileFragment()
            bottomSheetAddFileFragment.show(supportFragmentManager, BottomSheetAddFileFragment.TAG)
        }

        pickTglMulai.setOnClickListener {
            val cal = Calendar.getInstance()
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            val now : Long = Calendar.getInstance().timeInMillis
            datePicker.setMinDate(now)
            datePicker.showDialog(d,m,y, object : DatePickerHelper.Callback {
                override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                    val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                    val mon = month + 1
                    val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                    textViewTglSelected.setText("${year}-${monthStr}-${dayStr}")
                }

            })
        }

        pickTglSelesai.setOnClickListener {
            val cal = Calendar.getInstance()
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            datePicker.showDialog(d,m,y, object : DatePickerHelper.Callback {
                override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                    val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                    val mon = month + 1
                    val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                    textViewTglSelected2.setText("${year}-${monthStr}-${dayStr}")
                }

            })
        }

        buttonSubmitAbsensi.setOnClickListener {
            val tglAwal = textViewTglSelected.text.toString()
            val tglAkhir = textViewTglSelected2.text.toString()
            if(!tglAwal.equals("Pilih tanggal mulai izin") && !tglAkhir.equals("Pilih tanggal selesai izin")){
                if(presensi.tipeIzin.equals("Lainnya") && editTextKeterangan.text.toString().equals("")){
                    Toast.makeText(this,"Keterangan wajib diisi, karena izin tidak spesifik",Toast.LENGTH_SHORT).show()
                }
                else{
                    if(!presensi.tipeIzin.equals("Sakit") && presensi.tipeFileIzin.equals("")){
                        Toast.makeText(this,"Bukti izin harus disertakan",Toast.LENGTH_SHORT).show()
                    }else{
                        progressDialog.show(this,"Menyimpan data....")
                        presensi.tglStartIzin = textViewTglSelected.text.toString()
                        presensi.tglEndIzin = textViewTglSelected2.text.toString()
                        presensi.keterangan = editTextKeterangan.text.toString()
                        if(isEdit as Boolean){
                            presensiController!!.updateIzinKetidakhadiran(presensi,oldFile){
                                progressDialog.hide(this)
                                Toast.makeText(this,"Berhasil menyimpan data", Toast.LENGTH_LONG).show()
                                    onBackPressed()
                                }
                        }else{
                            presensiController!!.postKetidakhadrian(presensi){
                                progressDialog.hide(this)
                                if(it.isSimpan){
                                    Toast.makeText(this,"Berhasil menyimpan data", Toast.LENGTH_LONG).show()
                                    onBackPressed()
                                }else{
                                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }else{
                Toast.makeText(this,"Tanggal mulai & selesai Izin harus dipilih",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("application/pdf")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Select File Pdf"),
            TAG_FILE_PDF
        )
    }

    fun openCamera(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE,"Select Picture from Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, AbsensiFragment.TAG_FILE_CAMERA)
    }

    fun openGalery() {
        val galeryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galeryIntent, AbsensiFragment.TAG_FILE_GALERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == TAG_FILE_PDF && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.getBase64ForUriAndPossiblyCrash(data!!.data!!)
            presensi.buktiIzin = fileUrl.toString()
            txtSelectedFile.text = "File pdf sudah diambil"
            presensi.tipeFileIzin = "pdf"
        }
        if(requestCode == TAG_FILE_CAMERA && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(imageUri!!))
            presensi.buktiIzin = fileUrl.toString()
            txtSelectedFile.text = "File gambar sudah diambil"
            presensi.tipeFileIzin = "image"
        }
        if(requestCode == TAG_FILE_GALERY && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(data!!.data!!))
            presensi.buktiIzin = fileUrl.toString()
            txtSelectedFile.text = "File gambar sudah diambil"
            presensi.tipeFileIzin = "image"
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        BottomSheetAddFileFragment().dialog?.setCancelable(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}