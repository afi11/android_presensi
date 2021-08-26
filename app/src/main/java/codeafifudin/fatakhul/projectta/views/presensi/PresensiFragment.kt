package codeafifudin.fatakhul.projectta.views.presensi

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.controllers.PresensiController
import codeafifudin.fatakhul.projectta.controllers.TempatPresensiController
import codeafifudin.fatakhul.projectta.controllers.WaktuPresensiController
import codeafifudin.fatakhul.projectta.services.GeofenceRegistrationService
import codeafifudin.fatakhul.projectta.utils.Constants
import codeafifudin.fatakhul.projectta.utils.GetWaktu
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_presensi.view.*

class PresensiFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener  {

    lateinit var thisParent : MainActivity
    lateinit var v : View

    var tempatPresensiController : TempatPresensiController ?= null
    var presensiController : PresensiController ?= null
    var waktuPresensiController : WaktuPresensiController ?= null
    var auth : Auth ?= null
    var getWaktu : GetWaktu ?= null

    var jarakPresensi : Int = 0
    var tipePresensi : String = ""
    var waktuPresen : String = ""
    var messagePresensi : String = ""
    var dptPresensi : Boolean = true
    var isGetPresensi : Boolean = true
    lateinit var latLong : LatLng
    var handler : Handler = Handler()

    private var googleMap: GoogleMap? = null
    private var geofencingRequest: GeofencingRequest? = null
    private var googleApiClient: GoogleApiClient? = null
    private var isMonitoring = false
    private var markerOptions: MarkerOptions? = null
    private var currentLocationMarker: Marker? = null
    private var pendingIntent: PendingIntent? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        tempatPresensiController = TempatPresensiController(thisParent)
        presensiController = PresensiController(thisParent)
        waktuPresensiController = WaktuPresensiController(thisParent)
        auth = Auth(thisParent)
        getWaktu = GetWaktu(thisParent)
        v = inflater.inflate(R.layout.fragment_presensi, container, false)

        isGetPresensi = getWaktu!!.isTimeBetweenTwoTime(getWaktu!!.currentClock(),"16:00:00")

        locationEnabled()
        initView()

        return v
    }

    private fun initView() {

        thisParent.supportActionBar!!.title = "Presensi"
        val mapFragment = childFragmentManager.findFragmentById(R.id.supportMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        googleApiClient = GoogleApiClient.Builder(thisParent)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()

        if (ActivityCompat.checkSelfPermission(thisParent, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(thisParent,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                ActivityCompat.requestPermissions(
                        thisParent,
                        arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        REQUEST_LOCATION_PERMISSION_CODE
                )
            }else{
                ActivityCompat.requestPermissions(
                        thisParent,
                        arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                        REQUEST_LOCATION_PERMISSION_CODE
                )
            }

        }

        v.button_presensi_peg.setOnClickListener {
            if(dptPresensi){
                if(tipePresensi.equals("end")){
                    Toast.makeText(thisParent,"Presensi Hari Sudah dilakukan",Toast.LENGTH_LONG).show()
                }else{
                    if(tipePresensi.equals("jam_pulang")){
                        if(isGetPresensi){
                            presenNow()
                        }else{
                            Toast.makeText(thisParent,"Untuk presensi jam pulang silahkan lakukan diatas jam 12 siang!",Toast.LENGTH_LONG).show()
                        }
                    }else{
                        presenNow()
                    }
                }
            }else{
                Toast.makeText(thisParent,messagePresensi,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun presenNow(){
        presensiController!!.presensiPegawai(latLong,jarakPresensi,tipePresensi,waktuPresen){
            var tipe = ""
            if(tipePresensi.equals("jam_masuk")) tipe = "jam masuk"
            else tipe = "jam pulang"
            Toast.makeText(thisParent,"Berhasil melakukan presensi ${tipe}!",Toast.LENGTH_LONG).show()
            getWaktu()
        }
    }

    private fun locationEnabled(){
        val lm = thisParent.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled : Boolean = false
        var networkEnabled : Boolean = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder(thisParent)
                .setMessage("GPS Harap diaktifkan")
                .setPositiveButton("Pengaturan"){
                    dialogInterface, paramInt -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                 }
                .setCancelable(false)
                .show()
        }
    }

    private fun startLocationMonitor() {
        Log.d(TAG, "start location monitor")
        val locationRequest = LocationRequest.create()
            .setInterval(2000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest
            ) { location ->
                if (currentLocationMarker != null) {
                    currentLocationMarker!!.remove()
                }
                markerOptions = MarkerOptions()
                markerOptions!!.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                markerOptions!!.position(LatLng(location.latitude, location.longitude))
                markerOptions!!.title("Current Location")
                currentLocationMarker = googleMap!!.addMarker(markerOptions)

                if(dptPresensi){
                    tempatPresensiController!!.getLokasiPresensi {
                        val loc1 : Location = Location("")
                        val loc2 : Location = Location("")
                        loc1.latitude = location.latitude
                        loc1.longitude = location.longitude
                        loc2.latitude = it.lokasi.latitude
                        loc2.longitude = it.lokasi.longitude
                        val distanInMeter : Float = loc1.distanceTo(loc2)

                        latLong = LatLng(location.latitude, location.longitude)
                        jarakPresensi = distanInMeter.toInt()
                        v.txtJarakPresensi.setText("Jarak dengan tempat presensi ${Math.round(distanInMeter)} m")
                    }

                }
                Log.d(
                    TAG,
                    "Location Change Lat Lng " + location.latitude + " " + location.longitude
                )
            }
        } catch (e: SecurityException) {
            Log.d(TAG, (e.message)!!)
        }
    }

    private fun getWaktu(){
        presensiController!!.cekAdaTugas {
            if(it.status){
                dptPresensi = false
                v.button_presensi_peg.visibility = View.GONE
                v.backWarningPresensi.setBackgroundColor(resources.getColor(R.color.info))
                v.txtJarakPresensi.setText("Tidak perlu presensi, sedang masa tugas ${it.tglAwal} sd ${it.tglAkhir}")
            }else{
                presensiController!!.cekAdaIzin {
                    if(it.status){
                        dptPresensi = false
                        v.button_presensi_peg.visibility = View.GONE
                        v.backWarningPresensi.setBackgroundColor(resources.getColor(R.color.info))
                        v.txtJarakPresensi.setText("Tidak perlu presensi, sedang masa izin ${it.tglAwal} sd ${it.tglAkhir}")
                    }else{

                        waktuPresensiController!!.cekLibur {
                            if(getWaktu!!.getDateNow().equals(it.tanggal)){
                                if(it.allPagawai == 1){
                                    if(it.isLibur) dptPresensi = false
                                    else dptPresensi = true
                                    messagePresensi = "Hari libur, Tidak ada presensi hari ini!"
                                    Toast.makeText(thisParent,"Hari libur, Tidak ada presensi hari ini!",Toast.LENGTH_LONG).show()
                                    v.backWarningPresensi.setBackgroundColor(resources.getColor(R.color.danger))
                                    v.txtJarakPresensi.setText("Hari libur, ${it.keterangan}")
                                }
                                else if(it.tipePegawai.equals(auth!!.tipePegawai())){
                                    if(it.isLibur) dptPresensi = false
                                    else dptPresensi = true
                                    messagePresensi = "Hari libur, Tidak ada presensi hari ini!"
                                    Toast.makeText(thisParent,"Hari libur, Tidak ada presensi hari ini!",Toast.LENGTH_LONG).show()
                                    v.backWarningPresensi.setBackgroundColor(resources.getColor(R.color.danger))
                                    v.txtJarakPresensi.setText("Hari libur, ${it.keterangan}")
                                }
                                else true
                            }else{
                                waktuPresensiController!!.getWaktu(){ waktu ->
                                    if(waktu.size > 1) {
                                        presensiController!!.cekPresensi {
                                            if(it.equals("jam_pulang")){
                                                tipePresensi = "jam_pulang"
                                                waktuPresen = waktu.get(1).jam
                                            }else if(it.equals("free")){
                                                tipePresensi = "jam_masuk"
                                                waktuPresen = waktu.get(0).jam
                                            }else{
                                                tipePresensi = "end"
                                                waktuPresen = waktu.get(1).jam
                                            }
                                        }
                                    }
                                    else if(waktu.size == 1){
                                        presensiController!!.cekPresensi {
                                            if(it.equals("free")){
                                                tipePresensi = waktu.get(0).tipe_presensi
                                                waktuPresen = waktu.get(0).jam
                                            }else{
                                                tipePresensi = "end"
                                                waktuPresen = waktu.get(0).jam
                                            }
                                        }
                                    }
                                    else{
                                        dptPresensi = false
                                        messagePresensi = "Tidak ada waktu presensi hari ini!"
                                        Toast.makeText(thisParent,"Tidak ada waktu presensi hari ini",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private fun startGeofencing(latLng: LatLng) {
        Log.d(TAG, "Start geofencing monitoring call")
        pendingIntent = geofencePendingIntent
        geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            .addGeofence(geofence(latLng))
            .build()
        if (!googleApiClient!!.isConnected) {
            Log.d(TAG, "Google API client not connected")
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    geofencingRequest,
                    pendingIntent
                ).setResultCallback(
                    ResultCallback { status ->
                        if (status.isSuccess) {
                            Log.d(TAG, "Successfully Geofencing Connected")
                        } else {
                            Log.d(TAG, "Failed to add Geofencing " + status.status)
                        }
                    })
            } catch (e: SecurityException) {
                Log.d(TAG, (e.message)!!)
            }
        }
        isMonitoring = true
    }

    private fun geofence(latLng: LatLng): Geofence {
        return Geofence.Builder()
            .setRequestId(Constants.GEOFENCE_ID)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setCircularRegion(
                latLng.latitude,
                latLng.longitude,
                Constants.GEOFENCE_RADIUS_IN_METERS
            )
            .setNotificationResponsiveness(1000)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }


    private val geofencePendingIntent: PendingIntent
        private get() {
            if (pendingIntent != null) {
                return pendingIntent as PendingIntent
            }
            val intent = Intent(thisParent, GeofenceRegistrationService::class.java)
            return PendingIntent.getService(thisParent, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    private fun stopGeoFencing() {
        pendingIntent = geofencePendingIntent
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent)
            .setResultCallback(object : ResultCallback<Status> {
                override fun onResult(status: Status) {
                    if (status.isSuccess) Log.d(TAG, "Stop geofencing") else Log.d(
                        TAG,
                        "Not stop geofencing"
                    )
                }
            })
        isMonitoring = false
    }

    override fun onResume() {
        super.onResume()
        val response =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(thisParent)
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Service Not Available")
            GoogleApiAvailability.getInstance().getErrorDialog(thisParent, response, 1)
                .show()
        } else {
            Log.d(TAG, "Google play service available")
        }
    }


    override fun onStart() {
        super.onStart()
        googleApiClient!!.reconnect()
        getWaktu()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient!!.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                thisParent,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                thisParent,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        this.googleMap = googleMap

        tempatPresensiController!!.getLokasiPresensi {
            googleMap.addMarker(MarkerOptions().position(it.lokasi).title(it.namaTempat))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.lokasi, 17f))

            val circle = googleMap.addCircle(
                CircleOptions()
                    .center(it.lokasi)
                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS.toDouble())
                    .strokeColor(resources.getColor(R.color.colorBg))
                    .fillColor(Color.parseColor("#2218bf8a"))
                    .strokeWidth(4f)
            )
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "Google Api Client Connected")
        tempatPresensiController!!.getLokasiPresensi  {
            startGeofencing(it.lokasi)
        }
        startLocationMonitor()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "Google Connection Suspended")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "Connection Failed:" + connectionResult.errorMessage)
    }

    companion object {
        private val TAG = "MainActivity"
        private val REQUEST_LOCATION_PERMISSION_CODE = 101
    }


}