package codeafifudin.fatakhul.projectta.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Tugas(var id: String,var pegawaiID : String, var noSurat : String, var tglBerangkat : String, var tglKembali : String, var perihal: String, var tempatTugas: String,
    var jeniAngkutan : String, var jenisTugas : String, var fileTugas : String, var status : String, var ketAdmin: String, var tipeFile : String, var penyelesaian : String)
    : Parcelable { constructor() : this("","","","","","","","","","","","",
    "","")

}