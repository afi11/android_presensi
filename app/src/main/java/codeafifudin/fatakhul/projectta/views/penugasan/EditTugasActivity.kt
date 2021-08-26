package codeafifudin.fatakhul.projectta.views.penugasan

import android.content.ContentValues
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.components.BottomSheetAddFileTugasFragment
import codeafifudin.fatakhul.projectta.controllers.PenugasanController
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Tugas
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.utils.CustomProgress
import kotlinx.android.synthetic.main.activity_edit_tugas.*

class EditTugasActivity : AppCompatActivity() {

    lateinit var toolbar : Toolbar
    var conveter : Conveter ?= null
    var penugasanController : PenugasanController ?= null
    val tugas = Tugas()
    private val progressDialog = CustomProgress()

    var tipeFile: String ?= null
    var fileUrl : String ?= null
    var imageUri : Uri?= null

    companion object {
        const val TAG_FILE_PDF = 100
        const val TAG_FILE_CAMERA = 101
        const val TAG_FILE_GALERY = 102
        const val TAG_FILE_DOC = 103
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tugas)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }

        val editTugas = intent.getParcelableExtra<Tugas>("editTugas")
        penugasanController = PenugasanController(this)
        conveter = Conveter(this)

        toolbar = findViewById(R.id.toolbarAddTugas)
        penugasanController = PenugasanController(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Edit Tugas "+editTugas!!.noSurat
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        val backArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        backArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        tugas.id = editTugas.id
        btnAddFile2.setOnClickListener {
            val bottomSheetAddFileFragment = BottomSheetAddFileTugasFragment()
            bottomSheetAddFileFragment.show(supportFragmentManager, BottomSheetAddFileTugasFragment.TAG)
        }

        btnDownloadTemplate.setOnClickListener {
            val url = "${EndPoints.URL_DOWNLOAD_TEMPLATE}/${tugas.id}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        buttonSubmitPenugasan.setOnClickListener {
            progressDialog.show(this,"Menyimpan data...")
            tugas.tipeFile = tipeFile.toString()
            penugasanController!!.postTugas(tugas){
                progressDialog.hide(this)
                Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
                onBackPressed()
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
        startActivityForResult(cameraIntent, TAG_FILE_CAMERA)
    }

    fun openGalery() {
        val galeryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galeryIntent, TAG_FILE_GALERY)
    }

    fun openFileDoc() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("*/*")
        val mimeTypes = arrayOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, TAG_FILE_DOC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == TAG_FILE_PDF && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.getBase64ForUriAndPossiblyCrash(data!!.data!!)
            tugas.penyelesaian = fileUrl.toString()
            txtSelectedFile.text = "File pdf sudah diambil"
            tipeFile = "pdf"
        }
        if(requestCode == TAG_FILE_DOC && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.getBase64ForUriAndPossiblyCrash(data!!.data!!)
            tugas.penyelesaian = fileUrl.toString()
            txtSelectedFile.text = "File pdf sudah diambil"
            tipeFile = "doc"
        }
        if(requestCode == TAG_FILE_CAMERA && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(imageUri!!))
            tugas.penyelesaian = fileUrl.toString()
            txtSelectedFile.text = "File gambar sudah diambil"
            tipeFile = "image"
        }
        if(requestCode == TAG_FILE_GALERY && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(data!!.data!!))
            tugas.penyelesaian = fileUrl.toString()
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