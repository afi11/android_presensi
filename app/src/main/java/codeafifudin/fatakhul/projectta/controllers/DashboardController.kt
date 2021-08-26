package codeafifudin.fatakhul.projectta.controllers

import android.content.Context
import codeafifudin.fatakhul.projectta.App
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.models.ChartPresensi
import codeafifudin.fatakhul.projectta.models.ItemDashboard
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import kotlin.jvm.Throws

class DashboardController(val context : Context) {

    var auth : Auth ?= null

    fun getItemDashboard(callback:(MutableList<ItemDashboard>) -> Unit){
        auth = Auth(context)
        val listItemDashboard : MutableList<ItemDashboard> = ArrayList()
        val requestDashboard = object : StringRequest(Request.Method.GET, "${EndPoints.URL_ITEM_DASHBOARD}/${auth!!.userId()}/${auth!!.tipePegawai()}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val dataWaktu = obj.getJSONArray("waktu")
                val dataPresensi = obj.getJSONArray("presensi")
                if(dataWaktu.length() > 0){
                    val itemDashboard = ItemDashboard()
                    itemDashboard.jamMasuk = dataWaktu.getJSONObject(0).getString("jam_presensi")
                    itemDashboard.jamPulang = dataWaktu.getJSONObject(1).getString("jam_presensi")
                    if(dataPresensi.length() > 0 && dataPresensi.length() == 1){
                        itemDashboard.ketMasuk = dataPresensi.getJSONObject(0).getString("status_presensi")
                        itemDashboard.ketPulang = "Belum presensi"
                    }else if(dataPresensi.length() == 2){
                        itemDashboard.ketMasuk = dataPresensi.getJSONObject(0).getString("status_presensi")
                        itemDashboard.ketPulang = dataPresensi.getJSONObject(1).getString("status_presensi")
                    }else{
                        itemDashboard.ketMasuk = "Belum presensi"
                        itemDashboard.ketPulang = "Belum presensi"
                    }
                    listItemDashboard.add(itemDashboard)
                    callback(listItemDashboard)
                }
            }, Response.ErrorListener { error ->  }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance!!.addToRequestQueue(requestDashboard)
    }

    fun getTugasChart(callback: (MutableList<ChartPresensi>) -> Unit){
        auth = Auth(context)
        val listPresensi : MutableList<ChartPresensi> = ArrayList()
        val requestChartPresensi = object : StringRequest(Request.Method.GET, "${EndPoints.URL_CHART_TUGAS}/${auth!!.userId()}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val dataChart = obj.getJSONArray("data")
                for (i in 0 until dataChart.length()){
                    val chart = ChartPresensi()
                    val presensiData = dataChart.getJSONObject(i)
                    chart.label = presensiData.getString("label")
                    chart.data = presensiData.getInt("data")
                    listPresensi.add(chart)
                }
                callback(listPresensi)
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
        App.instance!!.addToRequestQueue(requestChartPresensi)
    }

    fun getPresensiChart(tipePresensi : String, callback: (MutableList<ChartPresensi>) -> Unit){
        auth = Auth(context)
        val listChartPresensi : MutableList<ChartPresensi> = ArrayList()
        val requestChartPresensi = object : StringRequest(Request.Method.GET,"${EndPoints.URL_CHART_PRESENSI}/${auth!!.userId()}/${tipePresensi}",
            Response.Listener { response ->
                val obj = JSONObject(response)
                val dataChart = obj.getJSONArray("data")
                for (i in 0 until dataChart.length()){
                    val chart = ChartPresensi()
                    val presensiData = dataChart.getJSONObject(i)
                    chart.label = presensiData.getString("label")
                    chart.data = presensiData.getInt("data")
                    listChartPresensi.add(chart)
                }
                callback(listChartPresensi)
            }, Response.ErrorListener { error ->  }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = auth!!.token()
                params["content-type"] = "application/json"
                return params
            }
        }
        App.instance!!.addToRequestQueue(requestChartPresensi)
    }

}