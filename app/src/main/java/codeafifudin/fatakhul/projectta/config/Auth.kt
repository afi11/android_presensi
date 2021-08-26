package codeafifudin.fatakhul.projectta.config

import android.content.Context
import codeafifudin.fatakhul.projectta.preferences.DataPreferences

class Auth(val context: Context) {

    var dataPreferences : DataPreferences ?= null

    fun userId() : String {
        dataPreferences = DataPreferences(context)
        return dataPreferences!!.getUserId()
    }

    fun token() : String {
        dataPreferences = DataPreferences(context)
        return dataPreferences!!.getUserToken()
    }

    fun tipePegawai() : String {
        dataPreferences = DataPreferences(context)
        return dataPreferences!!.getUserTipePegawai()
    }

    fun cekUserIsLoggin() : Boolean{
        var state : Boolean
        dataPreferences = DataPreferences(context)
        val userid = dataPreferences!!.getUserId()
        val token = dataPreferences!!.getUserToken()
        val tipePegawai = dataPreferences!!.getUserTipePegawai()
        if(userid != "" && token != "" && tipePegawai != "") state = true
        else state = false
        return state
    }

    fun addUserData(userid: String, tipePegawai: String, token: String) : Boolean {
        dataPreferences = DataPreferences(context)
        return dataPreferences!!.addUserData(userid, tipePegawai, token)
    }

    fun logutUser(): Boolean {
        dataPreferences = DataPreferences(context)
        return dataPreferences!!.removeUserData()
    }

}