package codeafifudin.fatakhul.projectta.models

class Libur(var tanggal: String, var tipePegawai: String,  var allPagawai: Int, var keterangan: String, var isLibur: Boolean) {
    constructor() : this("","",0,"",false)
}