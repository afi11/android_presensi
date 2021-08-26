package codeafifudin.fatakhul.projectta.models

class TidakPresensi(var status: Boolean, var tglAwal: String, var tglAkhir: String) {
    constructor() : this(false, "","")
}