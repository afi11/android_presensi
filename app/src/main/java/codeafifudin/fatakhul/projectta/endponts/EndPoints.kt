package codeafifudin.fatakhul.projectta.endponts

object EndPoints {
    private val url_root = "http://192.168.10.60/"
   // private val url_root = "https://kemenagkabjombang.my.id/presensi_tugas_baru/public/"
    private val root_api = "${url_root}api/"
    val URL_PROFIL = "${url_root}files/images_profil/"
    val URL_FILE_IZIN = "${url_root}files/absensi/"
    val URL_FILE_AKTIVITAS = "${url_root}files/aktivitas/"
    val URL_FILE_TUGAS = "${url_root}files/tugas/"

    val api_tempat_presensi = root_api+"get_tempat"
    val URL_LOGIN = "${root_api}login"
    val URL_LOGOUT = "${root_api}logout"
    val URL_USER_BY_ID = "${root_api}getuser"
    val URL_PRESENSI = "${root_api}presensi"
    val URL_WAKTU_PRESENSI = "${root_api}get_waktu"
    val URL_CEK_PRESENSI = "${root_api}cek_presensi"
    val URL_ABSENSI = "${root_api}absensi"
    val URL_GET_LOG_PRESENSI = "${root_api}logpresensi"
    val URL_GET_LOG_ABSENSI = "${root_api}logabsensi"
    val URL_CEK_LIBUR = "${root_api}cek_libur"
    val URL_CHART_PRESENSI = "${root_api}chart_presensi"
    val URL_AKTIVITAS = "${root_api}logaktivitas"
    val URL_POSTAKTIVITAS = "${root_api}postaktivitas"
    val URL_ITEM_DASHBOARD = "${root_api}item_dashboard"
    val URL_CHART_TUGAS = "${root_api}chart_tugas"
    val URL_GET_LOG_TUGAS = "${root_api}logtugas"
    val URL_POSTTUGAS = "${root_api}update_tugas"
    val URL_STATISTIK_PRESENSI = "${root_api}statistik_presensi"
    val URL_GET_PERIODE = "${root_api}get_periode"
    val URL_DOWNLOAD_TEMPLATE = "${root_api}cetak_form_penyelesaian"
    val URL_CEK_TUGAS = "${root_api}cek_ada_tugas"
    val URL_CEK_IZIN = "${root_api}cek_ada_izin"
}