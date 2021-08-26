package codeafifudin.fatakhul.projectta.views.absensi

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.components.BottomSheetAddFileFragment
import codeafifudin.fatakhul.projectta.controllers.PresensiController
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.utils.Conveter
import codeafifudin.fatakhul.projectta.utils.CustomProgress
import codeafifudin.fatakhul.projectta.utils.DatePickerHelper
import kotlinx.android.synthetic.main.fragment_absensi.view.*
import java.util.*

class AbsensiFragment : Fragment() {

    lateinit var thisParent : MainActivity
    lateinit var v : View
    lateinit var datePicker: DatePickerHelper
    private val progressDialog = CustomProgress()
    var presensiController: PresensiController ?= null
    val presensi = Presensi()

    val tipeIzin = arrayOf("izin", "cuti")
    var tglmulai = ""
    var tglselesai = ""
    var conveter : Conveter?= null
    var tipeFile: String ?= null
    var fileUrl : String ?= null
    var imageUri : Uri ?= null
    var isEdit : Boolean ?= false

    companion object {
        const val TAG_FILE_PDF = 100
        const val TAG_FILE_CAMERA = 101
        const val TAG_FILE_GALERY = 102
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        conveter = Conveter(thisParent)
        datePicker = DatePickerHelper(thisParent)
        presensiController = PresensiController(thisParent)
        v = inflater.inflate(R.layout.fragment_absensi, container, false)


        thisParent.supportActionBar!!.title = "Ketidakhadiran"

        val spinnerAdapter  = ArrayAdapter(thisParent, R.layout.support_simple_spinner_dropdown_item, tipeIzin)

        v.btnAddFile.setOnClickListener {
            val bottomSheetAddFileFragment = BottomSheetAddFileFragment()
            bottomSheetAddFileFragment.show(childFragmentManager, BottomSheetAddFileFragment.TAG)

        }

        v.pickTglMulai.setOnClickListener {
            val cal = Calendar.getInstance()
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            datePicker.showDialog(d,m,y, object : DatePickerHelper.Callback {
                override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                    val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                    val mon = month + 1
                    val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                    tglmulai = "${year}-${monthStr}-${dayStr}"
                    v.textViewTglSelected.setText("Tanggal yang dipilih ${tglmulai}")
                }

            })
        }

        v.pickTglSelesai.setOnClickListener {
            val cal = Calendar.getInstance()
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val m = cal.get(Calendar.MONTH)
            val y = cal.get(Calendar.YEAR)

            datePicker.showDialog(d,m,y, object : DatePickerHelper.Callback {
                override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                    val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                    val mon = month + 1
                    val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                    tglselesai = "${year}-${monthStr}-${dayStr}"
                    v.textViewTglSelected2.setText("Tanggal yang dipilih ${tglmulai}")
                }

            })
        }

        v.buttonSubmitAbsensi.setOnClickListener {
            progressDialog.show(thisParent,"Menyimpan data....")
            presensi.tglStartIzin = tglmulai
            presensi.tglEndIzin = tglselesai
            presensi.keterangan = v.editTextKeterangan.text.toString()
            presensiController!!.postKetidakhadrian(presensi){
                progressDialog.hide(thisParent)
                Toast.makeText(thisParent,"Berhasil menyimpan data",Toast.LENGTH_LONG).show()
                fileUrl = ""
                tglmulai = ""
                tglselesai = ""
                v.txtSelectedFile.setText("")
                v.textViewTglSelected.setText("Pilih tanggal mulai izin")
                v.textViewTglSelected2.setText("Pilih tanggal selesai izin")
                v.editTextKeterangan.setText("")
            }
        }

        return v
    }

    fun openPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("application/pdf")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select File Pdf"), TAG_FILE_PDF)
    }

    fun openCamera(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE,"Select Picture from Camera")
        imageUri = thisParent.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, TAG_FILE_CAMERA)
    }

    fun openGalery() {
        val galeryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galeryIntent, TAG_FILE_GALERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == TAG_FILE_PDF && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.getBase64ForUriAndPossiblyCrash(data!!.data!!)
            presensi.buktiIzin = fileUrl.toString()
            v.txtSelectedFile.text = "File pdf sudah diambil"
            tipeFile = "pdf"
        }
        if(requestCode == TAG_FILE_CAMERA && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(imageUri!!))
            presensi.buktiIzin = fileUrl.toString()
            v.txtSelectedFile.text = "File gambar sudah diambil"
            tipeFile = "image"
        }
        if(requestCode == TAG_FILE_GALERY && resultCode == AppCompatActivity.RESULT_OK){
            fileUrl = conveter!!.encodeImageToBase64(conveter!!.encodeBitmap(data!!.data!!))
            presensi.buktiIzin = fileUrl.toString()
            v.txtSelectedFile.text = "File gambar sudah diambil"
            tipeFile = "image"
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        BottomSheetAddFileFragment().dialog?.setCancelable(true)
    }



}