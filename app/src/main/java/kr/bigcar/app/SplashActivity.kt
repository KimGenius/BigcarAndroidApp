package kr.bigcar.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            Log.d("asdf", "??")
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }, 1500)
    }
}