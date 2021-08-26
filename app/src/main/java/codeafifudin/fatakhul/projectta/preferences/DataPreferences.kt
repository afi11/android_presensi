package codeafifudin.fatakhul.projectta.preferences

import android.content.Context
import android.content.SharedPreferences

class DataPreferences(val context: Context) {

    val APPNAME = "KEMENAGAPP"
    val SET_USER_TOKEN = "SET_USER_TOKEN"
    val SET_USER_IDUSER = "SET_USER_IDUSER"
    val SET_USER_TIPE = "SET_USER_TIPE"

    val sharedPref : SharedPreferences = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE)

    fun addUserData(iduser: String, tipePegawai: String, token: String) : Boolean{
        val editor_token : SharedPreferences.Editor = sharedPref.edit()
        val editor_iduser : SharedPreferences.Editor = sharedPref.edit()
        val editor_userTipe : SharedPreferences.Editor = sharedPref.edit()
        editor_token.putString(SET_USER_TOKEN,token)
        editor_iduser.putString(SET_USER_IDUSER, iduser)
        editor_userTipe.putString(SET_USER_TIPE, tipePegawai)
        editor_token.commit()
        editor_iduser.commit()
        editor_userTipe.commit()
        return true
    }

    fun removeUserData(): Boolean {
        val editor_token : SharedPreferences.Editor = sharedPref.edit()
        val editor_iduser : SharedPreferences.Editor = sharedPref.edit()
        val editor_userTipe : SharedPreferences.Editor = sharedPref.edit()
        editor_token.putString(SET_USER_TOKEN,"")
        editor_iduser.putString(SET_USER_IDUSER,"")
        editor_userTipe.putString(SET_USER_TIPE,"")
        editor_iduser.commit()
        editor_token.commit()
        editor_userTipe.commit()
        return true
    }

    fun getUserToken() : String {
        return sharedPref.getString(SET_USER_TOKEN,"").toString()
    }

    fun getUserId() : String {
        return sharedPref.getString(SET_USER_IDUSER,"").toString()
    }

    fun getUserTipePegawai() : String {
        return sharedPref.getString(SET_USER_TIPE,"").toString()
    }

}