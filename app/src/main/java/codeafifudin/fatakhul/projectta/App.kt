package codeafifudin.fatakhul.projectta

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val requestQueue: RequestQueue? = null
        get(){
            if(field == null){
                return Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>){
        request.tag =TAG
        requestQueue?.add(request)
    }

    companion object {
        private val TAG = App::class.java.simpleName
        @get:Synchronized var instance: App? = null
            private set
    }

}