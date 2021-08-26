package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Message
import codeafifudin.fatakhul.projectta.models.Periode
import codeafifudin.fatakhul.projectta.models.Presensi
import codeafifudin.fatakhul.projectta.models.TidakPresensi
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

class PresensiController(val context: Context) {

    var auth: Auth ?= null

    fun getPeriode(callBack: (Periode) -> Unit){
        auth = Auth(context)
        val requestPeriode = object : StringRequest(Request.Method.GET, "${EndPoints.URL_GET_PERIODE}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val data = obj.getJSONObject("data")
                val periode = Periode()
                periode.periode_awal = data.getString("periode_awal")
                periode.periode_akhir = data.getString("periode_akhir")
                callBack(periode)
            },Response.ErrorListener { error -> }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance!!.addToRequestQueue(requestPeriode)
    }

    fun cekPresensi(callBack: (String) -> Unit){
        auth = Auth(context)
        val requestCek = object : StringRequest(Request.Method.GET,"${EndPoints.URL_CEK_PRESENSI}/${auth!!.userId()}",
            Response.Listener{response ->
                val obj = JSONObject(response)
                callBack(obj.getString("data"))
            },Response.ErrorListener { error -> }){
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

    fun cekAdaIzin(callBack: (TidakPresensi) -> Unit){
        auth = Auth(context)
        val requestCek = object : StringRequest(Request.Method.GET, "${EndPoints.URL_CEK_IZIN}/${auth!!.userId()}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val tidakPresensi = TidakPresensi()
                tidakPresensi.status = obj.getBoolean("status")
                tidakPresensi.tglAwal = obj.getString("tgl_start")
                tidakPresensi.tglAkhir = obj.getString("tgl_end")
                callBack(tidakPresensi)
            }, Response.ErrorListener { error ->}){
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

    fun cekAdaTugas(callBack: (TidakPresensi) -> Unit) {
        auth = Auth(context)
        val requestCek = object : StringRequest(Request.Method.GET,"${EndPoints.URL_CEK_TUGAS}/${auth!!.userId()}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val tidakPresensi = TidakPresensi()
                tidakPresensi.status = obj.getBoolean("status")
                tidakPresensi.tglAwal = obj.getString("tgl_berangkat")
                tidakPresensi.tglAkhir = obj.getString("tgl_kembali")
                callBack(tidakPresensi)
            }, Response.ErrorListener { error ->  }){
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

    fun presensiPegawai(latLong: LatLng, jarakPresensi: Int, tipePresensi: String, waktuPresensi: String, callBack: (Boolean) -> Unit){
        auth = Auth(context)
        if(jarakPresensi > 200){
            Toast.makeText(context,"Silahkan presensi pada area presensi",Toast.LENGTH_SHORT).show()
        }else{
            val jsonBody = JSONObject()
            jsonBody.put("pegawai_id",auth!!.userId())
            jsonBody.put("latitude_presensi",latLong.latitude)
            jsonBody.put("longitude_presensi",latLong.longitude)
            jsonBody.put("jarak_presensi",jarakPresensi)
            jsonBody.put("tipe_presensi",tipePresensi)
            jsonBody.put("waktu_presensi",waktuPresensi)
            //jsonBody.put("tipe_izin","masuk")
            jsonBody.put("bukti_izin",null)

            Log.d("testinput",jsonBody.toString())

            val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, EndPoints.URL_PRESENSI, jsonBody, Response.Listener { response ->
                Log.d("resPresensi ",response.toString())
                callBack(true)
            }, Response.ErrorListener { error ->
                Log.d("ErrorPresensi",error.printStackTrace().toString())
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val params : MutableMap<String, String> = HashMap()
                    params["Authorization"] = auth!!.token()
                    params["content-type"] = "application/json"
                    return params
                }
            }
            App.instance?.addToRequestQueue(jsonRequest)
        }
    }

    fun updateIzinKetidakhadiran(presensi: Presensi, oldFile: String, callBack: (Boolean) -> Unit){
        auth = Auth(context)
        val jsonBody = JSONObject()
        jsonBody.put("tgl_start_izin", presensi.tglStartIzin)
        jsonBody.put("tgl_end_izin", presensi.tglEndIzin)
        jsonBody.put("keterangan", presensi.keterangan)
        jsonBody.put("tipefile",presensi.tipeFileIzin)
        jsonBody.put("tipeIzin",presensi.tipeIzin)
        jsonBody.put("old_file", oldFile)
        jsonBody.put("file",presensi.buktiIzin)
        Log.d("fileInput",jsonBody.toString())

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.PUT,
            "${EndPoints.URL_ABSENSI}/${presensi.id}", jsonBody, Response.Listener { response ->
                callBack(true)
            },Response.ErrorListener { error ->
                Log.d("ErrorAbsensi",error.printStackTrace().toString())
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)

    }

    fun postKetidakhadrian(presensi: Presensi, callBack: (Message) -> Unit){
        auth = Auth(context)
        val jsonBody = JSONObject()
        jsonBody.put("pegawai_id",auth!!.userId())
        jsonBody.put("tipefile",presensi.tipeFileIzin)
        jsonBody.put("file",presensi.buktiIzin)
        jsonBody.put("tgl_start_izin",presensi.tglStartIzin)
        jsonBody.put("tgl_end_izin",presensi.tglEndIzin)
        jsonBody.put("tipeIzin",presensi.tipeIzin)
        jsonBody.put("keterangan",presensi.keterangan)
        jsonBody.put("status_izin","waiting")

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                EndPoints.URL_ABSENSI, jsonBody, Response.Listener { s ->
                try {
                    val message = Message()
                    message.kode = s.getInt("code")
                    message.message = s.getString("message")
                    if(message.kode == 200){
                        message.isSimpan = true
                        callBack(message)
                    }else{
                        message.isSimpan = false
                        callBack(message)
                    }
                }catch (e:JSONException){
                    e.printStackTrace()
                }
        },Response.ErrorListener { error ->
                Log.d("ErrorAbsensi",error.printStackTrace().toString())
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(jsonRequest)
    }


    fun getLogPresensi(callBack: (MutableList<Presensi>) -> Unit){
        auth = Auth(context)
        val logPresensi : MutableList<Presensi> = ArrayList()
        val getLogPresensi = object : StringRequest(
                Request.Method.GET, EndPoints.URL_GET_LOG_PRESENSI+"/"+auth!!.userId(),Response.Listener { s ->
            try {
                val obj = JSONObject(s)
                val array = obj.getJSONArray("data")
                for (i in 0..array.length() - 1){
                    val data = array.getJSONObject(i)
                    val presensi = Presensi()
                    presensi.tgl = data.getString("tgl_presensi")
                    presensi.waktu = data.getString("waktu_presensi")
                    presensi.tipejam = data.getString("tipe_presensi")
                    presensi.jamMasuk = data.getString("jam_masuk")
                    presensi.jamPulang = data.getString("jam_pulang")
                    presensi.statusJamMasuk = data.getString("status_jam_masuk")
                    presensi.statusJamPulang = data.getString("status_jam_pulang")
                    presensi.tipeIzin = data.getString("tipe_izin")
                    presensi.buktiIzin = data.getString("bukti_izin")
                    presensi.keterangan = data.getString("keterangan")
                    presensi.tglStartIzin = data.getString("tgl_start_izin")
                    presensi.tglEndIzin = data.getString("tgl_end_izin")
                    presensi.tipeFileIzin = data.getString("tipe_file")
                    logPresensi.add(presensi)
                }
                callBack(logPresensi)
            }catch (e: JSONException){
                Log.d("ErrorLogPre",e.toString())
            }
        },Response.ErrorListener { error ->
            Log.d("ErrorLogPresensi",error.toString())
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(getLogPresensi)
    }

}