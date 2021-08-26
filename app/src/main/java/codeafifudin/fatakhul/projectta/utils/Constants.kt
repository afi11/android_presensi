package codeafifudin.fatakhul.projectta.utils

import com.google.android.gms.maps.model.LatLng
import java.util.*

object Constants {
    //Location
    const val GEOFENCE_ID = "TACME"
    const val GEOFENCE_RADIUS_IN_METERS = 200f

    /**
     * Map for storing information about tacme in the dubai.
     */
    @JvmField
    val AREA_LANDMARKS = HashMap<String, LatLng>()

    init {
        // Tacme
        AREA_LANDMARKS[GEOFENCE_ID] =
            LatLng(-7.8438418, 111.9764279)
    }
}