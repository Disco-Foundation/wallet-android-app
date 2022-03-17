package disco.foundation.discowallet.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import disco.foundation.discowallet.R
import disco.foundation.discowallet.databinding.ActivityPayBinding
import disco.foundation.discowallet.fragments.PayFragment
import disco.foundation.discowallet.utils.DATA_ARG

class PayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = PayFragment.newInstance(intent.getStringExtra(DATA_ARG).toString())
        supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        intent.removeExtra(DATA_ARG)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}