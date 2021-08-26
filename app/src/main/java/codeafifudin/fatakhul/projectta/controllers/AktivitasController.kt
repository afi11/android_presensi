package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import android.util.Log
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Aktivitas
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class AktivitasController(val context: Context) {

    var auth: Auth ?= null

    fun postAktivitas(aktivitas: Aktivitas,callBack: (Boolean) -> Unit){
        auth = Auth(context)
        val jsonBody = JSONObject()
        jsonBody.put("pegawai_id",auth!!.userId())
        jsonBody.put("uraian",aktivitas.uraian)
        jsonBody.put("kuantitas",aktivitas.kuantitas)
        jsonBody.put("satuan",aktivitas.satuan)
        jsonBody.put("file",aktivitas.file)
        jsonBody.put("tipefile", aktivitas.tipe)

        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,EndPoints.URL_POSTAKTIVITAS,jsonBody,
            Response.Listener { response ->
            Log.d("AktivitasResponse",response.toString())
                callBack(true)
        },Response.ErrorListener { error ->
            Log.d("ErrorAktivitas",error.printStackTrace().toString())
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