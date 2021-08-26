package codeafifudin.fatakhul.projectta.views.aktivitas

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import kotlinx.android.synthetic.main.activity_open_file_izin.*
import java.io.File

class OpenFileIzinAktivitasActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var urlFile : String
    lateinit var tipeFile : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_file_izin)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }

        toolbar = findViewById(R.id.toobarOpenFile)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "File Aktivitas"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        val backArrow = resources.getDrawable(R.drawable.ic_arrow_back)
        backArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        tipeFile = intent.getStringExtra("tipeFile").toString()

        urlFile = EndPoints.URL_FILE_AKTIVITAS+intent.getStringExtra("openFile")

        PRDownloader.initialize(applicationContext)

        downloadPdfFromIntenet(urlFile, getRootDirPath(this), "pdffile.pdf")

    }

    private fun downloadPdfFromIntenet(url: String, dirPath: String, fileName: String){
        PRDownloader.download(url, dirPath, fileName).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    val downloadedFile = File(dirPath, fileName)
                    progressBar.visibility = View.GONE
                    showPdfFromFile(downloadedFile)
                }

                override fun onError(error: Error?) {
                    Toast.makeText(
                        this@OpenFileIzinAktivitasActivity,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
    }

    private fun showPdfFromFile(file: File){
        pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->
                Toast.makeText(
                    this@OpenFileIzinAktivitasActivity,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    fun getRootDirPath(context: Context): String {
        return if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
            val file: File = ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
            file.absolutePath
        }else{
            context.applicationContext.filesDir.absolutePath
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}