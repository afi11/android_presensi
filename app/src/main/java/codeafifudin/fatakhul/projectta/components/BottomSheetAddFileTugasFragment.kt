package codeafifudin.fatakhul.projectta.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.views.absensi.AbsensiFragment
import codeafifudin.fatakhul.projectta.views.absensi.TambahAbsensiActivity
import codeafifudin.fatakhul.projectta.views.aktivitas.TambahAktivitasActivity
import codeafifudin.fatakhul.projectta.views.penugasan.EditTugasActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_add_file.view.*

class BottomSheetAddFileTugasFragment : BottomSheetDialogFragment() {

    lateinit var thisParent : EditTugasActivity
    lateinit var v : View

    companion object {
        const val TAG = "CustomBottomSheetDialogFragmentAktivtas"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisParent = activity as EditTugasActivity
        v = inflater.inflate(R.layout.fragment_bottom_sheet_add_file, container, false)

        initView()

        return v
    }

    private fun initView() {
        v.btnPickPdf.setOnClickListener {
            thisParent.openPdfFile()
        }

        v.btnPickCamera.setOnClickListener {
            thisParent.openCamera()
        }

        v.btnPickGalery.setOnClickListener {
            thisParent.openGalery()
        }

        v.btnPickDoc.setOnClickListener {
            thisParent.openFileDoc()
        }

    }


}