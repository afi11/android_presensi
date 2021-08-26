package codeafifudin.fatakhul.projectta.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
class Presensi(var id: String, var pegawai_id: String,
               var latLng: LatLng, var tgl: String, var waktu: String, var jarak: Double, var rule: String, var tipejam: String,
                var tipeIzin: String, var tglStartIzin: String, var tglEndIzin: String, var lamaIzin: String,var buktiIzin: String, var jamMasuk: String, var jamPulang: String, var statusJamMasuk: String,
               var statusJamPulang: String, var tipeFileIzin: String, var keterangan: String, var statusIzin: String, var ketFromAdmin: String) : Parcelable  {
    constructor() : this("","",LatLng(0.0,0.0),"","",0.0,"","","","","",
        "","","","","","","","","","")
}