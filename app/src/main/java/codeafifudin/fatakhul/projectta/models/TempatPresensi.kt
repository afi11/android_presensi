package codeafifudin.fatakhul.projectta.models

import com.google.android.gms.maps.model.LatLng

class TempatPresensi(var namaTempat: String, var lokasi: LatLng) {
    constructor() : this("",LatLng(0.0,0.0))
}