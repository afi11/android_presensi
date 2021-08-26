package codeafifudin.fatakhul.projectta.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import codeafifudin.fatakhul.projectta.controllers.PresensiController
import codeafifudin.fatakhul.projectta.services.NotPresenceService

class NotPresenceBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val intent = Intent(context,NotPresenceService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context!!.startForegroundService(intent)
        }else{
            context!!.startService(intent)
        }
    }
}