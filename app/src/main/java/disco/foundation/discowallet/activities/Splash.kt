package disco.foundation.discowallet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import disco.foundation.discowallet.R
import disco.foundation.discowallet.utils.TIME_SPLASH_SCREEN

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        exitSplashScreen()
    }

    private fun exitSplashScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, TIME_SPLASH_SCREEN)
    }
}