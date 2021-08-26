package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import android.util.Log
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Aktivitas
import codeafifudin.fatakhul.projectta.models.Tugas
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class PenugasanController(context: Context) {

    var auth : Auth ?= null
    val context = context

    fun postTugas(tugas: Tugas, callBack: (Boolean) -> Unit){
        auth = Auth(context)
        val jsonBody = JSONObject()
        jsonBody.put("file",tugas.penyelesaian)
        jsonBody.put("tipefile", tugas.tipeFile)

        val jsonRequest : JsonObjectRequest = object : JsonObjectRequest(Request.Method.PUT,
            EndPoints.URL_POSTTUGAS+"/${tugas.id}",jsonBody,
            Response.Listener { response ->
                Log.d("AktivitasResponse",response.toString())
                callBack(true)
            },
            Response.ErrorListener { error ->
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