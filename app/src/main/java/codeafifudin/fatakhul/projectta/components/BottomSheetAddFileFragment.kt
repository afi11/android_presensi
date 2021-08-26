package codeafifudin.fatakhul.projectta.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.views.absensi.AbsensiFragment
import codeafifudin.fatakhul.projectta.views.absensi.TambahAbsensiActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_add_file.view.*

class BottomSheetAddFileFragment : BottomSheetDialogFragment() {

    lateinit var thisParent : TambahAbsensiActivity
    lateinit var v : View

    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisParent = activity as TambahAbsensiActivity
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
    }


}