package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import android.util.Log
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.TempatPresensi
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

class TempatPresensiController(val context: Context) {

    var auth: Auth ?=null

    fun getLokasiPresensi(callBack: (TempatPresensi) -> Unit){
        auth = Auth(context)
        val requst = object : StringRequest(
            Request.Method.GET, EndPoints.api_tempat_presensi+"/"+auth!!.userId(),
            Response.Listener { s ->
                try {
                    val obj = JSONObject(s)
                    val data = obj.getJSONObject("data")
                    val namaTempat = data.getString("nama_tempat")
                    val latitude = data.getString("latitude_tempat").toDouble()
                    val longitude = data.getString("longitude_tempat").toDouble()
                    callBack(TempatPresensi(namaTempat, LatLng(latitude,longitude)))
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("PresensiActivity",error!!.message.toString())
                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(requst)
    }

}