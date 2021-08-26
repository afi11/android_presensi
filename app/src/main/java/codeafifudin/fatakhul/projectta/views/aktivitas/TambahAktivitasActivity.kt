package codeafifudin.fatakhul.projectta.views.aktivitas

import android.content.ContentValues
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.components.BottomSheetAddFileAktivitasFragment
import codeafifudin.fatakhul.projectta.controllers.AktivitasController
import codeafifudin.fatakhul.projectta.models.Aktivitas
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.utils.CustomProgress
import codeafifudin.fatakhul.projectta.views.absensi.AbsensiFragment
import codeafifudin.fatakhul.projectta.views.absensi.TambahAbsensiActivity
import kotlinx.android.synthetic.main.activity_tambah_aktivitas.*
import kotlinx.android.synthetic.main.activity_tambah_aktivitas.txtSelectedFile
import java.util.*

class TambahAktivitasActivity : AppCompatActivity() {

    lateinit var toolbar : Toolbar

    var aktivitasController : AktivitasController ?= null
    val aktivitas = Aktivitas()

    private val progressDialog = CustomProgress()

    var conveter : Conveter?= null
    var tipeFile: String ?= null
    var fileUrl : String ?= null
    var imageUri : Uri?= null

    companion object {
        const val TAG_FILE_PDF = 100
        const val TAG_FILE_CAMERA = 101
        const val TAG_FILE_GALERY = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_aktivitas)

        conveter = Conveter(this)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }

        toolbar = findViewById(R.id.toolbarAddAktivitas)
        aktivitasController = AktivitasController(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Tambah Aktivitas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        val backArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        backArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        btnAddFile2.setOnClickListener {
            val bottomSheetAddFileFragment = BottomSheetAddFileAktivitasFragment()
            bottomSheetAddFileFragment.show(supportFragmentManager, BottomSheetAddFileAktivitasFragment.TAG)
        }


        buttonSubmitAktivitas.setOnClickListener {
            progressDialog.show(this,"Menyimpan data...")
            aktivitas.uraian = editTextUraian.text.toString()
            aktivitas.tipe = tipeFile.toString()
            aktivitas.kuantitas = editTextKuantitas.text.toString()
            aktivitas.satuan = editTextSatuan.text.toString()
            if(aktivitas.uraian.equals("")){
                progressDialog.hide(this)
                Toast.makeText(this,"Uraian harus diisi",Toast.LENGTH_LONG).show()
            }else if(aktivitas.kuantitas.equals("")){
                progressDialog.hide(this)
                Toast.makeText(this,"Kuantitas harus diisi",Toast.LENGTH_LONG).show()
            }else if(aktivitas.satuan.equals("")){
                progressDialog.hide(this)
                Toast.makeText(this,"Satuan harus diisi",Toast.LENGTH_LONG).show()
            }else{
                 aktivitasController!!.postAktivitas(aktivitas){
                    progressDialog.hide(this)
                    Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
            }
        }

    }

    fun openPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("application/pdf")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Select File Pdf"),
            TambahAbsensiActivity.TAG_FILE_PDF
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
        if(requestCode == TambahAbsensiActivity.TAG_FILE_PDF && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.getBase64ForUriAndPossiblyCrash(data!!.data!!)
            aktivitas.file = fileUrl.toString()
            txtSelectedFile.text = "File pdf sudah diambil"
            tipeFile = "pdf"
        }
        if(requestCode == TambahAbsensiActivity.TAG_FILE_CAMERA && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(imageUri!!))
            aktivitas.file = fileUrl.toString()
            txtSelectedFile.text = "File gambar sudah diambil"
            tipeFile = "image"
        }
        if(requestCode == TambahAbsensiActivity.TAG_FILE_GALERY && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(data!!.data!!))
            aktivitas.file = fileUrl.toString()
            txtSelectedFile.text = "File gambar sudah diambil"
            tipeFile = "image"
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}