package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import android.util.Log
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Libur
import codeafifudin.fatakhul.projectta.models.WaktuPresensi
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject
import kotlin.jvm.Throws

class WaktuPresensiController(val context: Context) {

    var auth : Auth ?= null

    fun getWaktu(callBack: (MutableList<WaktuPresensi>) -> Unit){
        auth = Auth(context)
        val listWaktu : MutableList<WaktuPresensi> = ArrayList()
        val requestWaktu = object : StringRequest(Request.Method.GET,"${EndPoints.URL_WAKTU_PRESENSI}/${auth!!.userId()}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val dataWaktu = obj.getJSONArray("data")
                for (i in 0 until dataWaktu.length()){
                    val wp = WaktuPresensi()
                    val waktu = dataWaktu.getJSONObject(i)

                    wp.hari = waktu.getString("hari")
                    wp.jam = waktu.getString("jam_presensi")
                    wp.tipe_presensi = waktu.getString("tipe_presensi")

                    listWaktu.add(wp)
                }
                callBack(listWaktu)
            },Response.ErrorListener { error -> }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance!!.addToRequestQueue(requestWaktu)
    }

    fun cekLibur(callBack: (Libur) -> Unit){
        auth = Auth(context)
        val libur = Libur()
        val requestCek = object : StringRequest(Request.Method.GET, "${EndPoints.URL_CEK_LIBUR}",
                Response.Listener { response ->
                    Log.d("teslibur",response)
                    val obj = JSONObject(response)
                    if(obj.getBoolean("islibur")){
                        val liburObj = obj.getJSONObject("data")
                        if(!liburObj.equals(null)){
                            libur.tipePegawai = liburObj.getString("id_tipepegawai")
                            libur.tanggal = liburObj.getString("tanggal")
                            libur.keterangan = liburObj.getString("keterangan")
                            libur.allPagawai = liburObj.getInt("all_pegawai")
                            libur.isLibur = obj.getBoolean("islibur")
                        }
                    }
                    callBack(libur)
                },Response.ErrorListener { error -> })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance!!.addToRequestQueue(requestCek)
    }

}