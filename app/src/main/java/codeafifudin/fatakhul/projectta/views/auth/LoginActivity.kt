package codeafifudin.fatakhul.projectta.views.auth

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.controllers.AuthController
import codeafifudin.fatakhul.projectta.utils.CustomProgress
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var authController : AuthController ?= null
    var auth: Auth? = null

    private val progressDialog = CustomProgress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authController = AuthController(this)
        auth = Auth(this)

        if(Build.VERSION.SDK_INT >= 21){
            val window : Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorBg)
        }

        button_user_login.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        progressDialog.show(this,"Harap tunggu....")
        val username = editText_username_login.text.toString()
        val password = editText_password_login.text.toString()
        authController!!.requestLogin(username,password){
            progressDialog.hide(this)
            if(it){
                Toast.makeText(this,"Selamat data ${username}!",Toast.LENGTH_LONG).show()
                val intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                Toast.makeText(this,"User dengan akun tersebut tidak terdaftar!",Toast.LENGTH_LONG).show()
            }
        }
    }

}