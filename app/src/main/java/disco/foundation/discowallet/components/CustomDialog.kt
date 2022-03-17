package disco.foundation.discowallet.components

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import disco.foundation.discowallet.R
import disco.foundation.discowallet.databinding.SampleCustomDialogBinding

class CustomDialog(context: Context) : Dialog(context) {

    private lateinit var binding: SampleCustomDialogBinding
    private var _messageText: String = ""

    private var messageText: String
        get() = _messageText
        set(value) {
            _messageText = value
            setupMessage()
        }

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = SampleCustomDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupMessage(){
        binding.message.text = _messageText
        binding.message.setTextColor(Color.WHITE)
    }

    fun showPopup(message: String, showButton: Boolean, buttonText: String? = null, action: (() -> Unit)? = null){
        super.show()
        val desiredColor = ContextCompat.getColor(context, R.color.neon_pink)
        binding.progressBar.setIndeterminateTintCompat(desiredColor)
        // Popup text message
        messageText = message
        if (showButton){
            initButton(buttonText!!, action!!)
        } else {
            showProgressBar()
        }
    }

    fun update(message: String, showButton: Boolean, buttonText: String? = null, action: (() -> Unit)? = null){
        // Popup text message
        messageText = message
        if (showButton){
            initButton(buttonText!!, action!!)
        } else {
            showProgressBar()
        }
    }

    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
        binding.popupBtn.visibility = View.GONE
    }
    private fun showButton(){
        binding.progressBar.visibility = View.GONE
        binding.popupBtn.visibility = View.VISIBLE
    }

    private fun setupButtonAction(cb: () -> Unit){
        binding.popupBtn.setOnClickListener { cb() }
    }

    private fun setupButtonText(text: String){
        binding.popupBtn.text = text
    }

    private fun initButton(text: String, action: (() -> Unit)){
        showButton()
        setupButtonAction(action)
        setupButtonText(text)
    }

}

// Progress bar extension
fun ProgressBar.setIndeterminateTintCompat(@ColorInt color: Int) {
    indeterminateTintList = ColorStateList.valueOf(color)
}