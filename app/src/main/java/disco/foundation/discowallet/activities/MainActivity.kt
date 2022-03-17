package disco.foundation.discowallet.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import disco.foundation.discowallet.R
import disco.foundation.discowallet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup UI
        binding.actionButton.setupAnimation {
            goToReadQR()
        }

        binding.actionButton.buttonText = getString(R.string.camera_qr)
    }

    private fun goToReadQR(){
        val intent = Intent(this, ReadQrCamera::class.java)
        startActivity(intent)
    }

}