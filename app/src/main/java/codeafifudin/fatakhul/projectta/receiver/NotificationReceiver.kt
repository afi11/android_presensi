package codeafifudin.fatakhul.projectta.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import codeafifudin.fatakhul.projectta.services.NotPresenceService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val service = Intent(context, NotPresenceService::class.java)
        service.putExtra("reason", intent!!.getStringExtra("reason"))
        service.putExtra("timestamp", intent!!.getLongExtra("timestamp", 0))
        context!!.startService(service)
    }
}