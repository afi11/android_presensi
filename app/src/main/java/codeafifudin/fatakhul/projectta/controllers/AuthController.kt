package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.Pegawai
import codeafifudin.fatakhul.projectta.utils.Conveter
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import javax.xml.transform.ErrorListener

class AuthController(val context: Context) {

    var auth : Auth ?= null
    var conveter : Conveter ?= null

    fun requestLogin(username: String, password: String, callBack: (Boolean) -> Unit){
        auth = Auth(context)
        val request = object : StringRequest(
                Request.Method.POST, EndPoints.URL_LOGIN,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        val token =  "Bearer "+obj.getString("access_token")
                        val userid = obj.getString("userid")
                        val tipePegawai = obj.getString("tipePegawai")
                        callBack(auth!!.addUserData(userid,tipePegawai,token))
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("LoginActivity",error?.message.toString())
                callBack(false)
            }
        }
        ){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("username",username)
                params.put("password",password)
                return params
            }
        }
        App.instance?.addToRequestQueue(request)
    }

    fun getDataUser(callBack: (Pegawai) -> Unit){
        auth = Auth(context)
        val request = object : StringRequest(Method.GET,EndPoints.URL_USER_BY_ID,
            Response.Listener { s ->
                val obj = JSONObject(s)
                val user = obj.getJSONObject("data")
                val nip = user.getString("nip")
                val nama = nip +"-"+ user.getString("nama")
                val pangkatGolongan = user.getString("pangkat") +"-"+user.getString("golongan")
                val foto = user.getString("foto")
                callBack(Pegawai(nip,nama,pangkatGolongan,foto))
            },object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("AuthController",error.toString())
                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(request)
    }

    fun logout(callBack: (Boolean) -> Unit){
        auth = Auth(context)
        conveter = Conveter(context)
        val jsonBody = JSONObject()
        val token = conveter!!.removerCharacter(auth!!.token(),"Bearer")
        jsonBody.put("token",token)
        val logutRequest = object : JsonObjectRequest(Request.Method.POST,EndPoints.URL_LOGOUT,
            jsonBody, Response.Listener{ response ->
                callBack(auth!!.logutUser())
                Toast.makeText(context,response.getString("message"),Toast.LENGTH_LONG).show()
            },object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params : MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance?.addToRequestQueue(logutRequest)
    }

}