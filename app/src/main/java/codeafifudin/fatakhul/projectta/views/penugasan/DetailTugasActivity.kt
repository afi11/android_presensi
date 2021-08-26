package codeafifudin.fatakhul.projectta.views.penugasan

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Tugas
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_tugas.*
import kotlinx.android.synthetic.main.layout_preview_image.view.*

class DetailTugasActivity : AppCompatActivity() {

    lateinit var toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tugas)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }

        val dtTugas = intent.getParcelableExtra<Tugas>("editTugas")

        toolbar = findViewById(R.id.toolbarDetailTugas)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Detail Tugas "+dtTugas!!.noSurat
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        val backArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        backArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)


        txtNoSurat.setText(dtTugas.noSurat)
        txtTglTugas.setText(dtTugas.tglBerangkat+" sd "+dtTugas.tglKembali)
        txtPerihal.setText(Html.fromHtml(dtTugas.perihal))
        txtTempatTugas.setText(dtTugas.tempatTugas)
        txtAngkutanTugas.setText(dtTugas.jeniAngkutan)
        txtJenisTugas.setText(dtTugas.jenisTugas)
        txtStatusTugas.setText(dtTugas.status)

        if(dtTugas.status.equals("selesai")){
            txtStatusTugas.setTextColor(resources.getColor(R.color.colorBg))
            btnEditTugas.visibility = View.GONE
        }else if(dtTugas.status.equals("batal")){
            txtStatusTugas.setTextColor(resources.getColor(R.color.danger))
            btnEditTugas.visibility = View.GONE
        }else{
            txtStatusTugas.setText("belum selesai")
        }

        if(dtTugas.tipeFile.equals("null")){
            txtSelesaiTugas.setText("belum ada")
        }

        if(dtTugas.ketAdmin.equals("null")){
            textViewKetAdmin.setText("")
        }else{
            textViewKetAdmin.text = "Note : "+dtTugas.ketAdmin
        }

        btnEditTugas.setOnClickListener {
            gotoEdit(dtTugas)
        }

        txtSelesaiTugas.setOnClickListener {
            gotoOpenFile(dtTugas.penyelesaian, dtTugas.tipeFile)
        }

        fileTugas.setOnClickListener {
            gotoOpenFileTugas(dtTugas.fileTugas)
        }

    }

    fun gotoOpenFileTugas(buktiFile: String){
        val intent = Intent(this, OpenFileIzinPenugasanActivity::class.java)
        intent.putExtra("openFile", buktiFile)
        startActivity(intent)
    }

    fun gotoEdit(tugas: Tugas){
        val intent = Intent(this, EditTugasActivity::class.java)
        intent.putExtra("editTugas", tugas)
        startActivity(intent)
    }

    fun gotoOpenFile(buktiFile: String, tipeFile: String){
        if(tipeFile.equals("pdf")){
            val intent = Intent(this, OpenFileIzinPenugasanActivity::class.java)
            intent.putExtra("openFile", buktiFile)
            startActivity(intent)
        }else{
            val alertDialog = AlertDialog.Builder(this)
            val dialog = alertDialog.create()
            val inflater : LayoutInflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_preview_image, null)
            dialog.setView(dialogView)
            dialog.setTitle("File Penugasan")
            dialog.setButton(Dialog.BUTTON_POSITIVE,"close", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }
            })
            Picasso.get().load(EndPoints.URL_FILE_AKTIVITAS+buktiFile).into(dialogView.imageViewPreview)
            dialog.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}