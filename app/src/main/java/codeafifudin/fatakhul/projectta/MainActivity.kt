
package codeafifudin.fatakhul.projectta

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.controllers.AuthController
import codeafifudin.fatakhul.projectta.endponts.EndPoints
import codeafifudin.fatakhul.projectta.services.NotPresenceService
import codeafifudin.fatakhul.projectta.utils.NotificationUtils
import codeafifudin.fatakhul.projectta.views.absensi.RiwayatAbsensiFragment
import codeafifudin.fatakhul.projectta.views.aktivitas.AktivitasFragment
import codeafifudin.fatakhul.projectta.views.alarm.AlarmPresensiActivity
import codeafifudin.fatakhul.projectta.views.auth.LoginActivity
import codeafifudin.fatakhul.projectta.views.dashboard.DashboardFragment
import codeafifudin.fatakhul.projectta.views.penugasan.PenugasanFragment
import codeafifudin.fatakhul.projectta.views.presensi.PresensiFragment
import codeafifudin.fatakhul.projectta.views.presensi.RiwayatPresensiFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_header_drawer.*
import java.util.*

class MainActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    var auth: Auth ?= null
    var authController: AuthController ?= null
    var isabsen = false

    lateinit var ft: FragmentTransaction
    lateinit var dashboardFragment: DashboardFragment
    lateinit var presensiFragment: PresensiFragment
    lateinit var riwayatPresensiFragment: RiwayatPresensiFragment
    lateinit var riwayatAbsensiFragment: RiwayatAbsensiFragment
    lateinit var aktivitasFragment: AktivitasFragment
    lateinit var penugasanFragment: PenugasanFragment

    private val mNotificationTime = Calendar.getInstance().timeInMillis + 5000
    private var mNotified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().subscribeToTopic("PresensiNotification")

        auth = Auth(this)
        authController = AuthController(this)

        val intent = Intent(this, NotPresenceService::class.java)
        startService(intent)

        redirectLogin()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(Intent(applicationContext, NotPresenceService::class.java))
        }else{
            startService(Intent(applicationContext, NotPresenceService::class.java))
        }
        initView()

    }

    private fun redirectLogin(){
        if(!auth!!.cekUserIsLoggin()){
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }

        supportActionBar!!.title = "Home"
        toolbar.setTitleTextColor(resources.getColor(R.color.white))

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.setCheckedItem(R.id.menu_app_dashboard)

        var toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0)
        drawerLayout.addDrawerListener(toggle)
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        if(!auth!!.userId().equals("") && !auth!!.token().equals("")){
            authController!!.getDataUser {
                if(it.nama != null){
                    textview_nama_pegawai.setText(it.nama)
                    textViewPangkatGolongan.setText(it.pangkatGolongan)
                    Picasso.get().load(EndPoints.URL_PROFIL+it.foto).into(profile_image)
                }
            }
        }

        dashboardFragment = DashboardFragment()
        presensiFragment = PresensiFragment()
        riwayatPresensiFragment = RiwayatPresensiFragment()
        riwayatAbsensiFragment = RiwayatAbsensiFragment()
        aktivitasFragment = AktivitasFragment()
        penugasanFragment = PenugasanFragment()

        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, dashboardFragment).commit()
        frameLayout.visibility = View.VISIBLE

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_app_dashboard -> {
                supportActionBar!!.title = "Home"
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, dashboardFragment).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.menu_app_presensi -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, presensiFragment).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.menu_log_presensi -> {
                isabsen = false
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, riwayatPresensiFragment).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.menu_log_absensi -> {
                isabsen = true
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, riwayatAbsensiFragment).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.menu_aktivitas -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, aktivitasFragment).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.menu_penugasan -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, penugasanFragment).commit()
                frameLayout.visibility = View.VISIBLE
            }
            R.id.menu_logout_test -> {
                authController!!.logout {
                    val intent = Intent(this,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

}