package codeafifudin.fatakhul.projectta.models

class Aktivitas(var pegawaiId: String, var tgl_aktivitas: String, var uraian: String, var kuantitas: String, var satuan: String, var file: String, var tipe: String) {
    constructor() : this("","","","","","","")
}