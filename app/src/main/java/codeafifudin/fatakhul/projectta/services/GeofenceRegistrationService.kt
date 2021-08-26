package codeafifudin.fatakhul.projectta.services

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.utils.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.*

class GeofenceRegistrationService : IntentService(TAG) {
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "GeofencingEvent error " + geofencingEvent.errorCode)
        } else {
            val transaction = geofencingEvent.geofenceTransition
            val geofences = geofencingEvent.triggeringGeofences
            val geofence = geofences[0]
            if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.requestId == Constants.GEOFENCE_ID) {
                Log.d(TAG, "You are inside Tacme")
            } else {
                Log.d(TAG, "You are outside Tacme")
            }
            val geofenceTransitionDetails = getGeofenceTrasitionDetails(transaction, geofences)
            sendNotification(geofenceTransitionDetails)
        }
    }

    // Create a detail message with Geofences received
    private fun getGeofenceTrasitionDetails(
        geoFenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {
        // get the ID of each geofence triggered
        val triggeringGeofencesList = ArrayList<String?>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesList.add(geofence.requestId)
        }
        var status: String? = null
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) status =
            "Entering " else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) status =
            "Exiting "
        return status + TextUtils.join(", ", triggeringGeofencesList)
    }

    // Send a notification
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun sendNotification(msg: String) {
        Log.i(TAG, "sendNotification: $msg")

        // Intent to start the main Activity
        val notificationIntent = Intent(this, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(notificationIntent)
        val notificationPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // Creating and sending Notification
        val notificatioMng = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificatioMng.notify(
            0,
            createNotification(msg, notificationPendingIntent)
        )
    }

    // Create a notification
    private fun createNotification(
        msg: String,
        notificationPendingIntent: PendingIntent
    ): Notification {
        val notificationBuilder = NotificationCompat.Builder(this)
        notificationBuilder
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(Color.RED)
            .setContentTitle(msg)
            .setContentText("Geofence Notification!")
            .setContentIntent(notificationPendingIntent)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            .setAutoCancel(true)
        return notificationBuilder.build()
    }

    companion object {
        private const val TAG = "GeoIntentService"

        // Handle errors
        private fun getErrorString(errorCode: Int): String {
            return when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GeoFence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many GeoFences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
                else -> "Unknown error."
            }
        }
    }
}